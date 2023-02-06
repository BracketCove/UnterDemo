package com.bracketcove.android.dashboards.passenger

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
import com.google.android.libraries.places.api.net.FetchPlaceResponse
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
    val uiState = combineTuple(_passengerModel, _driverModel, _rideModel, _mapIsReady).map {
        //only publish state updates whe map is ready!
        if (_passengerModel.value == null || !_mapIsReady.value) PassengerDashboardUiState.Loading
        else {
            val passenger = it.first
            val driver = it.second
            val ride = it.third

            when {
                passenger == null -> {
                    Log.d(
                        "PASSENGER",
                        "${passenger.toString()}, ${driver.toString()}, ${ride.toString()}"
                    )
                    PassengerDashboardUiState.Error
                }

                ride == null -> PassengerDashboardUiState.RideInactive

                ride.driverId == null -> PassengerDashboardUiState.SearchingForDriver(
                    passenger.latitude, passenger.longitude
                )

                driver != null && ride.status == RideStatus.PASSENGER_PICK_UP.value -> PassengerDashboardUiState.PassengerPickUp(
                    passengerLat = passenger.latitude,
                    passengerLon = passenger.longitude,
                    driverLat = driver.latitude,
                    driverLon = driver.longitude,
                    destinationLat = ride.destinationLatitude,
                    destinationLon = ride.destinationLongitude,
                    driverName = driver.username,
                    vehicleDescription = driver.vehicleDescription ?: "",
                    vehicleAvatar = driver.avatarPhotoUrl,
                )

                driver != null && ride.status == RideStatus.EN_ROUTE.value -> PassengerDashboardUiState.EnRoute(
                    passengerLat = passenger.latitude,
                    passengerLon = passenger.longitude,
                    driverName = driver.username,
                    vehicleDescription = driver.vehicleDescription ?: "",
                    vehicleAvatar = driver.avatarPhotoUrl,
                    destinationLat = ride.destinationLatitude,
                    destinationLon = ride.destinationLongitude
                )

                driver != null && ride.status == RideStatus.ARRIVED.value -> PassengerDashboardUiState.Arrived(
                    passengerLat = passenger.latitude,
                    passengerLon = passenger.longitude,
                    driverName = driver.username,
                    vehicleDescription = driver.vehicleDescription ?: "",
                    vehicleAvatar = driver.avatarPhotoUrl,
                    destinationLat = ride.destinationLatitude,
                    destinationLon = ride.destinationLongitude
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
    private suspend fun getRideIfOneExists(passenger: User) {
        val getRide = rideService.getRideIfInProgress()
        when (getRide) {
            is ServiceResult.Failure -> {
                toastHandler?.invoke(ToastMessages.GENERIC_ERROR)
                sendToLogin()
            }
            is ServiceResult.Success -> {
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

    fun getDriver(passenger: User, ride: Ride) = launch(Dispatchers.Main) {
        val getDriver = userService.getUserById(ride.driverId!!)
        when (getDriver) {
            is ServiceResult.Failure -> {
                toastHandler?.invoke(ToastMessages.GENERIC_ERROR)
                sendToLogin()
            }
            is ServiceResult.Success -> {
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
            is ServiceResult.Success -> {
                if (getCoordinates.value != null &&
                    getCoordinates.value!!.place.latLng != null
                ) {
                    attemptToCreateNewRide(getCoordinates.value!!, selectedPlace.address)
                }
                else toastHandler?.invoke(ToastMessages.UNABLE_TO_RETRIEVE_COORDINATES)
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
            is ServiceResult.Success -> {
                _rideModel.value = createRide.value
                //Note: what to do about user state?
            }
        }
    }

    fun requestAutocompleteResults(query: String) = launch(Dispatchers.Main) {
        val autocompleteRequest = googleService.getAutocompleteResults(query)
        when (autocompleteRequest) {
            is ServiceResult.Failure -> {
                toastHandler?.invoke(ToastMessages.SERVICE_ERROR)
            }
            is ServiceResult.Success -> {
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
        getPassenger()
    }

    override fun onServiceInactive() {
        canceller.cancel()
    }

    fun handleError() {
        sendToLogin()
    }


    private val canceller = Job()

    override val coroutineContext: CoroutineContext
        get() = canceller + Dispatchers.Main
}