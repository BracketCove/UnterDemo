package com.bracketcove.android.navigation

import androidx.fragment.app.Fragment
import com.bracketcove.android.dashboards.driver.DriverDashboardFragment
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentKey
import kotlinx.parcelize.Parcelize

@Parcelize
data class DriverDashboardKey(private val noArgsPlaceholder: String = ""): DefaultFragmentKey() {
    override fun instantiateFragment(): Fragment = DriverDashboardFragment()
}