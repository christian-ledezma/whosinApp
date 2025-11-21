package com.ucb.whosin.di


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ucb.whosin.features.event.data.datasource.FirebaseEventDataSource
import com.ucb.whosin.features.event.data.repository.EventRepository
import com.ucb.whosin.features.event.domain.repository.IEventRepository
import com.ucb.whosin.features.event.domain.usecase.FindByNameUseCase
import com.ucb.whosin.features.event.domain.usecase.GetEventByIdUseCase
import com.ucb.whosin.features.event.domain.usecase.RegisterEventUseCase
import com.ucb.whosin.features.event.presentation.RegisterEventViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import com.ucb.whosin.features.login.data.AuthRepositoryImpl
import com.ucb.whosin.features.login.data.SessionManager
import com.ucb.whosin.features.login.datasource.FirebaseAuthDataSource
import com.ucb.whosin.features.login.domain.repository.AuthRepository
import com.ucb.whosin.features.login.domain.usecase.CheckSessionUseCase
import com.ucb.whosin.features.login.domain.usecase.LoginUserUseCase
import com.ucb.whosin.features.login.domain.usecase.LogoutUseCase
import com.ucb.whosin.features.login.domain.usecase.RegisterUserUseCase
import com.ucb.whosin.features.login.presentation.LoginViewModel
import com.ucb.whosin.features.login.presentation.RegisterViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    // Firebase
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }

    // SessionManager
    single { SessionManager(androidContext()) }

    // DataSource
    single { FirebaseAuthDataSource(get(), get()) }

    // Repository
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }

    // Use Cases
    single { RegisterUserUseCase(get()) }
    single { LoginUserUseCase(get()) }
    single { CheckSessionUseCase(get()) }
    single { LogoutUseCase(get(), get()) }

    // ViewModels
    viewModel { RegisterViewModel(get(), get()) }
    viewModel { LoginViewModel(get(), get()) }

    // DataSource
    single { FirebaseEventDataSource(get(), get()) }

    // Repository
    single<IEventRepository> { EventRepository(get()) }

    // Use Cases
    single { FindByNameUseCase(get()) }
    single { RegisterEventUseCase(get()) }
    single { GetEventByIdUseCase(get()) }

    // ViewModels
    viewModel { RegisterEventViewModel(get()) }
}