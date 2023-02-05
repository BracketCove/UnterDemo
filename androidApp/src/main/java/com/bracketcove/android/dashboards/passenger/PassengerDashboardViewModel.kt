package com.bracketcove.android.dashboards.passenger

import com.bracketcove.ServiceResult
import com.bracketcove.android.navigation.LoginKey
import com.bracketcove.android.uicommon.ToastMessages
import com.bracketcove.android.uicommon.combineTuple
import com.bracketcove.authorization.UserService
import com.bracketcove.domain.Ride
import com.bracketcove.domain.RideStatus
import com.bracketcove.domain.User
import com.bracketcove.rides.RideService
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
    val rideService: RideService
) : ScopedServices.Activated, CoroutineScope {
    internal var toastHandler: ((ToastMessages) -> Unit)? = null

    private val _passengerModel = MutableStateFlow<User?>(null)
    private val _driverModel = MutableStateFlow<User?>(null)
    private val _rideModel = MutableStateFlow<Ride?>(null)

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
    val uiState = combineTuple(_passengerModel, _driverModel, _rideModel).map {
        if (_passengerModel.value == null) PassengerDashboardUiState.Loading
        else {
            val passenger = it.first
            val driver = it.second
            val ride = it.third

            when {
                passenger == null -> PassengerDashboardUiState.Error

                ride == null -> PassengerDashboardUiState.RideInactive(
                    passenger.latitude, passenger.longitude
                )

                driver != null && ride.driverId == null -> PassengerDashboardUiState.SearchingForDriver(
                    ride.destinationLatitude, ride.destinationLongitude
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

                else -> PassengerDashboardUiState.Error
            }
        }
    }

    private val _autoCompleteList = MutableStateFlow<List<String>>(emptyList())
    val autoCompleteList: StateFlow<List<String>> get() = _autoCompleteList

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
                when  {
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

    private fun sendToLogin() {
        backstack.setHistory(
            History.of(LoginKey()),
            StateChange.BACKWARD
        )
    }

    override fun onServiceActive() {
        getPassenger()
    }

    override fun onServiceInactive() {
        canceller.cancel()
    }

    private val canceller = Job()

    override val coroutineContext: CoroutineContext
        get() = canceller + Dispatchers.Main
}