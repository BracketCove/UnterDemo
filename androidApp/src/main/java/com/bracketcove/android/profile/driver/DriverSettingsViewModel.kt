package com.bracketcove.android.profile.driver

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.bracketcove.ServiceResult
import com.bracketcove.android.navigation.LoginKey
import com.bracketcove.android.navigation.ProfileSettingsKey
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

class DriverSettingsViewModel(
    private val backstack: Backstack,
    private val userService: UserService
) : ScopedServices.Activated, CoroutineScope {
    internal var toastHandler: ((ToastMessages) -> Unit)? = null

    private val _userModel = MutableStateFlow<User?>(null)
    val userModel: StateFlow<User?> get() = _userModel

    fun updateVehicleDescription(input: String) {
        _userModel.value = _userModel.value!!.copy(
            vehicleDescription = input
        )
    }

    private val _vehiclePhotoUrl = MutableStateFlow<String?>(null)
    val vehiclePhotoUrl: StateFlow<String?> get() = _vehiclePhotoUrl

//    var vehiclePhotoUrl by mutableStateOf("")
//        private set

    fun handleThumbnailUpdate(imageUri: Uri?) {
        if (imageUri != null) {
            val updateAttempt =
                userService.attemptUserAvatarUpdate(_userModel.value!!, imageUri.toString())

            when (updateAttempt) {
                is ServiceResult.Failure -> toastHandler?.invoke(ToastMessages.SERVICE_ERROR)

                is ServiceResult.Success -> {
                    _vehiclePhotoUrl.value = imageUri.toString()
                    toastHandler?.invoke(ToastMessages.UPDATE_SUCCESSFUL)
                }
            }
        } else {
            toastHandler?.invoke(ToastMessages.GENERIC_ERROR)
        }
    }

    fun getUser() = launch(Dispatchers.Main) {
        val getUser = userService.getUser()
        when (getUser) {
            is ServiceResult.Failure -> {
                toastHandler?.invoke(ToastMessages.GENERIC_ERROR)
                sendBack()
            }
            is ServiceResult.Success -> {
                if (getUser.value == null) sendBack()
                else {
                    _userModel.value = getUser.value
                    _vehiclePhotoUrl.value = getUser.value!!.vehiclePhotoUrl!!
                }
            }
        }
    }

    override fun onServiceActive() {
        getUser()
    }

    fun handleSubmitButton() = launch(Dispatchers.Main) {
        val updateAttempt = userService.updateUser(
            _userModel.value!!
        )

        when (updateAttempt) {
            is ServiceResult.Failure -> toastHandler?.invoke(ToastMessages.SERVICE_ERROR)
            is ServiceResult.Success -> {
                if (updateAttempt.value == null) sendToLogin()
                else sendBack()
            }
        }
    }

    private fun sendBack() {
        backstack.setHistory(
            History.of(ProfileSettingsKey()),
            StateChange.BACKWARD
        )
    }

    private fun sendToLogin() {
        backstack.setHistory(
            History.of(LoginKey()),
            StateChange.REPLACE
        )
    }


    override fun onServiceInactive() {
        canceller.cancel()
    }

    fun handleCancelPress() {
        backstack.setHistory(
            History.of(ProfileSettingsKey()),
            StateChange.BACKWARD
        )
    }

    private val canceller = Job()

    override val coroutineContext: CoroutineContext
        get() = canceller + Dispatchers.Main
}