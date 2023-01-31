package com.bracketcove.android.navigation

import androidx.fragment.app.Fragment
import com.bracketcove.android.dashboards.passenger.PassengerDashboardFragment
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentKey
import kotlinx.parcelize.Parcelize

@Parcelize
data object PassengerDashboardKey: DefaultFragmentKey() {
    override fun instantiateFragment(): Fragment = PassengerDashboardFragment()
}