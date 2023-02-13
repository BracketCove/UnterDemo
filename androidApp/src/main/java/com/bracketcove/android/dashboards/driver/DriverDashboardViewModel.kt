package com.bracketcove.android.dashboards.driver

import android.util.Log
import com.bracketcove.ServiceResult
import com.bracketcove.android.dashboards.passenger.PassengerDashboardUiState
import com.bracketcove.android.google.GoogleService
import com.bracketcove.android.navigation.LoginKey
import com.bracketcove.android.navigation.SplashKey
import com.bracketcove.android.uicommon.ToastMessages
import com.bracketcove.android.uicommon.combineTuple
import com.bracketcove.authorization.UserService
import com.bracketcove.domain.Ride
import com.bracketcove.domain.RideStatus
import com.bracketcove.domain.UnterUser
import com.bracketcove.rides.RideService
import com.google.maps.model.LatLng
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.ScopedServices
import com.zhuinden.simplestack.StateChange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class DriverDashboardViewModel(
    val backstack: Backstack,
    val userService: UserService,
    val rideService: RideService,
    val googleService: GoogleService
) : ScopedServices.Activated, CoroutineScope {
    internal var toastHandler: ((ToastMessages) -> Unit)? = null

    private val _driverModel = MutableStateFlow<UnterUser?>(null)
    private val _rideModel: Flow<ServiceResult<Ride?>> = rideService.rideFlow()
    private val _mapIsReady = MutableStateFlow(false)

    /*
    Different UI states:
    1. User may never be null
    2. Ride may be null (If User.status is INACTIVE, then no need to try to fetch a ride)
    3. Ride may be not null, and in varying states:
        - SEARCHING_FOR_DRIVER
        - PASSENGER_PICK_UP
        - EN_ROUTE
        - ARRIVED
     */
    val uiState = combineTuple(_driverModel, _rideModel, _mapIsReady).map {
        if (it.second is ServiceResult.Failure) return@map DriverDashboardUiState.Error

        val driver = it.first
        val ride = (it.second as ServiceResult.Value).value
        val isMapReady = it.third

        if (driver == null || !isMapReady) DriverDashboardUiState.Loading
        else {
            when {
                ride == null -> DriverDashboardUiState.SearchingForPassengers

                ride.status == RideStatus.PASSENGER_PICK_UP.value
                        && ride.driverLatitude != null
                        && ride.driverLongitude != null -> DriverDashboardUiState.PassengerPickUp(
                    passengerLat = ride.passengerLatitude,
                    passengerLon = ride.passengerLongitude,
                    driverLat = ride.driverLatitude!!,
                    driverLon = ride.driverLongitude!!,
                    destinationLat = ride.destinationLatitude,
                    destinationLon = ride.destinationLongitude,
                    destinationAddress = ride.destinationAddress,
                    passengerName = ride.passengerName,
                    passengerAvatar = ride.passengerAvatarUrl
                )

                ride.status == RideStatus.EN_ROUTE.value
                        && ride.driverLatitude != null
                        && ride.driverLongitude != null  -> DriverDashboardUiState.EnRoute(
                    driverLat = ride.driverLatitude!!,
                    driverLon = ride.driverLongitude!!,
                    destinationLat = ride.destinationLatitude,
                    destinationLon = ride.destinationLongitude,
                    destinationAddress = ride.destinationAddress,
                    passengerName = ride.passengerName,
                    passengerAvatar = ride.passengerAvatarUrl
                )

                ride.status == RideStatus.ARRIVED.value
                        && ride.driverLatitude != null
                        && ride.driverLongitude != null -> DriverDashboardUiState.Arrived(
                    driverLat = ride.driverLatitude!!,
                    driverLon = ride.driverLongitude!!,
                    destinationLat = ride.destinationLatitude,
                    destinationLon = ride.destinationLongitude,
                    destinationAddress = ride.destinationAddress,
                    passengerName = ride.passengerName,
                    passengerAvatar = ride.passengerAvatarUrl
                )

                else -> {
                    Log.d("ELSE", "${driver}, ${ride}")
                    DriverDashboardUiState.Error
                }
            }
        }
    }

    //999 represents an impossible value, indicating we don't know the driver's location
    private val DEFAULT_LAT_OR_LON = 999.0
    private val _driverLocation = MutableStateFlow(LatLng(DEFAULT_LAT_OR_LON, DEFAULT_LAT_OR_LON))
    private val _passengerList = MutableStateFlow<List<UnterUser>?>(null)

    val locationAwarePassengerList = combineTuple(_driverLocation, _passengerList).map {
        if (it.first.lat == DEFAULT_LAT_OR_LON
            || it.first.lng == DEFAULT_LAT_OR_LON
            || it.second.isNullOrEmpty()

        ) emptyList<Pair<UnterUser, String>>()
        else {
            it.second!!.map { user ->
                //TODO FIXIT
               // val passengerLatLng = LatLng(user.latitude, user.longitude)
              //  val getDistance = googleService.getDistanceBetween(it.first, passengerLatLng)

               // Pair(user, getDistance)
            }
        }
    }

    fun mapIsReady() {
        _mapIsReady.value = true
    }

    fun getDriver() = launch(Dispatchers.Main) {
        val getUser = userService.getUser()
        when (getUser) {
            is ServiceResult.Failure -> {
                toastHandler?.invoke(ToastMessages.GENERIC_ERROR)
                sendToLogin()
            }
            is ServiceResult.Value -> {
                if (getUser.value == null) sendToLogin()
                else {
                    getActiveRideIfItExists(getUser.value!!)
                }
            }
        }
    }

    /**
     * The Passenger model must always be the last model which is mutated from a null state. By
     * setting the other models first, we avoid the UI rapidly switching between different states
     * in a disorganized way.
     */
    private suspend fun getActiveRideIfItExists(user: UnterUser) {
        val result = rideService.getRideIfInProgress()

        when (result) {
            is ServiceResult.Failure -> {
                toastHandler?.invoke(ToastMessages.SERVICE_ERROR)
                sendToLogin()
            }
            is ServiceResult.Value -> {
                //if null, no active ride exists
                if (result.value == null) {
                    _driverModel.value = user
                    getPassengerList()
                }
                else observeRideModel(result.value!!, user)
            }
        }
    }

    private suspend fun observeRideModel(rideId: String, user: UnterUser) {
        //The result of this call is handled inside the flowable assigned to _rideModel
        rideService.observeRideById(rideId)
        _driverModel.value = user
    }

    private suspend fun getPassengerList() {
        val getPassengersList = userService.getPassengersLookingForRide()

        when (getPassengersList) {
            is ServiceResult.Failure -> {
                toastHandler?.invoke(ToastMessages.SERVICE_ERROR)
                sendToLogin()
            }
            is ServiceResult.Value -> {
                _passengerList.value = getPassengersList.value ?: emptyList()
            }
        }
    }
    fun handlePassengerItemClick(passenger: UnterUser) = launch(Dispatchers.Main) {
        val getRideByPassengerId = rideService.getRideByPassengerId(passenger.userId)

        when (getRideByPassengerId) {
            is ServiceResult.Failure -> toastHandler?.invoke(ToastMessages.SERVICE_ERROR)
            is ServiceResult.Value -> {
              //  _passengerModel.value = passenger
              //  _rideModel.value = getRideByPassengerId.value
                //TODO update the ride status and driver details
            }
        }
    }

    fun updateDriverLocation(latLng: LatLng) = launch(Dispatchers.Main) {
//        val updateAttempt = userService.updateUser(
//            _driverModel.value!!.copy(
//                latitude = latLng.lat,
//                longitude = latLng.lng
//            )
//        )
//
//        when (updateAttempt) {
//            is ServiceResult.Failure -> toastHandler?.invoke(ToastMessages.SERVICE_ERROR)
//            is ServiceResult.Value -> {
//                if (updateAttempt.value == null) toastHandler?.invoke(ToastMessages.SERVICE_ERROR)
//                else{
//                    _driverModel.value = updateAttempt.value
//                    _driverLocation.value = latLng
//                }
//            }
//        }
    }

    fun cancelRide() = launch(Dispatchers.Main) {
//        val cancelRide = rideService.cancelRide(_rideModel.value!!)
//        when (cancelRide) {
//            is ServiceResult.Failure -> {
//                toastHandler?.invoke(ToastMessages.GENERIC_ERROR)
//                sendToSplash()
//            }
//            is ServiceResult.Value -> {
//                sendToSplash()
//            }
//        }
    }

    private fun sendToLogin() {
        backstack.setHistory(
            History.of(LoginKey()),
            StateChange.BACKWARD
        )
    }

    private fun sendToSplash() {
        backstack.setHistory(
            History.of(SplashKey()),
            StateChange.REPLACE
        )
    }


    override fun onServiceActive() {
        getDriver()
    }

    override fun onServiceInactive() {
        canceller.cancel()
    }

    fun handleError() {
        sendToLogin()
    }

    fun completeRide() = launch(Dispatchers.Main) {
//        val completeRide = rideService.completeRide(_rideModel.value!!)
//        when (completeRide) {
//            is ServiceResult.Failure -> {
//                toastHandler?.invoke(ToastMessages.GENERIC_ERROR)
//                sendToSplash()
//            }
//            is ServiceResult.Value -> {
//                sendToSplash()
//            }
//        }
    }

    fun advanceRide() = launch {
//        val oldRideState = _rideModel.value!!
//
//        val updateRide = rideService.updateRide(
//            oldRideState.copy(
//                status = advanceRideState(oldRideState.status)
//            )
//        )
//
//        when (updateRide) {
//            is ServiceResult.Failure -> {
//                toastHandler?.invoke(ToastMessages.SERVICE_ERROR)
//                sendToSplash()
//            }
//            is ServiceResult.Value -> {
//               // _rideModel.value = updateRide.value!!
//            }
//        }
    }

    private fun advanceRideState(status: String) : String {
       return when (status) {
            RideStatus.SEARCHING_FOR_DRIVER.value -> RideStatus.PASSENGER_PICK_UP.value
            RideStatus.PASSENGER_PICK_UP.value -> RideStatus.EN_ROUTE.value
            else -> RideStatus.ARRIVED.value
        }
    }

    fun openChat() {

    }

    private val canceller = Job()

    override val coroutineContext: CoroutineContext
        get() = canceller + Dispatchers.Main
}