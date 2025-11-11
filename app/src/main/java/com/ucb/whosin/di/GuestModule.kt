package com.ucb.whosin.di

import com.google.firebase.firestore.FirebaseFirestore
import com.ucb.whosin.features.Guest.data.datasource.FirebaseGuestDataSource
import com.ucb.whosin.features.Guest.data.repository.GuestRepository
import com.ucb.whosin.features.Guest.domain.repository.IGuestRepository
import com.ucb.whosin.features.Guest.domain.usecase.AddGuestUseCase
import com.ucb.whosin.features.Guest.domain.usecase.GetGuestsUseCase
import com.ucb.whosin.features.Guest.presentation.AddGuestViewModel
import com.ucb.whosin.features.Guest.presentation.GuestListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val guestModule = module {
    // DataSource
    single { FirebaseGuestDataSource(get<FirebaseFirestore>()) }

    // Repository
    single<IGuestRepository> { GuestRepository(get()) }

    // UseCases
    factory { AddGuestUseCase(get()) }
    factory { GetGuestsUseCase(get()) }

    // ViewModels
    viewModel { GuestListViewModel(get()) }
    viewModel { AddGuestViewModel(get()) }
}