package com.ucb.whosin.features.login.domain.usecase

import com.ucb.whosin.features.login.domain.model.AuthResult
import com.ucb.whosin.features.login.domain.repository.AuthRepository

class LoginUserUseCase (private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): AuthResult {
        if (email.isBlank() || password.isBlank()) {
            return AuthResult.Error("El correo y la contraseña no pueden estar vacíos")
        }

        return repository.loginUser(email, password)
    }
}