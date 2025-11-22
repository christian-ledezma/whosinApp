package com.ucb.whosin.features.login.domain.usecase

import com.ucb.whosin.features.login.domain.model.User
import com.ucb.whosin.features.login.domain.repository.AuthRepository

class GetUserProfileUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(userId: String): User? {
        return repository.getUserProfile(userId)
    }
}

class UpdateUserProfileUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(user: User): Result<Unit> {
        // Validaciones
        if (user.name.isBlank()) {
            return Result.failure(Exception("El nombre es obligatorio"))
        }
        if (user.lastname.isBlank()) {
            return Result.failure(Exception("El apellido es obligatorio"))
        }
        if (user.phone.isBlank()) {
            return Result.failure(Exception("El teléfono es obligatorio"))
        }

        return repository.updateUserProfile(user)
    }
}

class ChangePasswordUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(
        currentPassword: String,
        newPassword: String,
        confirmPassword: String
    ): Result<Unit> {
        // Validaciones
        if (currentPassword.isBlank()) {
            return Result.failure(Exception("La contraseña actual es obligatoria"))
        }
        if (newPassword.isBlank()) {
            return Result.failure(Exception("La nueva contraseña es obligatoria"))
        }
        if (newPassword.length < 6) {
            return Result.failure(Exception("La nueva contraseña debe tener al menos 6 caracteres"))
        }
        if (newPassword != confirmPassword) {
            return Result.failure(Exception("Las contraseñas no coinciden"))
        }
        if (currentPassword == newPassword) {
            return Result.failure(Exception("La nueva contraseña debe ser diferente a la actual"))
        }

        return repository.changePassword(currentPassword, newPassword)
    }
}