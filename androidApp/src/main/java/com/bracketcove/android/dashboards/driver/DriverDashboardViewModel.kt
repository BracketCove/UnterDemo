package com.bracketcove.android.dashboards.driver

import android.util.Log
import com.bracketcove.ServiceResult
import com.bracketcove.android.google.GoogleService
import com.bracketcove.android.navigation.LoginKey
import com.bracketcove.android.navigation.SplashKey
import com.bracketcove.android.uicommon.ToastMessages
import com.bracketcove.android.uicommon.combineTuple
import com.bracketcove.authorization.UserService
import com.bracketcove.domain.Ride
import com.bracketcove.domain.RideStatus
import com.bracketcove.domain.User
import com.bracketcove.rides.RideService
import com.google.maps.model.LatLng
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.ScopedServices
import com.zhuinden.simplestack.StateChange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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

    private val _passengerModel = MutableStateFlow<User?>(null)
    private val _driverModel = MutableStateFlow<User?>(null)
    private val _rideModel = MutableStateFlow<Ride?>(null)
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
    val uiState = combineTuple(_driverModel, _passengerModel, _rideModel, _mapIsReady).map {
        //only publish state updates whe map is ready!
        val driver = it.first
        val passenger = it.second
        val ride = it.third
        val isMapReady = it.fourth

        if (driver == null || !isMapReady) DriverDashboardUiState.Loading
        else {
            when {
                ride == null -> DriverDashboardUiState.SearchingForPassengers

                passenger != null && ride.status == RideStatus.PASSENGER_PICK_UP.value -> DriverDashboardUiState.PassengerPickUp(
                    passengerLat = passenger.latitude,
                    passengerLon = passenger.longitude,
                    driverLat = driver.latitude,
                    driverLon = driver.longitude,
                    destinationLat = ride.destinationLatitude,
                    destinationLon = ride.destinationLongitude,
                    destinationAddress = ride.destinationAddress,
                    passengerName = passenger.username,
                    passengerAvatar = passenger.avatarPhotoUrl
                )

                passenger != null && ride.status == RideStatus.EN_ROUTE.value -> DriverDashboardUiState.EnRoute(
                    driverLat = driver.latitude,
                    driverLon = driver.longitude,
                    destinationLat = ride.destinationLatitude,
                    destinationLon = ride.destinationLongitude,
                    destinationAddress = ride.destinationAddress,
                    passengerName = passenger.username,
                    passengerAvatar = passenger.avatarPhotoUrl
                )

                passenger != null && ride.status == RideStatus.ARRIVED.value -> DriverDashboardUiState.Arrived(
                    driverLat = driver.latitude,
                    driverLon = driver.longitude,
                    destinationLat = ride.destinationLatitude,
                    destinationLon = ride.destinationLongitude,
                    destinationAddress = ride.destinationAddress,
                    passengerName = passenger.username,
                    passengerAvatar = passenger.avatarPhotoUrl
                )

                else -> {
                    Log.d("ELSE", "${passenger}, ${driver}, ${ride}")
                    DriverDashboardUiState.Error
                }
            }
        }
    }

    //999 represents an impossible value, indicating we don't know the driver's location
    private val DEFAULT_LAT_OR_LON = 999.0
    private val _driverLocation = MutableStateFlow(LatLng(DEFAULT_LAT_OR_LON, DEFAULT_LAT_OR_LON))
    private val _passengerList = MutableStateFlow<List<User>?>(null)

    val locationAwarePassengerList = combineTuple(_driverLocation, _passengerList).map {
        if (it.first.lat == DEFAULT_LAT_OR_LON
            || it.first.lng == DEFAULT_LAT_OR_LON
            || it.second.isNullOrEmpty()

        ) emptyList<Pair<User, String>>()
        else {
            it.second!!.map { user ->
                val passengerLatLng = LatLng(user.latitude, user.longitude)
                val getDistance = googleService.getDistanceBetween(it.first, passengerLatLng)

                Pair(user, getDistance)
            }
        }
    }

    fun updateDriverLocation(latLng: LatLng) = launch(Dispatchers.Main) {
        val updateAttempt = userService.updateUser(
            _passengerModel.value!!.copy(
                latitude = latLng.lat,
                longitude = latLng.lng
            )
        )

        when (updateAttempt) {
            is ServiceResult.Failure -> toastHandler?.invoke(ToastMessages.SERVICE_ERROR)
            is ServiceResult.Success -> {
                if (updateAttempt.value == null) toastHandler?.invoke(ToastMessages.SERVICE_ERROR)
                else{
                    _driverModel.value = updateAttempt.value
                    _driverLocation.value = latLng
                }
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
            is ServiceResult.Success -> {
                if (getUser.value == null) sendToLogin()
                else {
                    getRideIfOneExists(getUser.value!!)
                }
            }
        }
    }

    /**
     * The Passenger model must always be the last model which is mutated from a null state. By
     * setting the other models first, we avoid the UI rapidly switching between different states
     * in a disorganized way.
     */
    private suspend fun getRideIfOneExists(driver: User) {
        val getRide = rideService.getRideIfInProgress()
        when (getRide) {
            is ServiceResult.Failure -> {
                toastHandler?.invoke(ToastMessages.GENERIC_ERROR)
                sendToLogin()
            }
            is ServiceResult.Success -> {
                when {
                    getRide.value == null -> {
                        _driverModel.value = driver
                        getPassengerList()
                    }

                    else -> {
                        //driver is already present
                        getPassenger(driver, getRide.value!!)
                    }
                }
            }
        }
    }

    private suspend fun getPassengerList() {
        val getPassengersList = userService.getPassengersLookingForRide()

        when (getPassengersList) {
            is ServiceResult.Failure -> {
                toastHandler?.invoke(ToastMessages.SERVICE_ERROR)
                sendToLogin()
            }
            is ServiceResult.Success -> {
                _passengerList.value = getPassengersList.value ?: emptyList()
            }
        }
    }

    fun getPassenger(driver: User, ride: Ride) = launch(Dispatchers.Main) {
        val getPassenger = userService.getUserById(ride.passengerId!!)
        when (getPassenger) {
            is ServiceResult.Failure -> {
                toastHandler?.invoke(ToastMessages.GENERIC_ERROR)
                sendToLogin()
            }
            is ServiceResult.Success -> {
                if (getPassenger.value == null) {
                    toastHandler?.invoke(ToastMessages.GENERIC_ERROR)
                    sendToLogin()
                } else {
                    //The order here is important
                    _rideModel.value = ride
                    _passengerModel.value = getPassenger.value
                    _driverModel.value = driver
                }
            }
        }
    }

    fun handlePassengerItemClick(passenger: User) = launch(Dispatchers.Main) {
        val getRideByPassengerId = rideService.getRideByPassengerId(passenger.userId)

        when (getRideByPassengerId) {
            is ServiceResult.Failure -> toastHandler?.invoke(ToastMessages.SERVICE_ERROR)
            is ServiceResult.Success -> {
                _passengerModel.value = passenger
                _rideModel.value = getRideByPassengerId.value
            }
        }
    }

    fun cancelRide() = launch(Dispatchers.Main) {
        val cancelRide = rideService.cancelRide(_rideModel.value!!)
        when (cancelRide) {
            is ServiceResult.Failure -> {
                toastHandler?.invoke(ToastMessages.GENERIC_ERROR)
                sendToSplash()
            }
            is ServiceResult.Success -> {
                sendToSplash()
            }
        }
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
        val completeRide = rideService.completeRide(_rideModel.value!!)
        when (completeRide) {
            is ServiceResult.Failure -> {
                toastHandler?.invoke(ToastMessages.GENERIC_ERROR)
                sendToSplash()
            }
            is ServiceResult.Success -> {
                sendToSplash()
            }
        }
    }

    fun advanceRide() = launch {
        val oldRideState = _rideModel.value!!

        val updateRide = rideService.updateRide(
            oldRideState.copy(
                status = advanceRideState(oldRideState.status)
            )
        )

        when (updateRide) {
            is ServiceResult.Failure -> {
                toastHandler?.invoke(ToastMessages.SERVICE_ERROR)
                sendToSplash()
            }
            is ServiceResult.Success -> {
                _rideModel.value = updateRide.value!!
            }
        }

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