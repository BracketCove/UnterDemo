package com.bracketcove.android.navigation

import androidx.fragment.app.Fragment
import com.bracketcove.android.authentication.signup.SignUpViewModel
import com.bracketcove.android.dashboards.passenger.PassengerDashboardFragment
import com.bracketcove.android.profile.driver.DriverSettingsFragment
import com.bracketcove.android.profile.driver.DriverSettingsViewModel
import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentKey
import com.zhuinden.simplestackextensions.services.DefaultServiceProvider
import com.zhuinden.simplestackextensions.servicesktx.add
import com.zhuinden.simplestackextensions.servicesktx.lookup
import kotlinx.parcelize.Parcelize

@Parcelize
data class DriverSettingsKey(private val noArgsPlaceholder: String = ""): DefaultFragmentKey(),
    DefaultServiceProvider.HasServices  {
    override fun instantiateFragment(): Fragment = DriverSettingsFragment()

    override fun getScopeTag(): String = toString()

    //How to create a scoped service
    override fun bindServices(serviceBinder: ServiceBinder) {
        with(serviceBinder) {
            add(DriverSettingsViewModel(backstack, lookup()))
        }
    }
}