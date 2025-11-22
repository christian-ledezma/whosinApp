package com.ucb.whosin.features.login.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.ucb.whosin.features.login.domain.model.CountryCode
import com.ucb.whosin.features.login.domain.model.User
import com.ucb.whosin.features.login.domain.usecase.ChangePasswordUseCase
import com.ucb.whosin.features.login.domain.usecase.GetUserProfileUseCase
import com.ucb.whosin.features.login.domain.usecase.UpdateUserProfileUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val user: User? = null,
    val name: String = "",
    val lastname: String = "",
    val secondLastname: String = "",
    val phone: String = "",
    val selectedCountryCode: CountryCode = CountryCode.BO,
    val email: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val showCountryPicker: Boolean = false,
    // Cambio de contraseña
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isChangingPassword: Boolean = false,
    val passwordChangeSuccess: Boolean = false
)

class ProfileViewModel(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val changePasswordUseCase: ChangePasswordUseCase,
    private val firebaseAuth: FirebaseAuth
) : ViewModel()  {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        val userId = firebaseAuth.currentUser?.uid ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val user = getUserProfileUseCase(userId)

            if (user != null) {
                val countryCode = CountryCode.entries.find { it.code == user.countryCode }
                    ?: CountryCode.BO

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        user = user,
                        name = user.name,
                        lastname = user.lastname,
                        secondLastname = user.secondLastname ?: "",
                        phone = user.phone,
                        selectedCountryCode = countryCode,
                        email = user.email
                    )
                }
            } else {
                // Usuario sin perfil completo
                val email = firebaseAuth.currentUser?.email ?: ""
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        email = email,
                        user = User(uid = userId, email = email)
                    )
                }
            }
        }
    }

    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name, errorMessage = null) }
    }

    fun onLastnameChange(lastname: String) {
        _uiState.update { it.copy(lastname = lastname, errorMessage = null) }
    }

    fun onSecondLastnameChange(secondLastname: String) {
        _uiState.update { it.copy(secondLastname = secondLastname) }
    }

    fun onPhoneChange(phone: String) {
        val cleanPhone = phone.filter { it.isDigit() }
        _uiState.update { it.copy(phone = cleanPhone, errorMessage = null) }
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

    fun saveProfile() {
        val state = _uiState.value
        val userId = firebaseAuth.currentUser?.uid ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null, successMessage = null) }

            val updatedUser = User(
                uid = userId,
                email = state.email,
                name = state.name.trim(),
                lastname = state.lastname.trim(),
                secondLastname = state.secondLastname.trim().ifBlank { null },
                phone = state.phone.trim(),
                countryCode = state.selectedCountryCode.code,
                createdAt = state.user?.createdAt
            )

            val result = updateUserProfileUseCase(updatedUser)

            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            user = updatedUser,
                            successMessage = "Perfil actualizado correctamente"
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = error.message ?: "Error al actualizar"
                        )
                    }
                }
            )
        }
    }

    // Cambio de contraseña
    fun onCurrentPasswordChange(password: String) {
        _uiState.update { it.copy(currentPassword = password, errorMessage = null) }
    }

    fun onNewPasswordChange(password: String) {
        _uiState.update { it.copy(newPassword = password, errorMessage = null) }
    }

    fun onConfirmPasswordChange(password: String) {
        _uiState.update { it.copy(confirmPassword = password, errorMessage = null) }
    }

    fun changePassword() {
        val state = _uiState.value

        viewModelScope.launch {
            _uiState.update { it.copy(isChangingPassword = true, errorMessage = null) }

            val result = changePasswordUseCase(
                currentPassword = state.currentPassword,
                newPassword = state.newPassword,
                confirmPassword = state.confirmPassword
            )

            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isChangingPassword = false,
                            passwordChangeSuccess = true,
                            currentPassword = "",
                            newPassword = "",
                            confirmPassword = "",
                            successMessage = "Contraseña cambiada correctamente"
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isChangingPassword = false,
                            errorMessage = error.message ?: "Error al cambiar contraseña"
                        )
                    }
                }
            )
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }

    fun resetPasswordChangeSuccess() {
        _uiState.update { it.copy(passwordChangeSuccess = false) }
    }

}