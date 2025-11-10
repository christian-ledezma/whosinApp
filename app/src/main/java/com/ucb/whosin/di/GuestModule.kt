package com.ucb.whosin.di




import com.uch.whosin.features.Guest.data.repository.GuestRepository
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val guestModule = module {
    // Repository
    single { GuestRepository() }

    // ViewModel
    //viewModel { GuestViewModel(get()) }
}