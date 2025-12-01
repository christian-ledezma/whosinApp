package com.ucb.whosin.remoteconfig

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import kotlinx.coroutines.tasks.await

class RemoteConfigService(
    private val remoteConfig: FirebaseRemoteConfig
) {
    init {
        remoteConfig.setConfigSettingsAsync(
            remoteConfigSettings {
                minimumFetchIntervalInSeconds = 0 // inmediato
            }
        )

        remoteConfig.setDefaultsAsync(
            mapOf("maintenance" to false)
        )
    }

    suspend fun fetchMaintenanceFlag(): Boolean {
        remoteConfig.fetchAndActivate().await()
        return remoteConfig.getBoolean("maintenance")
    }
}
