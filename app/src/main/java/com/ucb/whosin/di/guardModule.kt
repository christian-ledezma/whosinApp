package com.ucb.whosin.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ucb.whosin.features.Guard.data.repository.GuardRepository
import com.ucb.whosin.features.Guard.data.repository.GuardRepositoryFirebase
import com.ucb.whosin.ui.guard.GuardViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val guardModule = module {
    // Repositorio que depende de Firestore
    single<GuardRepository> { GuardRepositoryFirebase(get<FirebaseFirestore>()) }

    // ViewModel que depende del repositorio, FirebaseAuth y SavedStateHandle
    viewModel { GuardViewModel(get(), get<FirebaseAuth>(), get()) }
}