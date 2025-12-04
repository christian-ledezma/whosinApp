package com.ucb.whosin.features.login.domain.usecase

import com.ucb.whosin.features.login.domain.vo.UserId
import com.ucb.whosin.features.login.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetCurrentUserUseCase(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<String?> = flow {
        authRepository.getSession().collect { (userId, _) ->
            if (userId != null) {
                val userIdResult = UserId.create(userId)
                if (userIdResult.isSuccess) {
                    val userResult = authRepository.getUserProfile(userIdResult.getOrThrow())
                    if (userResult.isSuccess) {
                        emit(userResult.getOrThrow().fullName())
                    } else {
                        emit(null)
                    }
                } else {
                    emit(null)
                }
            } else {
                emit(null)
            }
        }
    }
}