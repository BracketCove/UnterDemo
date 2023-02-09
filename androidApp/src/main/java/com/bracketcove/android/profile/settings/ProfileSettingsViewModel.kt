package com.bracketcove.android.profile.settings

import android.net.Uri
import com.bracketcove.ServiceResult
import com.bracketcove.android.navigation.DriverDashboardKey
import com.bracketcove.android.navigation.DriverSettingsKey
import com.bracketcove.android.navigation.LoginKey
import com.bracketcove.android.navigation.PassengerDashboardKey
import com.bracketcove.android.uicommon.ToastMessages
import com.bracketcove.authorization.AuthorizationService
import com.bracketcove.authorization.FirebaseAuthService
import com.bracketcove.authorization.UserService
import com.bracketcove.domain.User
import com.bracketcove.domain.UserType
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.ScopedServices
import com.zhuinden.simplestack.StateChange
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.coroutines.CoroutineContext

class ProfileSettingsViewModel(
    private val backstack: Backstack,
    private val userService: UserService,
    private val authService: AuthorizationService
) : ScopedServices.Activated, CoroutineScope {
    internal var toastHandler: ((ToastMessages) -> Unit)? = null

    private val _userModel = MutableStateFlow<User?>(null)
    val userModel: StateFlow<User?> get() = _userModel
    fun handleLogOut() = launch(Dispatchers.Main) {
        val logout = authService.logout()

        when (logout) {
            is ServiceResult.Failure -> toastHandler?.invoke(ToastMessages.GENERIC_ERROR)
            is ServiceResult.Value -> sendToLogin()
        }
    }

    fun handleDriverDetailEdit() {
        backstack.setHistory(
            History.of(DriverSettingsKey()),
            StateChange.FORWARD
        )
    }

    fun isUserRegistered(): Boolean {
        return false
    }

    fun getUser() = launch(Dispatchers.Main) {
        val getUser = userService.getUser()
        when (getUser) {
            is ServiceResult.Failure -> {
                toastHandler?.invoke(ToastMessages.GENERIC_ERROR)
                sendToLogin()
            }
            is ServiceResult.Value -> {
                if (getUser.value == null) sendToLogin()
                else _userModel.value = getUser.value
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
        getUser()
    }

    override fun onServiceInactive() {
        canceller.cancel()
        toastHandler = null
    }

    fun handleThumbnailUpdate(imageUri: Uri?) = launch(Dispatchers.Main) {
        if (imageUri != null) {
            val updateAttempt =
                userService.attemptUserAvatarUpdate(_userModel.value!!, imageUri.toString())

            when (updateAttempt) {
                is ServiceResult.Failure -> toastHandler?.invoke(ToastMessages.SERVICE_ERROR)

                is ServiceResult.Value -> {
                    toastHandler?.invoke(ToastMessages.UPDATE_SUCCESSFUL)
                }
            }
        } else {
            toastHandler?.invoke(ToastMessages.GENERIC_ERROR)
        }
    }

    private suspend fun updateUser(user: User) {
        val updateAttempt = userService.updateUser(user)

        when (updateAttempt) {
            is ServiceResult.Failure -> toastHandler?.invoke(ToastMessages.SERVICE_ERROR)
            is ServiceResult.Value -> {
                if (updateAttempt.value == null) sendToLogin()
                else _userModel.value = updateAttempt.value
            }
        }
    }

    fun handleToggleUserType() = launch(Dispatchers.Main) {
        val oldModel = _userModel.value!!
        val newType = flipType(oldModel.type)

        updateUser(oldModel.copy(type = newType))
    }

    private fun flipType(oldType: String): String {
        return if (oldType == UserType.PASSENGER.value) UserType.DRIVER.value
        else UserType.PASSENGER.value
    }

    fun handleBackPress() {
        when (_userModel.value!!.type) {
            UserType.PASSENGER.value -> backstack.setHistory(
                History.of(PassengerDashboardKey()),
                //Direction of navigation which is used for animation
                StateChange.BACKWARD
            )
            UserType.DRIVER.value -> backstack.setHistory(
                History.of(DriverDashboardKey()),
                //Direction of navigation which is used for animation
                StateChange.BACKWARD
            )
        }
    }

    private val canceller = Job()

    override val coroutineContext: CoroutineContext
        get() = canceller + Dispatchers.Main
}