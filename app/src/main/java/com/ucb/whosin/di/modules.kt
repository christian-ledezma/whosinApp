package com.ucb.whosin.di


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ucb.whosin.features.event.data.datasource.FirebaseEventDataSource
import com.ucb.whosin.features.event.data.repository.EventRepository
import com.ucb.whosin.features.event.domain.repository.IEventRepository
import com.ucb.whosin.features.event.domain.usecase.FindByNameUseCase
import com.ucb.whosin.features.event.domain.usecase.RegisterEventUseCase
import com.ucb.whosin.features.event.presentation.RegisterEventViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import okhttp3.OkHttpClient
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

val appModule = module {

    single {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // Firebase
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }

    // DataSource
    single { FirebaseEventDataSource(get()) }

    // Repository
    single<IEventRepository> { EventRepository(get()) }

    // Use Cases
    single { FindByNameUseCase(get()) }
    single { RegisterEventUseCase(get()) }

    // ViewModels
    viewModel { RegisterEventViewModel(get()) }

}