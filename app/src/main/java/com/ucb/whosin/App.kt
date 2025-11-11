package com.ucb.whosin

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.ucb.whosin.di.appModule
import com.ucb.whosin.di.guestModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                val options = FirebaseOptions.Builder()
                    .setProjectId("whosin-992db")
                    .setApplicationId("1:437570902163:android:9bcdc8ae6bd5406777d566")
                    .setApiKey("AIzaSyB-foGBulcCnpcYBkcm0wkq7fJ5eo7CaVU")
                    .setDatabaseUrl("https://whosin-992db-default-rtdb.firebaseio.com")
                    .setStorageBucket("whosin-992db.firebasestorage.app")
                    .build()

                FirebaseApp.initializeApp(this, options)
                Log.d("Firebase", "✅ Firebase inicializado manualmente")
            } else {
                Log.d("Firebase", "✅ Firebase ya estaba inicializado")
            }
        } catch (e: Exception) {
            Log.e("Firebase", "❌ Error al inicializar Firebase", e)
        }


        startKoin {
            androidContext( this@App )
            modules(
                appModule,
                guestModule
            )
        }
    }
}