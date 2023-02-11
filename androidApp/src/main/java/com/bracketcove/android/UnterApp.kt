package com.bracketcove.android

import android.app.Application
import com.bracketcove.android.google.GoogleService
import com.bracketcove.authorization.*
import com.bracketcove.rides.RideService
import com.bracketcove.usecase.GetUser
import com.bracketcove.usecase.LogInUser
import com.bracketcove.usecase.SignUpUser
import com.bracketcove.usecase.UpdateUserAvatar
import com.google.android.gms.maps.MapsInitializer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
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
        val firebaseStorageService = FirebasePhotoService(FirebaseStorage.getInstance(), this)

        val streamUserService = StreamUserService(streamClient)
        val streamRideService = StreamRideService()

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
        val updateUserAvatar = UpdateUserAvatar(firebaseStorageService, streamUserService)

        globalServices = GlobalServices.builder()
            .add(streamRideService)
            .rebind<RideService>(streamRideService)
            .add(streamUserService)
            .rebind<UserService>(streamUserService)
            .add(firebaseAuthService)
            .rebind<AuthorizationService>(firebaseAuthService)
            .add(googleService)
            .add(getUser)
            .add(signUpUser)
            .add(logInUser)
            .add(updateUserAvatar)
            .build()
    }
}