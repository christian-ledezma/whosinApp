package com.ucb.whosin.features.login.domain.usecase

import com.ucb.whosin.features.login.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetCurrentUserUseCase(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<String?> = flow {
        authRepository.getSession().collect { (userId, _) ->
            if (userId != null) {
                val user = authRepository.getUserProfile(userId)
                emit(user?.fullName())
            } else {
                emit(null)
            }
        }
    }
}