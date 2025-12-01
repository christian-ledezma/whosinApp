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
    compileSdk = 35

    ndkVersion = "27.0.12077973"

    defaultConfig {
        applicationId = "com.ucb.whosin"
        minSdk = 24
        targetSdk = 35
        versionCode = 11
        versionName = "1.7.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val properties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            properties.load(localPropertiesFile.inputStream())
        }

        manifestPlaceholders["MAPS_API_KEY"] = properties.getProperty("MAPS_API_KEY", "")

    }

    signingConfigs {
        create("release") {
            // Lee las variables de entorno del sistema
            // Si no existen, intenta leer de gradle.properties como fallback
            val storeFilePath = System.getenv("RELEASE_STORE_FILE")
                ?: project.findProperty("RELEASE_STORE_FILE") as String?

            val storePass = System.getenv("RELEASE_STORE_PASSWORD")
                ?: project.findProperty("RELEASE_STORE_PASSWORD") as String?

            val alias = System.getenv("RELEASE_KEY_ALIAS")
                ?: project.findProperty("RELEASE_KEY_ALIAS") as String?

            val keyPass = System.getenv("RELEASE_KEY_PASSWORD")
                ?: project.findProperty("RELEASE_KEY_PASSWORD") as String?

            // Validación: asegurarse de que las credenciales existen
            if (storeFilePath == null || storePass == null || alias == null || keyPass == null) {
                throw GradleException(
                    """
                    ❌ ERROR: Credenciales de firma no configuradas.
                    
                    Configura las variables de entorno:
                    - RELEASE_STORE_FILE
                    - RELEASE_STORE_PASSWORD
                    - RELEASE_KEY_ALIAS
                    - RELEASE_KEY_PASSWORD
                    
                    Ejemplo (macOS/Linux):
                    export RELEASE_STORE_FILE="/ruta/a/whosin.jks"
                    export RELEASE_STORE_PASSWORD="tu_password"
                    export RELEASE_KEY_ALIAS="key0"
                    export RELEASE_KEY_PASSWORD="tu_password"
                    """.trimIndent()
                )
            }

            storeFile = file(storeFilePath)
            storePassword = storePass
            keyAlias = alias
            keyPassword = keyPass
        }
    }


    packaging {
        jniLibs {
            useLegacyPackaging = false
        }
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/LICENSE.md"
            excludes += "/META-INF/LICENSE-notice.md"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true

            // Agregar estas líneas:
            ndk {
                debugSymbolLevel = "FULL"  // Para los símbolos de depuración
            }

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
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
    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose BOM y UI
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.compose.foundation)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.database)
    implementation(libs.kotlinx.coroutines.play.services)

    // DataStore
    implementation(libs.datastore)
    implementation(libs.androidx.datastore.core)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Network
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // Dependency Injection
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.navigation)
    implementation(libs.koin.androidx.compose)

    // Image Loading
    implementation(libs.coil.compose)
    implementation(libs.coil.network)

    // Monitoring
    implementation(libs.sentry)

    // Room Database
    implementation(libs.bundles.local)
    annotationProcessor(libs.room.compiler)
    ksp(libs.room.compiler)

    // Google Fonts
    implementation(libs.androidx.ui.text.google.fonts)

    // CameraX
    implementation(libs.bundles.camerax)
    implementation(libs.google.mlkit.barcode.scanning)
    implementation("com.google.guava:guava:33.0.0-android")

    // Google Maps
    implementation(libs.bundles.googlemaps)

    // Splash Screen
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation("androidx.core:core-splashscreen:1.0.1")

    // Testing
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


    // Remote Config
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    implementation("com.google.firebase:firebase-config")
}
