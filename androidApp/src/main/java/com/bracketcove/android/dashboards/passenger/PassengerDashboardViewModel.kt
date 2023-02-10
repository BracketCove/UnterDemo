package com.bracketcove.android.dashboards.passenger

import android.util.Log
import com.bracketcove.ServiceResult
import com.bracketcove.android.google.GoogleService
import com.bracketcove.android.navigation.LoginKey
import com.bracketcove.android.navigation.ProfileSettingsKey
import com.bracketcove.android.navigation.SplashKey
import com.bracketcove.android.uicommon.ToastMessages
import com.bracketcove.android.uicommon.combineTuple
import com.bracketcove.authorization.UserService
import com.bracketcove.domain.Ride
import com.bracketcove.domain.RideStatus
import com.bracketcove.domain.UnterUser
import com.bracketcove.rides.RideService
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.maps.model.LatLng
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.ScopedServices
import com.zhuinden.simplestack.StateChange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


class PassengerDashboardViewModel(
    val backstack: Backstack,
    val userService: UserService,
    val rideService: RideService,
    val googleService: GoogleService
) : ScopedServices.Activated, CoroutineScope {
    internal var toastHandler: ((ToastMessages) -> Unit)? = null

    private val _passengerModel = MutableStateFlow<UnterUser?>(null)
    private val _driverModel = MutableStateFlow<UnterUser?>(null)
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
    val uiState = combineTuple(_passengerModel, _driverModel, _rideModel, _mapIsReady).map {
        val passenger = it.first
        val driver = it.second
        val ride = it.third
        val isMapReady = it.fourth

        //only publish state updates whe map is ready!
        if (passenger == null || !isMapReady) PassengerDashboardUiState.Loading
        else {

            when {
                ride == null -> PassengerDashboardUiState.RideInactive

                ride.driverId == null -> PassengerDashboardUiState.SearchingForDriver(
                    passenger.latitude, passenger.longitude, ride.destinationAddress
                )

                driver != null && ride.status == RideStatus.PASSENGER_PICK_UP.value -> PassengerDashboardUiState.PassengerPickUp(
                    passengerLat = passenger.latitude,
                    passengerLon = passenger.longitude,
                    driverLat = driver.latitude,
                    driverLon = driver.longitude,
                    destinationLat = ride.destinationLatitude,
                    destinationLon = ride.destinationLongitude,
                    destinationAddress = ride.destinationAddress,
                    driverName = driver.username,
                    driverAvatar = driver.avatarPhotoUrl
                )

                driver != null && ride.status == RideStatus.EN_ROUTE.value -> PassengerDashboardUiState.EnRoute(
                    passengerLat = passenger.latitude,
                    passengerLon = passenger.longitude,
                    driverName = driver.username,
                    destinationAddress = ride.destinationAddress,
                    destinationLat = ride.destinationLatitude,
                    destinationLon = ride.destinationLongitude,
                    driverAvatar = driver.avatarPhotoUrl
                )

                driver != null && ride.status == RideStatus.ARRIVED.value -> PassengerDashboardUiState.Arrived(
                    passengerLat = passenger.latitude,
                    passengerLon = passenger.longitude,
                    driverName = driver.username,
                    destinationLat = ride.destinationLatitude,
                    destinationLon = ride.destinationLongitude,
                    destinationAddress = ride.destinationAddress,
                    driverAvatar = driver.avatarPhotoUrl
                )

                else -> {
                    Log.d("ELSE", "${passenger}, ${driver}, ${ride}")
                    PassengerDashboardUiState.Error
                }
            }
        }
    }

    private val _autoCompleteList = MutableStateFlow<List<AutoCompleteModel>>(emptyList())
    val autoCompleteList: StateFlow<List<AutoCompleteModel>> get() = _autoCompleteList

    fun mapIsReady() {
        _mapIsReady.value = true
    }

    fun getPassenger() = launch(Dispatchers.Main) {
        goToProfile()
//        val getUser = userService.getUser()
//        when (getUser) {
//            is ServiceResult.Failure -> {
//                toastHandler?.invoke(ToastMessages.GENERIC_ERROR)
//                sendToLogin()
//            }
//            is ServiceResult.Value -> {
//                if (getUser.value == null) sendToLogin()
//                else {
//                    getRideIfOneExists(getUser.value!!)
//                }
//            }
//        }
    }

    /**
     * The Passenger model must always be the last model which is mutated from a null state. By
     * setting the other models first, we avoid the UI rapidly switching between different states
     * in a disorganized way.
     */
    private suspend fun getRideIfOneExists(passenger: UnterUser) {
        val getRide = rideService.getRideIfInProgress()
        when (getRide) {
            is ServiceResult.Failure -> {
                toastHandler?.invoke(ToastMessages.GENERIC_ERROR)
                sendToLogin()
            }
            is ServiceResult.Value -> {
                when {
                    getRide.value == null -> {
                        _passengerModel.value = passenger
                    }

                    getRide.value!!.driverId == null -> {
                        _rideModel.value = getRide.value
                        _passengerModel.value = passenger
                    }
                    else -> {
                        //driver is already present
                        getDriver(passenger, getRide.value!!)
                    }
                }
            }
        }
    }

    fun getDriver(passenger: UnterUser, ride: Ride) = launch(Dispatchers.Main) {
        val getDriver = userService.getUserById(ride.driverId!!)
        when (getDriver) {
            is ServiceResult.Failure -> {
                toastHandler?.invoke(ToastMessages.GENERIC_ERROR)
                sendToLogin()
            }
            is ServiceResult.Value -> {
                if (getDriver.value == null) {
                    toastHandler?.invoke(ToastMessages.GENERIC_ERROR)
                    sendToLogin()
                } else {
                    //The order here is important
                    _driverModel.value = getDriver.value
                    _rideModel.value = ride
                    _passengerModel.value = passenger
                }
            }
        }
    }

    fun handleSearchItemClick(selectedPlace: AutoCompleteModel) = launch(Dispatchers.Main) {
        val getCoordinates = googleService.getPlaceCoordinates(selectedPlace.prediction.placeId)

        when (getCoordinates) {
            is ServiceResult.Failure -> toastHandler?.invoke(ToastMessages.SERVICE_ERROR)
            is ServiceResult.Value -> {
                if (getCoordinates.value != null &&
                    getCoordinates.value!!.place.latLng != null
                ) {
                    attemptToCreateNewRide(getCoordinates.value!!, selectedPlace.address)
                } else toastHandler?.invoke(ToastMessages.UNABLE_TO_RETRIEVE_COORDINATES)
            }
        }
    }

    private suspend fun attemptToCreateNewRide(response: FetchPlaceResponse, address: String) {
        val createRide = rideService.createRide(
            passengerId = _passengerModel.value!!.userId,
            latitude = response.place.latLng!!.latitude,
            longitude = response.place.latLng!!.longitude,
            address = address
        )

        when (createRide) {
            is ServiceResult.Failure -> toastHandler?.invoke(ToastMessages.SERVICE_ERROR)
            is ServiceResult.Value -> {
                _rideModel.value = createRide.value
            }
        }
    }

    fun requestAutocompleteResults(query: String) = launch(Dispatchers.Main) {
        val autocompleteRequest = googleService.getAutocompleteResults(query)
        when (autocompleteRequest) {
            is ServiceResult.Failure -> {
                toastHandler?.invoke(ToastMessages.SERVICE_ERROR)
            }
            is ServiceResult.Value -> {
                _autoCompleteList.value = autocompleteRequest.value.map { prediction ->
                    AutoCompleteModel(
                        address = prediction.getFullText(null).toString(),
                        prediction = prediction
                    )
                }
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
            is ServiceResult.Value -> {
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
        getPassenger()
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
            is ServiceResult.Value -> {
                sendToSplash()
            }
        }
    }

    fun updatePassengerLocation(latLng: LatLng) = launch (Dispatchers.Main){
        val updateAttempt = userService.updateUser(
            _passengerModel.value!!.copy(
                latitude = latLng.lat,
                longitude = latLng.lng
            )
        )

        when (updateAttempt) {
            is ServiceResult.Failure -> toastHandler?.invoke(ToastMessages.SERVICE_ERROR)
            is ServiceResult.Value -> {
                if (updateAttempt.value == null) toastHandler?.invoke(ToastMessages.SERVICE_ERROR)
                else _passengerModel.value = updateAttempt.value
            }
        }
    }

    fun openChat() {
            TODO("Not yet implemented")
    }

    fun goToProfile() {
        backstack.setHistory(
            History.of(ProfileSettingsKey()),
            StateChange.REPLACE
        )
    }

    private val canceller = Job()

    override val coroutineContext: CoroutineContext
        get() = canceller + Dispatchers.Main
}