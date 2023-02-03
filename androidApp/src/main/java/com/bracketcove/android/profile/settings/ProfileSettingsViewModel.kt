package com.bracketcove.android.profile.settings

import com.zhuinden.simplestack.ScopedServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

class ProfileSettingsViewModel : ScopedServices.Activated, CoroutineScope {
    // TODO: Implement the ViewModel
    override fun onServiceActive() {
        TODO("Not yet implemented")
    }

    fun handleLogOut() {
        TODO("Not yet implemented")
    }

    fun handleDriverDetailEdit() {
        TODO("Not yet implemented")
    }

    fun isUserRegistered(): Boolean {
        return false
    }

    override fun onServiceInactive() {
        canceller.cancel()
    }

    private val canceller = Job()

    override val coroutineContext: CoroutineContext
        get() = canceller + Dispatchers.Main
}