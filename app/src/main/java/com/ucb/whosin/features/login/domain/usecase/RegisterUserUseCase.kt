package com.ucb.whosin.features.login.domain.usecase

import com.ucb.whosin.features.login.domain.model.AuthResult
import com.ucb.whosin.features.login.domain.model.CountryCode
import com.ucb.whosin.features.login.domain.model.RegisterData
import com.ucb.whosin.features.login.domain.repository.AuthRepository

class RegisterUserUseCase (private val repository: AuthRepository) {
    suspend operator fun invoke(
        email: String,
        password: String,
        name: String,
        lastname: String,
        secondLastname: String?,
        phone: String,
        countryCode: CountryCode
    ): AuthResult {
        val registerDataResult = RegisterData.create(
            email = email,
            password = password,
            name = name,
            lastname = lastname,
            secondLastname = secondLastname,
            phone = phone,
            countryCode = countryCode
        )

        if (registerDataResult.isFailure) {
            return AuthResult.Error(
                registerDataResult.exceptionOrNull()?.message ?: "Error de validación"
            )
        }

        return repository.registerUser(registerDataResult.getOrThrow())
    }
}