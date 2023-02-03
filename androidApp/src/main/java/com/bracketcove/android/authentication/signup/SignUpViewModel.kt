package com.bracketcove.android.authentication.signup

import com.zhuinden.simplestack.ScopedServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

class SignUpViewModel : ScopedServices.Activated, CoroutineScope {
    // TODO: Implement the ViewModel
    override fun onServiceActive() {
        TODO("Not yet implemented")
    }

    fun handleSignUp(textFieldValue: String) {
        TODO("Not yet implemented")
    }

    override fun onServiceInactive() {
        canceller.cancel()
    }

    private val canceller = Job()

    override val coroutineContext: CoroutineContext
        get() = canceller + Dispatchers.Main
}