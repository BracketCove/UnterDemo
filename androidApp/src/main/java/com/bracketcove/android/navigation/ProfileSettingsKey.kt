package com.bracketcove.android.navigation

import androidx.fragment.app.Fragment
import com.bracketcove.android.dashboards.driver.DriverDashboardFragment
import com.bracketcove.android.profile.settings.ProfileSettingsFragment
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentKey
import kotlinx.parcelize.Parcelize

@Parcelize
data object ProfileSettingsKey: DefaultFragmentKey() {
    override fun instantiateFragment(): Fragment = ProfileSettingsFragment()
}