package com.bracketcove.android.profile.driver

import com.bracketcove.ServiceResult
import com.bracketcove.android.navigation.LoginKey
import com.bracketcove.android.navigation.ProfileSettingsKey
import com.bracketcove.android.uicommon.ToastMessages
import com.bracketcove.authorization.AuthService
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
    private val authService: AuthService
) : ScopedServices.Activated, CoroutineScope {
    internal var toastHandler: ((ToastMessages) -> Unit)? = null

    private val _userModel = MutableStateFlow<User?>(null)
    val userModel: StateFlow<User?> get() = _userModel
    fun getUser() = launch(Dispatchers.Main) {
        val getUser = authService.getUser()
        when (getUser) {
            is ServiceResult.Failure -> {
                toastHandler?.invoke(ToastMessages.GENERIC_ERROR)
                sendBack()
            }
            is ServiceResult.Success -> {
                if (getUser.value == null) sendBack()
                else _userModel.value = getUser.value
            }
        }
    }
    override fun onServiceActive() {
        getUser()
    }

    fun handleSubmitButton() = launch(Dispatchers.Main) {
        
    }

    private fun sendBack() {
        backstack.setHistory(
            History.of(ProfileSettingsKey()),
            StateChange.BACKWARD
        )
    }

    override fun onServiceInactive() {
        canceller.cancel()
    }

    private val canceller = Job()

    override val coroutineContext: CoroutineContext
        get() = canceller + Dispatchers.Main
}