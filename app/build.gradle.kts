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

    // this will upload your source code to Sentry to show it as part of the stack traces
    // disable if you don't want to expose your sources
    includeSourceContext.set(true)
}

dependencies {

    // 🔹 ANDROIDX BÁSICO Y COMPOSE
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.ui.text.google.fonts)

    // 🔹 FIREBASE (usando BOM)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.database)
    // implementation(libs.firebase.messaging) // si lo usas después, descomenta

    // 🔹 INYECCIÓN DE DEPENDENCIAS (KOIN)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.navigation)
    implementation(libs.koin.androidx.compose)

    // 🔹 NETWORKING Y SERIALIZACIÓN
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // 🔹 IMÁGENES
    implementation(libs.coil.compose)
    implementation(libs.coil.network)

    // 🔹 ROOM (Persistencia local)
    implementation(libs.bundles.local)
    annotationProcessor(libs.room.compiler)
    ksp(libs.room.compiler)

    // 🔹 DATASTORE (almacenamiento liviano)
    implementation(libs.datastore)

    // 🔹 MONITOREO / LOGGING
    implementation(libs.sentry)

    // 🔹 TESTING
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.room.testing)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
