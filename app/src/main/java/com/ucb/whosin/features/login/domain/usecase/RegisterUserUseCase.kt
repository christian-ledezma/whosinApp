package com.ucb.whosin.features.login.domain.usecase

import com.ucb.whosin.features.login.domain.model.AuthResult
import com.ucb.whosin.features.login.domain.repository.AuthRepository

class RegisterUserUseCase (private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): AuthResult {
        if (email.isBlank() || password.isBlank()) {
            return AuthResult.Error("El correo y la contraseña no pueden estar vacíos")
        }

        if (password.length < 6) {
            return AuthResult.Error("La contraseña debe tener al menos 6 caracteres")
        }

        return repository.registerUser(email, password)
    }
}