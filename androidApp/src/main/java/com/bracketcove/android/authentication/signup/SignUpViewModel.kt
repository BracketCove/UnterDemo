package com.bracketcove.android.authentication.signup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.bracketcove.ServiceResult
import com.bracketcove.android.navigation.LoginKey
import com.bracketcove.android.navigation.PassengerDashboardKey
import com.bracketcove.android.uicommon.ToastMessages
import com.bracketcove.authorization.UserService
import com.bracketcove.authorization.SignUpResult
import com.bracketcove.authorization.SignUpUser
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.ScopedServices
import com.zhuinden.simplestack.StateChange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class SignUpViewModel(
    private val backstack: Backstack,
    private val signUp: SignUpUser,
) : ScopedServices.Activated, CoroutineScope {
    internal var toastHandler: ((ToastMessages) -> Unit)? = null

    var mobileNumber by mutableStateOf("")
        private set

    fun updateMobileNumber(input: String) {
        mobileNumber = input
    }

    var name by mutableStateOf("")
        private set

    fun updateName(input: String) {
        name = input
    }

    var password by mutableStateOf("")
        private set

    fun updatePassword(input: String) {
        password = input
    }


    fun handleSignUp() = launch(Dispatchers.Main) {
        val signupAttempt = signUp.signUpUser(mobileNumber, name, password)
        when (signupAttempt) {
            is ServiceResult.Failure -> toastHandler?.invoke(ToastMessages.SERVICE_ERROR)
            is ServiceResult.Value -> {
                when (signupAttempt.value) {
                    is SignUpResult.Success -> {
                        backstack.setHistory(
                            History.of(PassengerDashboardKey()),
                            //Direction of navigation which is used for animation
                            StateChange.FORWARD
                        )
                    }
                    SignUpResult.InvalidCredentials -> toastHandler?.invoke(ToastMessages.INVALID_CREDENTIALS)
                    SignUpResult.AlreadySignedUp -> toastHandler?.invoke(ToastMessages.ACCOUNT_EXISTS)
                }
            }
        }
    }

    fun handleBackPress() {
        backstack.setHistory(
            History.of(LoginKey()),
            //Direction of navigation which is used for animation
            StateChange.BACKWARD
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