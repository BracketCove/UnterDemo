package com.bracketcove.android.splashscreen

import com.bracketcove.IFakeRepository
import com.bracketcove.android.navigation.DriverDashboardKey
import com.bracketcove.android.navigation.LoginKey
import com.bracketcove.android.navigation.PassengerDashboardKey
import com.bracketcove.domain.User
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.ScopedServices
import com.zhuinden.simplestack.StateChange


class SplashViewModel(
    val backstack: Backstack
) : ScopedServices.Activated {

    private fun sendToLogin() {
        //clear backstack and replace with what we enter
        backstack.setHistory(
            History.of(LoginKey()),
            //Direction of navigation which is used for animation
            StateChange.FORWARD
        )
    }

    fun checkAuthState() {

    }

    private fun sendToDashboard(user: User) {
        when (user.type) {
            "PASSENGER" -> backstack.setHistory(
                History.of((PassengerDashboardKey())),
                //Direction of navigation which is used for animation
                StateChange.FORWARD
            )
            "DRIVER" -> backstack.setHistory(
                History.of((DriverDashboardKey())),
                //Direction of navigation which is used for animation
                StateChange.FORWARD
            )
        }
    }

    //Lifecycle method to Fetch things if necessary
    override fun onServiceActive() {
        checkAuthState()
    }

    //Tear down
    override fun onServiceInactive() {
        Unit
    }
}