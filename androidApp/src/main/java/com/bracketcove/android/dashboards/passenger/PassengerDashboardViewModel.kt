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
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


class PassengerDashboardViewModel(
    val backstack: Backstack,
    val userService: UserService,
    val rideService: RideService,
    val googleService: GoogleService
) : ScopedServices.Activated, CoroutineScope {
    internal var toastHandler: ((ToastMessages) -> Unit)? = null

    private var _passengerModel = MutableStateFlow<UnterUser?> (null)
    private var _rideModel: StateFlow<ServiceResult<Ride?>> = MutableStateFlow<ServiceResult<Ride?>>(ServiceResult.Value(null))
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
    val uiState = combineTuple(_passengerModel, _rideModel, _mapIsReady).map {
        if (it.second is ServiceResult.Failure) return@map PassengerDashboardUiState.Error

        val passenger = it.first
        val ride = (it.second as ServiceResult.Value).value
        val isMapReady = it.third

        //only publish state updates whe map is ready!
        if (passenger == null || !isMapReady) PassengerDashboardUiState.Loading
        else {
            when {
                ride == null -> PassengerDashboardUiState.RideInactive

                ride.driverId == null -> PassengerDashboardUiState.SearchingForDriver(
                    ride.passengerLatitude,
                    ride.passengerLongitude,
                    ride.destinationAddress
                )

                ride.status == RideStatus.PASSENGER_PICK_UP.value
                        && ride.driverLatitude != null
                        && ride.driverLongitude != null -> PassengerDashboardUiState.PassengerPickUp(
                    passengerLat = ride.passengerLatitude,
                    passengerLon = ride.passengerLongitude,
                    driverLat = ride.driverLatitude!!,
                    driverLon = ride.driverLongitude!!,
                    destinationLat = ride.destinationLatitude,
                    destinationLon = ride.destinationLongitude,
                    destinationAddress = ride.destinationAddress,
                    driverName = ride.driverName ?: "Error",
                    driverAvatar = ride.driverAvatarUrl ?: ""
                )

                ride.status == RideStatus.EN_ROUTE.value
                        && ride.driverLatitude != null
                        && ride.driverLongitude != null -> PassengerDashboardUiState.EnRoute(
                    passengerLat = ride.passengerLatitude,
                    passengerLon = ride.passengerLongitude,
                    driverName = ride.driverName ?: "Error",
                    destinationAddress = ride.destinationAddress,
                    destinationLat = ride.destinationLatitude,
                    destinationLon = ride.destinationLongitude,
                    driverAvatar = ride.driverAvatarUrl ?: ""
                )

                ride.status == RideStatus.ARRIVED.value
                        && ride.driverLatitude != null
                        && ride.driverLongitude != null-> PassengerDashboardUiState.Arrived(
                    passengerLat = ride.passengerLatitude,
                    passengerLon = ride.passengerLongitude,
                    driverName = ride.driverName ?: "Error",
                    destinationLat = ride.destinationLatitude,
                    destinationLon = ride.destinationLongitude,
                    destinationAddress = ride.destinationAddress,
                    driverAvatar = ride.driverAvatarUrl ?: ""
                )

                else -> {
                    Log.d("ELSE", "${passenger}, ${ride}")
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

    private fun getPassenger() = launch(Dispatchers.Main) {
        val getUser = userService.getUser()
        when (getUser) {
            is ServiceResult.Failure -> {
                toastHandler?.invoke(ToastMessages.GENERIC_ERROR)
                sendToLogin()
            }
            is ServiceResult.Value -> {
                if (getUser.value == null) sendToLogin()
                else {
                    observeRideModel(getUser.value!!)
                }
            }
        }
    }

    /**
     * The Passenger model must always be the last model which is mutated from a null state. By
     * setting the other models first, we avoid the UI rapidly switching between different states
     * in a disorganized way.
     */
    private suspend fun observeRideModel(passenger: UnterUser) {
        _rideModel = rideService.getRideIfInProgress().stateIn(this)
        _passengerModel.value = passenger
        //passenger model is kept null until ride model is set to avoid state issues
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
        _rideModel = rideService.createRide(
            passengerId = _passengerModel.value!!.userId,
            latitude = response.place.latLng!!.latitude,
            longitude = response.place.latLng!!.longitude,
            destinationAddress = address,
            avatarUrl = _passengerModel.value!!.avatarPhotoUrl
        ).stateIn(this)
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
        //we might not need to do send user to splash
        val model = _rideModel.value
        if (model is ServiceResult.Value && model.value != null) {
            val cancelRide = rideService.cancelRide(model.value!!)
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

    fun completeRide() {
        sendToSplash()
    }

    fun updatePassengerLocation(latLng: LatLng) = launch(Dispatchers.Main) {
        val model = _rideModel.value
        if (model is ServiceResult.Value && model.value != null) {
            rideService.updateRide(
                model.value!!.copy(
                    passengerLatitude = latLng.lat,
                    passengerLongitude = latLng.lng
                )
            )
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