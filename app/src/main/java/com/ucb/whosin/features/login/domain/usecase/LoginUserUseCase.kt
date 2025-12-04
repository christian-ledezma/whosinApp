package com.ucb.whosin.features.login.domain.usecase

import com.ucb.whosin.features.login.domain.model.AuthResult
import com.ucb.whosin.features.login.domain.repository.AuthRepository
import com.ucb.whosin.features.login.domain.vo.Email
import com.ucb.whosin.features.login.domain.vo.Password

class LoginUserUseCase (private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): AuthResult {
        val emailResult = Email.create(email)
        if (emailResult.isFailure) {
            return AuthResult.Error(
                emailResult.exceptionOrNull()?.message ?: "Email inválido"
            )
        }

        val passwordResult = Password.create(password)
        if (passwordResult.isFailure) {
            return AuthResult.Error(
                passwordResult.exceptionOrNull()?.message ?: "Contraseña inválida"
            )
        }

        // Login con value objects
        return repository.loginUser(
            emailResult.getOrThrow(),
            passwordResult.getOrThrow()
        )
    }
}