package com.ucb.whosin.features.Guest.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val addGuestUseCase: AddGuestUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddGuestUiState())
    val uiState: StateFlow<AddGuestUiState> = _uiState.asStateFlow()

    // Hardcoded eventId para testing
    private val currentEventId = "test-event-123"

    fun addGuest(
        name: String,
        plusOnesAllowed: Int,
        inviteStatus: String,
        note: String?
    ) {
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

            when (val result = addGuestUseCase(currentEventId, guest)) {
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
}