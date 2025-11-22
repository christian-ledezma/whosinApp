package com.ucb.whosin.features.login.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.whosin.features.login.domain.model.AuthResult
import com.ucb.whosin.features.login.domain.model.CountryCode
import com.ucb.whosin.features.login.domain.model.RegisterData
import com.ucb.whosin.features.login.domain.repository.AuthRepository
import com.ucb.whosin.features.login.domain.usecase.RegisterUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class RegisterUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val name: String = "",
    val lastname: String = "",
    val secondLastname: String = "",
    val phone: String = "",
    val selectedCountryCode: CountryCode = CountryCode.BO, // Bolivia por defecto
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val showCountryPicker: Boolean = false,
    // Errores de campo individual
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val nameError: String? = null,
    val lastnameError: String? = null,
    val phoneError: String? = null
)

class RegisterViewModel(
    private val registerUserUseCase: RegisterUserUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, emailError = null, errorMessage = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, passwordError = null, errorMessage = null) }
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.update { it.copy(confirmPassword = confirmPassword, confirmPasswordError = null, errorMessage = null) }
    }

    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name, nameError = null, errorMessage = null) }
    }

    fun onLastnameChange(lastname: String) {
        _uiState.update { it.copy(lastname = lastname, lastnameError = null, errorMessage = null) }
    }

    fun onSecondLastnameChange(secondLastname: String) {
        _uiState.update { it.copy(secondLastname = secondLastname, errorMessage = null) }
    }

    fun onPhoneChange(phone: String) {
        // Solo permitir números
        val cleanPhone = phone.filter { it.isDigit() }
        _uiState.update { it.copy(phone = cleanPhone, phoneError = null, errorMessage = null) }
    }

    fun onCountryCodeSelect(countryCode: CountryCode) {
        _uiState.update { it.copy(selectedCountryCode = countryCode, showCountryPicker = false) }
    }

    fun toggleCountryPicker() {
        _uiState.update { it.copy(showCountryPicker = !it.showCountryPicker) }
    }

    fun dismissCountryPicker() {
        _uiState.update { it.copy(showCountryPicker = false) }
    }

    fun registerUser() {
        val state = _uiState.value

        // Validaciones locales
        if (!validateFields()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val registerData = RegisterData(
                email = state.email.trim(),
                password = state.password,
                name = state.name.trim(),
                lastname = state.lastname.trim(),
                secondLastname = state.secondLastname.trim().ifBlank { null },
                phone = state.phone.trim(),
                countryCode = state.selectedCountryCode
            )

            when (val result = registerUserUseCase.register(registerData)) {
                is AuthResult.Success -> {
                    authRepository.saveSession(
                        result.user.uid,
                        result.user.email
                    )
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                }
                is AuthResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
            }
        }
    }

    private fun validateFields(): Boolean {
        val state = _uiState.value
        var isValid = true

        if (state.email.isBlank()) {
            _uiState.update { it.copy(emailError = "El correo es obligatorio") }
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
            _uiState.update { it.copy(emailError = "Formato de correo inválido") }
            isValid = false
        }

        if (state.password.isBlank()) {
            _uiState.update { it.copy(passwordError = "La contraseña es obligatoria") }
            isValid = false
        } else if (state.password.length < 6) {
            _uiState.update { it.copy(passwordError = "Mínimo 6 caracteres") }
            isValid = false
        }

        if (state.confirmPassword != state.password) {
            _uiState.update { it.copy(confirmPasswordError = "Las contraseñas no coinciden") }
            isValid = false
        }

        if (state.name.isBlank()) {
            _uiState.update { it.copy(nameError = "El nombre es obligatorio") }
            isValid = false
        }

        if (state.lastname.isBlank()) {
            _uiState.update { it.copy(lastnameError = "El apellido es obligatorio") }
            isValid = false
        }

        if (state.phone.isBlank()) {
            _uiState.update { it.copy(phoneError = "El teléfono es obligatorio") }
            isValid = false
        } else if (state.phone.length < 7) {
            _uiState.update { it.copy(phoneError = "Teléfono inválido") }
            isValid = false
        }

        return isValid
    }

    // Mantener compatibilidad con método antiguo
    fun registerUser(email: String, password: String) {
        _uiState.update { it.copy(email = email, password = password) }
        registerUser()
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}