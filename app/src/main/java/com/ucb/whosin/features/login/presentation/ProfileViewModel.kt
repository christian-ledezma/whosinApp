package com.ucb.whosin.features.login.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.ucb.whosin.features.login.domain.model.CountryCode
import com.ucb.whosin.features.login.domain.model.User
import com.ucb.whosin.features.login.domain.usecase.ChangePasswordUseCase
import com.ucb.whosin.features.login.domain.usecase.GetUserProfileUseCase
import com.ucb.whosin.features.login.domain.usecase.UpdateUserProfileUseCase
import com.ucb.whosin.features.login.domain.vo.CountryCodeValue
import com.ucb.whosin.features.login.domain.vo.Email
import com.ucb.whosin.features.login.domain.vo.OptionalPersonName
import com.ucb.whosin.features.login.domain.vo.PersonName
import com.ucb.whosin.features.login.domain.vo.PhoneNumber
import com.ucb.whosin.features.login.domain.vo.UserId
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

            val result = getUserProfileUseCase(userId)

            result.fold(
                onSuccess = { user ->
                    val countryCode = CountryCode.entries.find { it.code == user.countryCode.value }
                        ?: CountryCode.BO

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            user = user,
                            name = user.name.value,
                            lastname = user.lastname.value,
                            secondLastname = user.secondLastname.value ?: "",
                            phone = user.phoneNumber.value,
                            selectedCountryCode = countryCode,
                            email = user.email.value
                        )
                    }
                },
                onFailure = { error ->
                    val email = firebaseAuth.currentUser?.email ?: ""
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            email = email,
                            errorMessage = error.message
                        )
                    }
                }
            )
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

            val userIdResult = UserId.create(userId)
            val emailResult = Email.create(state.email)
            val nameResult = PersonName.create(state.name.trim(), "nombre")
            val lastnameResult = PersonName.create(state.lastname.trim(), "apellido")
            val secondLastnameResult = OptionalPersonName.create(state.secondLastname.trim().ifBlank { null })
            val countryCodeResult = CountryCodeValue.fromEnum(state.selectedCountryCode)
            val phoneResult = PhoneNumber.create(state.phone.trim(), countryCodeResult.value)

            if (userIdResult.isFailure || emailResult.isFailure || nameResult.isFailure ||
                lastnameResult.isFailure || secondLastnameResult.isFailure || phoneResult.isFailure) {

                val error = listOf(
                    userIdResult.exceptionOrNull(),
                    emailResult.exceptionOrNull(),
                    nameResult.exceptionOrNull(),
                    lastnameResult.exceptionOrNull(),
                    secondLastnameResult.exceptionOrNull(),
                    phoneResult.exceptionOrNull()
                ).firstNotNullOfOrNull { it }

                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = error?.message ?: "Error de validación"
                    )
                }
                return@launch
            }

            val updatedUser = User.create(
                uid = userIdResult.getOrThrow(),
                email = emailResult.getOrThrow(),
                name = nameResult.getOrThrow(),
                lastname = lastnameResult.getOrThrow(),
                secondLastname = secondLastnameResult.getOrThrow(),
                phoneNumber = phoneResult.getOrThrow(),
                countryCode = countryCodeResult,
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