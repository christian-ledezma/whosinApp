package com.ucb.whosin.features.login.domain.usecase

import com.google.firebase.auth.FirebaseAuth
import com.ucb.whosin.features.login.domain.repository.AuthRepository

class LogoutUseCase(
    private val repository: AuthRepository,
    private val firebaseAuth: FirebaseAuth
) {
    suspend operator fun invoke() {
        firebaseAuth.signOut()
        repository.clearSession()
    }
}