package com.bracketcove.android.navigation

import androidx.fragment.app.Fragment
import com.bracketcove.android.dashboards.passenger.PassengerDashboardFragment
import com.bracketcove.android.profile.driver.DriverSettingsFragment
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentKey
import kotlinx.parcelize.Parcelize

@Parcelize
data object DriverSettingsKey: DefaultFragmentKey() {
    override fun instantiateFragment(): Fragment = DriverSettingsFragment()
}