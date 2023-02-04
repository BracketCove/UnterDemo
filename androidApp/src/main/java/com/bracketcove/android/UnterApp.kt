package com.bracketcove.android

import android.app.Application
import com.bracketcove.authorization.UserService
import com.bracketcove.fakes.FakeUserService
import com.bracketcove.fakes.FakeRideService
import com.bracketcove.rides.RideService
import com.zhuinden.simplestack.GlobalServices
import com.zhuinden.simplestackextensions.servicesktx.add
import com.zhuinden.simplestackextensions.servicesktx.rebind

class UnterApp: Application() {
    lateinit var globalServices: GlobalServices

    override fun onCreate() {
        super.onCreate()

        val fakeAuth = FakeUserService()
        val fakeRideService = FakeRideService()

        globalServices = GlobalServices.builder()
            .add(fakeAuth)
            .rebind<UserService>(fakeAuth)
            .add(fakeRideService)
            .rebind<RideService>(fakeRideService)
            .build()
    }
}