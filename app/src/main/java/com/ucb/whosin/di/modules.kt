package com.ucb.whosin.di


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ucb.whosin.features.event.data.datasource.FirebaseEventDataSource
import com.ucb.whosin.features.event.data.repository.EventRepository
import com.ucb.whosin.features.event.domain.repository.IEventRepository
import com.ucb.whosin.features.event.domain.usecase.AddGuardUseCase
import com.ucb.whosin.features.event.domain.usecase.CancelEventUseCase
import com.ucb.whosin.features.event.domain.usecase.DeleteEventUseCase
import com.ucb.whosin.features.event.domain.usecase.FindEventsByNameUseCase
import com.ucb.whosin.features.event.domain.usecase.GetAllEventsUseCase
import com.ucb.whosin.features.event.domain.usecase.GetEventByIdUseCase
import com.ucb.whosin.features.event.domain.usecase.GetEventGuardsUseCase
import com.ucb.whosin.features.event.domain.usecase.RegisterEventUseCase
import com.ucb.whosin.features.event.domain.usecase.RemoveGuardUseCase
import com.ucb.whosin.features.event.domain.usecase.UpdateEventUseCase
import com.ucb.whosin.features.event.presentation.EventEditViewModel
import com.ucb.whosin.features.event.presentation.EventSelectorViewModel
import com.ucb.whosin.features.event.presentation.LocationViewModel
import com.ucb.whosin.features.event.presentation.RegisterEventViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import com.ucb.whosin.features.login.data.AuthRepositoryImpl
import com.ucb.whosin.features.login.data.SessionManager
import com.ucb.whosin.features.login.datasource.FirebaseAuthDataSource
import com.ucb.whosin.features.login.domain.repository.AuthRepository
import com.ucb.whosin.features.login.domain.usecase.ChangePasswordUseCase
import com.ucb.whosin.features.login.domain.usecase.CheckSessionUseCase
import com.ucb.whosin.features.login.domain.usecase.GetCurrentUserUseCase
import com.ucb.whosin.features.login.domain.usecase.GetUserProfileUseCase
import com.ucb.whosin.features.login.domain.usecase.LoginUserUseCase
import com.ucb.whosin.features.login.domain.usecase.LogoutUseCase
import com.ucb.whosin.features.login.domain.usecase.RegisterUserUseCase
import com.ucb.whosin.features.login.domain.usecase.UpdateUserProfileUseCase
import com.ucb.whosin.features.login.presentation.LoginViewModel
import com.ucb.whosin.features.login.presentation.ProfileViewModel
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
    single { GetCurrentUserUseCase(get()) }

    // Use Cases - Perfil
    single { GetUserProfileUseCase(get()) }
    single { UpdateUserProfileUseCase(get()) }
    single { ChangePasswordUseCase(get()) }

    // ViewModels - Autenticación
    viewModel { RegisterViewModel(get(), get()) }
    viewModel { LoginViewModel(get(), get()) }

    // DataSource
    single { FirebaseEventDataSource(get(), get()) }

    // ViewModel - Perfil
    viewModel { ProfileViewModel(get(), get(), get(), get()) }

    // Repository - Eventos
    single<IEventRepository> { EventRepository(get()) }

    // Use Cases - Eventos
    factory { FindEventsByNameUseCase(get()) }
    single { RegisterEventUseCase(get()) }
    single { GetEventByIdUseCase(get()) }
    factory { DeleteEventUseCase(get()) }
    single { GetAllEventsUseCase(get()) }
    single { UpdateEventUseCase(get()) }
    single { CancelEventUseCase(get()) }
    single { AddGuardUseCase(get()) }
    single { RemoveGuardUseCase(get()) }
    single { GetEventGuardsUseCase(get()) }


    // ViewModels - Eventos
    viewModel { RegisterEventViewModel(get()) }
    viewModel { EventSelectorViewModel(get(), get(),get(), get()) }
    viewModel { LocationViewModel() }
    viewModel { EventEditViewModel(get(), get(), get(), get(), get(), get(), get()) }
}