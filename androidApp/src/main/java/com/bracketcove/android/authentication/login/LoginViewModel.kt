package com.bracketcove.android.authentication.login

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.bracketcove.ServiceResult
import com.bracketcove.android.navigation.DriverDashboardKey
import com.bracketcove.android.navigation.PassengerDashboardKey
import com.bracketcove.android.navigation.SignUpKey
import com.bracketcove.android.uicommon.ToastMessages
import com.bracketcove.authorization.AuthService
import com.bracketcove.authorization.LogInResult
import com.bracketcove.isValidPhoneNumber
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.ScopedServices
import com.zhuinden.simplestack.StateChange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


class LoginViewModel(
    private val backstack: Backstack,
    private val authService: AuthService
) : ScopedServices.Activated, CoroutineScope {
    internal var toastHandler: ((ToastMessages) -> Unit)? = null

    var mobileNumber by mutableStateOf("")
        private set
    fun updateMobileNumber(input: String) {
        mobileNumber = input
    }

    fun handleLogin() = launch(Dispatchers.Main) {
        val loginAttempt = authService.attemptLogin(mobileNumber)
        when (loginAttempt) {
            is ServiceResult.Failure -> toastHandler?.invoke(ToastMessages.SERVICE_ERROR)
            is ServiceResult.Success -> {
                when (loginAttempt.value) {
                    LogInResult.SUCCESS -> sendToDashboard()
                    LogInResult.INVALID_CREDENTIALS -> toastHandler?.invoke(ToastMessages.INVALID_CREDENTIALS)
                }
            }
        }
    }

    private suspend fun sendToDashboard() {
        val getUser = authService.getUser()

        when {
            getUser is ServiceResult.Success && getUser.value != null -> {
                when (getUser.value!!.type) {
                    "PASSENGER" -> backstack.setHistory(
                        History.of(PassengerDashboardKey()),
                        //Direction of navigation which is used for animation
                        StateChange.FORWARD
                    )
                    "DRIVER" -> backstack.setHistory(
                        History.of((DriverDashboardKey())),
                        //Direction of navigation which is used for animation
                        StateChange.FORWARD
                    )
                }
            }

            else -> toastHandler?.invoke(ToastMessages.SERVICE_ERROR)
        }
    }

    fun goToSignup() {
        backstack.setHistory(
            History.of(SignUpKey()),
            StateChange.FORWARD
        )
    }

    override fun onServiceActive() = Unit

    override fun onServiceInactive() {
        canceller.cancel()
        toastHandler = null
    }

    private val canceller = Job()

    override val coroutineContext: CoroutineContext
        get() = canceller + Dispatchers.Main
}