package com.ucb.whosin.features.login.domain.usecase

import com.ucb.whosin.features.login.domain.model.User
import com.ucb.whosin.features.login.domain.repository.AuthRepository
import com.ucb.whosin.features.login.domain.vo.Password
import com.ucb.whosin.features.login.domain.vo.UserId

class GetUserProfileUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(userId: String):  Result<User> {
        val userIdResult = UserId.create(userId)
        if (userIdResult.isFailure) {
            return Result.failure(
                userIdResult.exceptionOrNull() ?: Exception("UserId inválido")
            )
        }

        return repository.getUserProfile(userIdResult.getOrThrow())
    }
}

class UpdateUserProfileUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(user: User): Result<Unit> {
        return repository.updateUserProfile(user)
    }
}

class ChangePasswordUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(
        currentPassword: String,
        newPassword: String,
        confirmPassword: String
    ): Result<Unit> {
        // Validar contraseña actual
        val currentPasswordResult = Password.create(currentPassword)
        if (currentPasswordResult.isFailure) {
            return Result.failure(
                currentPasswordResult.exceptionOrNull() ?: Exception("Contraseña actual inválida")
            )
        }

        // Validar nueva contraseña
        val newPasswordResult = Password.create(newPassword)
        if (newPasswordResult.isFailure) {
            return Result.failure(
                newPasswordResult.exceptionOrNull() ?: Exception("Nueva contraseña inválida")
            )
        }

        // Validar confirmación
        val confirmPasswordResult = Password.create(confirmPassword)
        if (confirmPasswordResult.isFailure) {
            return Result.failure(
                confirmPasswordResult.exceptionOrNull() ?: Exception("Confirmación de contraseña inválida")
            )
        }

        // Verificar que las nuevas contraseñas coincidan
        if (newPasswordResult.getOrThrow().value != confirmPasswordResult.getOrThrow().value) {
            return Result.failure(Exception("Las contraseñas no coinciden"))
        }

        // Validar que sean diferentes
        val validationResult = Password.validate(
            currentPasswordResult.getOrThrow(),
            newPasswordResult.getOrThrow()
        )
        if (validationResult.isFailure) {
            return validationResult
        }

        return repository.changePassword(
            currentPasswordResult.getOrThrow(),
            newPasswordResult.getOrThrow()
        )
    }
}