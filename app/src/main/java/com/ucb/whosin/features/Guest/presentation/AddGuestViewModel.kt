package com.ucb.whosin.features.Guest.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.ucb.whosin.features.Guest.domain.model.Guest
import com.ucb.whosin.features.Guest.domain.model.GuestResult
import com.ucb.whosin.features.Guest.domain.usecase.AddGuestUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AddGuestUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

class AddGuestViewModel(
    private val addGuestUseCase: AddGuestUseCase,
    private val firebaseAuth: FirebaseAuth,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddGuestUiState())
    val uiState: StateFlow<AddGuestUiState> = _uiState.asStateFlow()

    // Obtener eventId desde la navegación, o usar el hardcoded para testing
    private val eventId: String = savedStateHandle.get<String>("eventId") ?: "test-event-123"

    fun addGuest(
        name: String,
        plusOnesAllowed: Int,
        inviteStatus: String,
        note: String?
    ) {
        if (firebaseAuth.currentUser == null) {
            _uiState.value = AddGuestUiState(
                errorMessage = "No hay usuario autenticado"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = AddGuestUiState(isLoading = true)

            val guest = Guest(
                userId = null,
                name = name,
                plusOnesAllowed = plusOnesAllowed,
                groupSize = 0,
                checkedIn = false,
                checkedInAt = null,
                checkedInBy = null,
                qrCode = "",
                inviteStatus = inviteStatus,
                note = note
            )

            when (val result = addGuestUseCase(eventId, guest)) {
                is GuestResult.Success -> {
                    _uiState.value = AddGuestUiState(isSuccess = true)
                }
                is GuestResult.Error -> {
                    _uiState.value = AddGuestUiState(errorMessage = result.message)
                }
                else -> {
                    _uiState.value = AddGuestUiState(errorMessage = "Error inesperado")
                }
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    fun clearSuccess() {
        _uiState.value = _uiState.value.copy(isSuccess = false)
    }
}