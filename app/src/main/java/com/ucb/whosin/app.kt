package com.ucb.whosin

import android.app.Application
import com.ucb.whosin.di.appModule
import com.ucb.whosin.di.guardModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext( this@App )
            modules(
                appModule,
                guardModule
            )
        }
    }
}