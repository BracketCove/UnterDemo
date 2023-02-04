package com.bracketcove.android.profile.settings

import android.net.Uri
import com.bracketcove.ServiceResult
import com.bracketcove.android.navigation.LoginKey
import com.bracketcove.android.uicommon.ToastMessages
import com.bracketcove.authorization.UserService
import com.bracketcove.domain.User
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.ScopedServices
import com.zhuinden.simplestack.StateChange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ProfileSettingsViewModel(
    private val backstack: Backstack,
    private val userService: UserService
) : ScopedServices.Activated, CoroutineScope {
    internal var toastHandler: ((ToastMessages) -> Unit)? = null

    private val _userModel = MutableStateFlow<User?>(null)
    val userModel: StateFlow<User?> get() = _userModel
    fun handleLogOut() = launch(Dispatchers.Main) {
        val logout = userService.attemptLogout()

        when (logout) {
            is ServiceResult.Failure -> toastHandler?.invoke(ToastMessages.GENERIC_ERROR)
            is ServiceResult.Success -> sendToLogin()
        }
    }

    fun handleDriverDetailEdit() {
        TODO("Not yet implemented")
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
            is ServiceResult.Success -> {
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

    fun handleThumbnailUpdate(imageUri: Uri?) {
        if (imageUri != null) {
            val updateAttempt = userService.attemptUserAvatarUpdate(_userModel.value!!, imageUri.toString())

        } else {
            toastHandler?.invoke(ToastMessages.GENERIC_ERROR)
        }
    }

    private fun updateUser(user: User) {
       val updateAttempt = userService.updateUser(user)

       when (updateAttempt) {
           is ServiceResult.Failure -> toastHandler?.invoke(ToastMessages.SERVICE_ERROR)
           is ServiceResult.Success -> {
               if (updateAttempt.value == null) sendToLogin()
               else _userModel.value = updateAttempt.value
           }
       }
    }

    fun handleToggleUserType() {
        TODO("Not yet implemented")
    }

    private val canceller = Job()

    override val coroutineContext: CoroutineContext
        get() = canceller + Dispatchers.Main
}