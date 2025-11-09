package com.ucb.whosin.di

import com.ucb.whosin.features.Guard.data.repository.GuardRepository
import com.ucb.whosin.features.Guard.data.repository.GuardRepositoryImpl
import com.ucb.whosin.ui.guard.GuardViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val guardModule = module {
    single<GuardRepository> { GuardRepositoryImpl() } // No longer needs Firestore
    viewModel { GuardViewModel(get()) }
}