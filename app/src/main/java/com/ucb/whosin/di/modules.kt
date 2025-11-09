package com.ucb.whosin.di


import com.ucb.whosin.features.event.data.repository.EventRepository
import com.ucb.whosin.features.event.domain.repository.IEventRepository
import com.ucb.whosin.features.event.domain.usecase.FindByNameUseCase
import com.ucb.whosin.features.event.presentation.EventViewModel
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

    single<IEventRepository>{ EventRepository() }
    factory { FindByNameUseCase(get()) }
    viewModel{ EventViewModel(get()) }
}