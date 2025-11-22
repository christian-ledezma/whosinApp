package com.ucb.whosin.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ucb.whosin.features.Guard.data.repository.GuardRepository
import com.ucb.whosin.features.Guard.data.repository.GuardRepositoryFirebase
import com.ucb.whosin.features.Guard.data.presentation.GuardViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val guardModule = module {

    single<GuardRepository> { GuardRepositoryFirebase(get<FirebaseFirestore>()) }

    viewModel { GuardViewModel(get(), get<FirebaseAuth>(), get()) }
}