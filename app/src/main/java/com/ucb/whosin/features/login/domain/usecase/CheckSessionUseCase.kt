package com.ucb.whosin.features.login.domain.usecase

import com.ucb.whosin.features.login.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class CheckSessionUseCase(private val repository: AuthRepository) {
    operator fun invoke(): Flow<Boolean> = repository.isLoggedIn()
}