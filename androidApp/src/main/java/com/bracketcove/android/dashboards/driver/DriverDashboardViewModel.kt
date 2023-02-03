package com.bracketcove.android.dashboards.driver

import com.zhuinden.simplestack.ScopedServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

class DriverDashboardViewModel : ScopedServices.Activated, CoroutineScope {
    override fun onServiceActive() {
        TODO("Not yet implemented")
    }

    override fun onServiceInactive() {
        canceller.cancel()
    }

    private val canceller = Job()

    override val coroutineContext: CoroutineContext
        get() = canceller + Dispatchers.Main
}