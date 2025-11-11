package com.ucb.whosin.features.Guest.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val searchQuery: String = ""
)

class GuestListViewModel(
    private val getGuestsUseCase: GetGuestsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(GuestListUiState())
    val uiState: StateFlow<GuestListUiState> = _uiState.asStateFlow()

    // Hardcoded eventId para testing - cambiar cuando tengas navegación con el módulo Events
    private val currentEventId = "test-event-123"

    init {
        loadGuests()
    }

    fun loadGuests() {
        viewModelScope.launch {
            _uiState.value = GuestListUiState(isLoading = true)

            when (val result = getGuestsUseCase(currentEventId)) {
                is GuestResult.SuccessList -> {
                    _uiState.value = GuestListUiState(
                        guests = result.guests,
                        filteredGuests = result.guests,
                        isLoading = false
                    )
                }
                is GuestResult.Error -> {
                    _uiState.value = GuestListUiState(
                        errorMessage = result.message,
                        isLoading = false
                    )
                }
                else -> {
                    _uiState.value = GuestListUiState(
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