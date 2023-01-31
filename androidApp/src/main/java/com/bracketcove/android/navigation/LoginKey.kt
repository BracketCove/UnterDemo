package com.bracketcove.android.navigation

import androidx.fragment.app.Fragment
import com.bracketcove.android.authentication.login.LoginFragment
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentKey
import kotlinx.parcelize.Parcelize

@Parcelize
data object LoginKey: DefaultFragmentKey() {
    override fun instantiateFragment(): Fragment = LoginFragment()
}