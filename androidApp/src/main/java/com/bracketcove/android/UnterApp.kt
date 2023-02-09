package com.bracketcove.android

import android.app.Application
import com.bracketcove.android.google.GoogleService
import com.bracketcove.authorization.FirebaseAuthService
import com.bracketcove.authorization.UserService
import com.bracketcove.fakes.FakeUserService
import com.bracketcove.fakes.FakeRideService
import com.bracketcove.rides.RideService
import com.google.android.gms.maps.MapsInitializer
import com.google.firebase.auth.FirebaseAuth
import com.google.maps.GeoApiContext
import com.zhuinden.simplestack.GlobalServices
import com.zhuinden.simplestackextensions.servicesktx.add
import com.zhuinden.simplestackextensions.servicesktx.rebind

class UnterApp: Application() {
    lateinit var globalServices: GlobalServices
    lateinit var geoContext: GeoApiContext

    override fun onCreate() {
        super.onCreate()
        MapsInitializer.initialize(this)
        geoContext = GeoApiContext.Builder()
            .apiKey(BuildConfig.MAPS_API_KEY)
            .build()

        val userService = FirebaseAuthService(FirebaseAuth.getInstance())
        val fakeRideService = FakeRideService()

        val googleService = GoogleService(this, geoContext)
        globalServices = GlobalServices.builder()
            .add(userService)
            .rebind<UserService>(userService)
            .add(fakeRideService)
            .rebind<RideService>(fakeRideService)
            .add(googleService)
            .build()


    }
}