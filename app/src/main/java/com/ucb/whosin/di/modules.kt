package com.ucb.whosin.di


import com.ucb.whosin.features.event.data.repository.EventRepository
import com.ucb.whosin.features.event.domain.repository.IEventRepository
import com.ucb.whosin.features.event.domain.usecase.FindByNameUseCase
import com.ucb.whosin.features.event.presentation.EventViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ucb.whosin.features.login.data.AuthRepositoryImpl
import com.ucb.whosin.features.login.datasource.FirebaseAuthDataSource
import com.ucb.whosin.features.login.domain.repository.AuthRepository
import com.ucb.whosin.features.login.domain.usecase.LoginUserUseCase
import com.ucb.whosin.features.login.domain.usecase.RegisterUserUseCase
import com.ucb.whosin.features.login.presentation.LoginViewModel
import com.ucb.whosin.features.login.presentation.RegisterViewModel
import okhttp3.OkHttpClient
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

val appModule = module {
    // Firebase
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }

    // DataSource
    single { FirebaseAuthDataSource(get(), get()) }

    // Repository
    single<AuthRepository> { AuthRepositoryImpl(get()) }

    // Use Cases
    single { RegisterUserUseCase(get()) }
    single { LoginUserUseCase(get()) }

    // ViewModels
    viewModel { RegisterViewModel(get()) }
    viewModel { LoginViewModel(get()) }
    single {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    single<IEventRepository>{ EventRepository() }
    factory { FindByNameUseCase(get()) }
    viewModel{ EventViewModel(get()) }
}