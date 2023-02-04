package com.bracketcove.android.splashscreen

import com.bracketcove.ServiceResult
import com.bracketcove.android.navigation.DriverDashboardKey
import com.bracketcove.android.navigation.LoginKey
import com.bracketcove.android.navigation.PassengerDashboardKey
import com.bracketcove.android.navigation.ProfileSettingsKey
import com.bracketcove.authorization.AuthService
import com.bracketcove.domain.User
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.ScopedServices
import com.zhuinden.simplestack.StateChange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext


class SplashViewModel(
    val backstack: Backstack,
    val authService: AuthService
) : ScopedServices.Activated, CoroutineScope {

    private fun sendToLogin() {

        //clear backstack and replace with what we enter
        backstack.setHistory(
            History.of(LoginKey()),
            //Direction of navigation which is used for animation
            StateChange.FORWARD
        )
    }

    fun checkAuthState() = launch {
        val getUser = authService.getUser()

        when (getUser) {
            //there's nothing else to do but send to the login page
            is ServiceResult.Failure -> sendToLogin()
            is ServiceResult.Success -> {
                if (getUser.value == null) sendToLogin()
                else sendToDashboard(getUser.value!!)
            }
        }
    }

    private fun sendToDashboard(user: User) {
        //TODO fix when done with testing
        backstack.setHistory(
            History.of(ProfileSettingsKey()),
            //Direction of navigation which is used for animation
            StateChange.FORWARD
        )
//        when (user.type) {
//            "PASSENGER" -> backstack.setHistory(
//                History.of((PassengerDashboardKey())),
//                //Direction of navigation which is used for animation
//                StateChange.FORWARD
//            )
//            "DRIVER" -> backstack.setHistory(
//                History.of((DriverDashboardKey())),
//                //Direction of navigation which is used for animation
//                StateChange.FORWARD
//            )
//        }
    }

    //Lifecycle method to Fetch things if necessary
    override fun onServiceActive() {
        checkAuthState()
    }

    //Tear down
    override fun onServiceInactive() {
        canceller.cancel()
    }

    private val canceller = Job()

    override val coroutineContext: CoroutineContext
        get() = canceller + Dispatchers.Main
}