# ============================================
# WHOSIN APP - PROGUARD RULES
# ============================================

-keepattributes SourceFile,LineNumberTable
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses
-keepattributes Exceptions
-renamesourcefileattribute SourceFile

# ============================================
# FIREBASE (Auth, Firestore, Database, Remote Config)
# ============================================

-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# Firebase Firestore específico
-keep class com.google.firebase.firestore.** { *; }
-keepclassmembers class com.google.firebase.firestore.** { *; }
-keep interface com.google.firebase.firestore.** { *; }

# Firebase Auth
-keep class com.google.firebase.auth.** { *; }

# Firebase Timestamp (CRÍTICO)
-keep class com.google.firebase.Timestamp { *; }
-keepclassmembers class com.google.firebase.Timestamp { *; }

# Firebase Remote Config
-keep class com.google.firebase.remoteconfig.** { *; }

# ============================================
# KOTLIN
# ============================================

-keep class kotlin.Metadata { *; }
-keep class kotlin.reflect.** { *; }
-dontwarn kotlin.reflect.**

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** { volatile <fields>; }
-keep class kotlinx.coroutines.** { *; }

# Data classes - proteger componentes
-keepclassmembers class * {
    public <init>(...);
    public ** copy(...);
    public ** component1();
    public ** component2();
    public ** component3();
    public ** component4();
    public ** component5();
    public ** component6();
    public ** component7();
    public ** component8();
    public ** component9();
    public ** component10();
}

# ============================================
# MODELOS DE DOMINIO - WHOSIN
# ============================================

# TODOS los modelos de domain y data
-keep class com.ucb.whosin.features.**.domain.** { *; }
-keep class com.ucb.whosin.features.**.data.model.** { *; }
-keepclassmembers class com.ucb.whosin.features.**.domain.** { *; }
-keepclassmembers class com.ucb.whosin.features.**.data.model.** { *; }

# Value Objects (VO) - CRÍTICO para User
-keep class com.ucb.whosin.features.**.vo.** { *; }
-keepclassmembers class com.ucb.whosin.features.**.vo.** { *; }

# Sealed Classes (EventResult, GuestResult, AuthResult, GuardResult)
-keep class com.ucb.whosin.features.**.domain.model.EventResult { *; }
-keep class com.ucb.whosin.features.**.domain.model.EventResult$* { *; }
-keep class com.ucb.whosin.features.**.domain.model.GuestResult { *; }
-keep class com.ucb.whosin.features.**.domain.model.GuestResult$* { *; }
-keep class com.ucb.whosin.features.**.domain.model.AuthResult { *; }
-keep class com.ucb.whosin.features.**.domain.model.AuthResult$* { *; }
-keep class com.ucb.whosin.features.**.domain.model.GuardResult { *; }
-keep class com.ucb.whosin.features.**.domain.model.GuardResult$* { *; }

# Features específicas
-keep class com.ucb.whosin.features.login.** { *; }
-keep class com.ucb.whosin.features.event.** { *; }
-keep class com.ucb.whosin.features.Guest.** { *; }
-keep class com.ucb.whosin.features.Guard.** { *; }
-keep class com.ucb.whosin.features.qrscanner.** { *; }

# ============================================
# KOIN (INYECCIÓN DE DEPENDENCIAS) - CRÍTICO
# ============================================

-keep class org.koin.** { *; }
-keep class org.koin.core.** { *; }
-keep class org.koin.android.** { *; }
-keep class org.koin.androidx.** { *; }
-keepclassmembers class org.koin.** { *; }

# Módulos de Koin
-keep class com.ucb.whosin.di.** { *; }
-keepclassmembers class com.ucb.whosin.di.** { *; }

# ViewModels - CRÍTICO
-keep class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}
-keep class * extends androidx.lifecycle.AndroidViewModel {
    <init>(...);
}
-keep class androidx.lifecycle.** { *; }
-keep class androidx.lifecycle.SavedStateHandle { *; }

# Todos los ViewModels de la app
-keep class com.ucb.whosin.**.presentation.**ViewModel { *; }
-keep class com.ucb.whosin.**.*ViewModel { *; }
-keep class com.ucb.whosin.navigation.NavigationViewModel { *; }
-keep class com.ucb.whosin.navigation.NavigationViewModel$* { *; }

-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
    public <init>(...);
}

# Repositorios, UseCases, DataSources
-keep class com.ucb.whosin.**.repository.** { *; }
-keep class com.ucb.whosin.**.usecase.** { *; }
-keep class com.ucb.whosin.**.datasource.** { *; }

-keepclassmembers class com.ucb.whosin.**.repository.** { <init>(...); }
-keepclassmembers class com.ucb.whosin.**.usecase.** { <init>(...); }
-keepclassmembers class com.ucb.whosin.**.datasource.** { <init>(...); }

# Evitar renombrar clases inyectadas
-keepnames class com.ucb.whosin.** { *; }

# ============================================
# NAVIGATION - CRÍTICO
# ============================================

-keep class androidx.navigation.** { *; }
-keepclassmembers class * extends androidx.navigation.Navigator { *; }

# NavigationViewModel y sus sealed classes
-keep class com.ucb.whosin.navigation.** { *; }
-keepclassmembers class com.ucb.whosin.navigation.** { *; }

# Enums (NavigationOptions)
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ============================================
# COMPOSE
# ============================================

-keep class androidx.compose.** { *; }
-keepclassmembers class androidx.compose.** { *; }
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.foundation.** { *; }

# ============================================
# ROOM DATABASE
# ============================================

-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

-keep class androidx.room.** { *; }
-keepclassmembers class androidx.room.** { *; }

# ============================================
# RETROFIT + GSON
# ============================================

# Retrofit
-keepattributes Signature
-keepattributes Exceptions
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# Gson
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Modelos de red (si tienes)
-keep class com.ucb.whosin.**.network.** { *; }
-keep class com.ucb.whosin.**.api.** { *; }

# ============================================
# COIL (IMÁGENES)
# ============================================

-keep class coil.** { *; }
-keep interface coil.** { *; }

# ============================================
# CAMERAX + ML KIT (QR SCANNER)
# ============================================

-keep class androidx.camera.** { *; }
-keep class com.google.mlkit.** { *; }
-keep class com.google.android.gms.vision.** { *; }

# ============================================
# GOOGLE MAPS
# ============================================

-keep class com.google.android.gms.maps.** { *; }
-keep interface com.google.android.gms.maps.** { *; }
-keep class com.google.maps.android.** { *; }

# ============================================
# SENTRY (MONITORING)
# ============================================

-keep class io.sentry.** { *; }
-dontwarn io.sentry.**

# ============================================
# DATASTORE
# ============================================

-keep class androidx.datastore.** { *; }
-keepclassmembers class androidx.datastore.** { *; }

# SessionManager (usa DataStore)
-keep class com.ucb.whosin.features.login.data.SessionManager { *; }

# ============================================
# REMOTE CONFIG
# ============================================

-keep class com.ucb.whosin.remoteconfig.** { *; }

# ============================================
# PARCELABLE
# ============================================

-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# ============================================
# SERIALIZACIÓN GENERAL
# ============================================

-keepclassmembers class * {
    @kotlinx.serialization.SerialName <fields>;
}

# ============================================
# ENUMS
# ============================================

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
    **[] $VALUES;
}

# ============================================
# REFLECTION
# ============================================

-keepattributes InnerClasses
-keep class kotlin.reflect.jvm.internal.** { *; }
-keep interface kotlin.reflect.jvm.internal.impl.** { *; }