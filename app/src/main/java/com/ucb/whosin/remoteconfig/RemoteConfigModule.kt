package com.ucb.whosin.remoteconfig

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import org.koin.dsl.module

val remoteConfigModule = module {
    single { FirebaseRemoteConfig.getInstance() }
    single { RemoteConfigService(get()) }
}
