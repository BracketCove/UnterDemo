package com.bracketcove.android

import android.app.Application
import com.bracketcove.android.google.GoogleService
import com.bracketcove.authorization.AuthorizationService
import com.bracketcove.authorization.FirebaseAuthService
import com.bracketcove.authorization.StreamUserService
import com.bracketcove.authorization.UserService
import com.bracketcove.fakes.FakeRideService
import com.bracketcove.rides.RideService
import com.bracketcove.usecase.GetUser
import com.bracketcove.usecase.LogInUser
import com.bracketcove.usecase.SignUpUser
import com.google.android.gms.maps.MapsInitializer
import com.google.firebase.auth.FirebaseAuth
import com.google.maps.GeoApiContext
import com.zhuinden.simplestack.GlobalServices
import com.zhuinden.simplestackextensions.servicesktx.add
import com.zhuinden.simplestackextensions.servicesktx.rebind
import io.getstream.chat.android.client.ChatClient

class UnterApp: Application() {
    lateinit var globalServices: GlobalServices
    lateinit var geoContext: GeoApiContext

    override fun onCreate() {
        super.onCreate()
        MapsInitializer.initialize(this)
        geoContext = GeoApiContext.Builder()
            .apiKey(BuildConfig.MAPS_API_KEY)
            .build()
        val streamClient = ChatClient.Builder(BuildConfig.STREAM_API_KEY, this).build()

        val firebaseAuthService = FirebaseAuthService(FirebaseAuth.getInstance())
        val streamUserService = StreamUserService(streamClient)

        val fakeRideService = FakeRideService()
        val googleService = GoogleService(this, geoContext)

        /*
        Usecases:
        - In situations where multiple BE services must be coordinated in order to carry out a
        single function, a usecase is employed. However, In situations where a single call to a
        single BE service is required, there does not tend to be any benefit to adding usecases.
         */
        val getUser = GetUser(firebaseAuthService, streamUserService)
        val signUpUser = SignUpUser(firebaseAuthService, streamUserService)
        val logInUser = LogInUser(firebaseAuthService, streamUserService)

        globalServices = GlobalServices.builder()
            .add(streamUserService)
            .rebind<UserService>(streamUserService)
            .add(fakeRideService)
            .rebind<RideService>(fakeRideService)
            .add(firebaseAuthService)
            .rebind<AuthorizationService>(firebaseAuthService)
            .add(googleService)
            .add(getUser)
            .add(signUpUser)
            .add(logInUser)
            .build()
    }
}