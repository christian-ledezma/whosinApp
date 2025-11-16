package com.ucb.whosin.features.event.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.ucb.whosin.features.Guest.domain.model.Guest
import com.ucb.whosin.features.Guest.domain.model.GuestResult
import com.ucb.whosin.features.Guest.domain.usecase.GetGuestsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class GuestListUiState(
    val guests: List<Guest> = emptyList(),
    val filteredGuests: List<Guest> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val isAuthenticated: Boolean = false
)

class GuestListViewModel(
    private val getGuestsUseCase: GetGuestsUseCase,
    private val firebaseAuth: FirebaseAuth,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(GuestListUiState())
    val uiState: StateFlow<GuestListUiState> = _uiState.asStateFlow()

    // Obtener eventId desde la navegación, o usar el hardcoded para testing
    private val eventId: String = savedStateHandle.get<String>("eventId") ?: "test-event-123"

    init {
        checkAuthentication()
        if (firebaseAuth.currentUser != null) {
            loadGuests()
        }
    }

    private fun checkAuthentication() {
        val isAuth = firebaseAuth.currentUser != null
        _uiState.value = _uiState.value.copy(isAuthenticated = isAuth)

        if (!isAuth) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Debes iniciar sesión para ver los invitados"
            )
        }
    }

    fun loadGuests() {
        if (firebaseAuth.currentUser == null) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "No hay usuario autenticado",
                isLoading = false
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            when (val result = getGuestsUseCase(eventId)) {
                is GuestResult.SuccessList -> {
                    _uiState.value = _uiState.value.copy(
                        guests = result.guests,
                        filteredGuests = result.guests,
                        isLoading = false,
                        isAuthenticated = true
                    )
                }
                is GuestResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = result.message,
                        isLoading = false
                    )
                }
                else -> {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Error inesperado",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        val filtered = if (query.isBlank()) {
            _uiState.value.guests
        } else {
            _uiState.value.guests.filter {
                it.name.contains(query, ignoreCase = true)
            }
        }

        _uiState.value = _uiState.value.copy(
            searchQuery = query,
            filteredGuests = filtered
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}