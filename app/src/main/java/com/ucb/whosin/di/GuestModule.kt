
package com.ucb.whosin.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ucb.whosin.features.Guest.data.datasource.FirebaseGuestDataSource
import com.ucb.whosin.features.Guest.data.repository.GuestRepository
import com.ucb.whosin.features.Guest.domain.repository.IGuestRepository
import com.ucb.whosin.features.Guest.domain.usecase.AddGuestUseCase
import com.ucb.whosin.features.Guest.domain.usecase.DeleteGuestUseCase
import com.ucb.whosin.features.Guest.domain.usecase.GetGuestsUseCase
import com.ucb.whosin.features.Guest.domain.usecase.UpdateGuestUseCase
import com.ucb.whosin.features.Guest.presentation.AcceptInvitationViewModel
import com.ucb.whosin.features.Guest.presentation.AddGuestViewModel
import com.ucb.whosin.features.Guest.presentation.GuestListViewModel
import com.ucb.whosin.features.event.data.repository.EventRepository
import com.ucb.whosin.features.event.domain.repository.IEventRepository
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val guestModule = module {
    // DataSource
    single { FirebaseGuestDataSource(get<FirebaseFirestore>()) }

    // Repository
    single<IGuestRepository> { GuestRepository(get()) }
    single<IEventRepository> { EventRepository(get()) }

    // UseCases
    factory { AddGuestUseCase(get(),get()) }
    factory { GetGuestsUseCase(get()) }
    factory { UpdateGuestUseCase(get()) }
    factory { DeleteGuestUseCase(get()) }

    // ViewModels
    viewModel { GuestListViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { AddGuestViewModel(get(), get<FirebaseAuth>(), get()) }
    viewModel { AcceptInvitationViewModel(get(), get(), get<FirebaseAuth>(), get(), get()) }
}
