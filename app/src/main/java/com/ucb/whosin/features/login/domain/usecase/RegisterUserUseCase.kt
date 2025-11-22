package com.ucb.whosin.features.login.domain.usecase

import com.ucb.whosin.features.login.domain.model.AuthResult
import com.ucb.whosin.features.login.domain.model.RegisterData
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

    suspend fun register(registerData: RegisterData): AuthResult {
        // Validaciones
        val validationError = validateRegisterData(registerData)
        if (validationError != null) {
            return AuthResult.Error(validationError)
        }

        return repository.registerUser(registerData)
    }

    private fun validateRegisterData(data: RegisterData): String? {
        return when {
            data.email.isBlank() -> "El correo electrónico es obligatorio"
            !isValidEmail(data.email) -> "El formato del correo no es válido"
            data.password.isBlank() -> "La contraseña es obligatoria"
            data.password.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
            data.name.isBlank() -> "El nombre es obligatorio"
            data.lastname.isBlank() -> "El apellido paterno es obligatorio"
            data.phone.isBlank() -> "El número de teléfono es obligatorio"
            !isValidPhone(data.phone) -> "El número de teléfono no es válido"
            else -> null
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPhone(phone: String): Boolean {
        // Solo números, mínimo 7 dígitos, máximo 15
        val cleanPhone = phone.replace(Regex("[^0-9]"), "")
        return cleanPhone.length in 7..15
    }

}