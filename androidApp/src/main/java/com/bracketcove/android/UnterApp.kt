package com.bracketcove.android

import android.app.Application
import com.bracketcove.IFakeRepository
import com.bracketcove.domain.FakeRepoImpl
import com.zhuinden.simplestack.GlobalServices
import com.zhuinden.simplestackextensions.servicesktx.add
import com.zhuinden.simplestackextensions.servicesktx.rebind

class UnterApp: Application() {
    lateinit var globalServices: GlobalServices

    override fun onCreate() {
        super.onCreate()

        val fakeRepoImpl = FakeRepoImpl()
        globalServices = GlobalServices.builder().add(
            fakeRepoImpl
        ).rebind<IFakeRepository>(fakeRepoImpl).build()


    }
}