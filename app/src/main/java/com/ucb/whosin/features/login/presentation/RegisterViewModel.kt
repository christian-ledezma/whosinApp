package com.ucb.whosin.features.login.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.whosin.features.login.domain.model.AuthResult
import com.ucb.whosin.features.login.domain.repository.AuthRepository
import com.ucb.whosin.features.login.domain.usecase.RegisterUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


data class RegisterUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

class RegisterViewModel(
    private val registerUserUseCase: RegisterUserUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun registerUser(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = RegisterUiState(isLoading = true)

            when (val result = registerUserUseCase(email, password)) {
                is AuthResult.Success -> {
                    authRepository.saveSession(
                        result.user.uid,
                        result.user.email
                    )
                    _uiState.value = RegisterUiState(isSuccess = true)
                }
                is AuthResult.Error -> {
                    _uiState.value = RegisterUiState(errorMessage = result.message)
                }
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}