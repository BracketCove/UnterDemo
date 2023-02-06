package com.bracketcove.android

import android.app.Application
import com.bracketcove.android.google.GoogleService
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

        val fakeUser = FakeUserService()
        val fakeRideService = FakeRideService()

        val googleService = GoogleService(this)
        globalServices = GlobalServices.builder()
            .add(fakeUser)
            .rebind<UserService>(fakeUser)
            .add(fakeRideService)
            .rebind<RideService>(fakeRideService)
            .add(googleService)
            .build()
    }
}