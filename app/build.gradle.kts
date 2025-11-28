import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    id("io.sentry.android.gradle") version "5.12.1"
    //alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.ucb.whosin"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.ucb.whosin"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val properties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            properties.load(localPropertiesFile.inputStream())
        }

        manifestPlaceholders["MAPS_API_KEY"] = properties.getProperty("MAPS_API_KEY", "")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

sentry {
    org.set("universidad-catolica-bk")
    projectName.set("android")


    autoUploadProguardMapping.set(false)
    uploadNativeSymbols.set(false)
    includeSourceContext.set(false)
    autoUploadSourceContext.set(false)
    // this will upload your source code to Sentry to show it as part of the stack traces
    // disable if you don't want to expose your sources
    //includeSourceContext.set(true)
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.firebase.database)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.firebase.firestore)
    implementation(libs.androidx.datastore.core)
    implementation(libs.material3)
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    //implementation(libs.firebase.database)
    //implementation(libs.firebase.messaging)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation (libs.koin.android)
    implementation (libs.koin.androidx.navigation)
    implementation (libs.koin.androidx.compose)
    implementation(libs.coil.compose)
    implementation(libs.coil.network)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.sentry)
    implementation(libs.androidx.material.icons.extended)

    //local bundle room
    implementation(libs.bundles.local)
    annotationProcessor(libs.room.compiler)
    ksp(libs.room.compiler)
    testImplementation(libs.room.testing)

    implementation(libs.datastore)

    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)

    //google fonts material
    implementation(libs.androidx.ui.text.google.fonts)

    // CameraX
    implementation(libs.bundles.camerax)
    implementation(libs.google.mlkit.barcode.scanning)
    implementation("com.google.guava:guava:33.0.0-android")

    // Google Maps
    implementation(libs.bundles.googlemaps)

    //splash
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Compose BOM
    implementation(libs.androidx.compose.foundation)

    implementation("androidx.core:core-splashscreen:1.0.1")

    // Remote Config
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    implementation("com.google.firebase:firebase-config")
}
