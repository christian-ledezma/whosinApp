
package com.ucb.whosin.features.event.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.ucb.whosin.features.event.domain.model.EventModel
import com.ucb.whosin.features.event.domain.usecase.GetEventsWhereUserIsGuardUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class GuardEventsUiState(
    val events: List<EventModel> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class GuardEventsViewModel(
    private val getEventsWhereUserIsGuardUseCase: GetEventsWhereUserIsGuardUseCase,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {
    private val _uiState = MutableStateFlow(GuardEventsUiState())
    val uiState: StateFlow<GuardEventsUiState> = _uiState.asStateFlow()

    fun loadGuardEvents() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Usuario no autenticado",
                isLoading = false
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val events = getEventsWhereUserIsGuardUseCase(currentUser.uid)
                _uiState.value = _uiState.value.copy(
                    events = events,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Error al cargar eventos",
                    isLoading = false
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
