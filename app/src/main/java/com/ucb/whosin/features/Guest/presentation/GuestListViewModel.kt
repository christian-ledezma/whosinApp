
package com.ucb.whosin.features.Guest.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.ucb.whosin.features.Guest.domain.model.Guest
import com.ucb.whosin.features.Guest.domain.model.GuestResult
import com.ucb.whosin.features.Guest.domain.usecase.AddGuestUseCase
import com.ucb.whosin.features.Guest.domain.usecase.DeleteGuestUseCase
import com.ucb.whosin.features.Guest.domain.usecase.GetGuestsUseCase
import com.ucb.whosin.features.Guest.domain.usecase.UpdateGuestUseCase
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
    val updateSuccess: Boolean = false,
    val deleteSuccess: Boolean = false
)

class GuestListViewModel(
    private val getGuestsUseCase: GetGuestsUseCase,
    private val updateGuestUseCase: UpdateGuestUseCase,
    private val deleteGuestUseCase: DeleteGuestUseCase,
    private val addGuestUseCase: AddGuestUseCase,
    private val firebaseAuth: FirebaseAuth,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(GuestListUiState())
    val uiState: StateFlow<GuestListUiState> = _uiState.asStateFlow()

    private val eventId: String = savedStateHandle["eventId"]
        ?: throw IllegalStateException("eventId no fue pasado")

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
                        isLoading = false
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

    fun addGuest(name: String, companions: Int) {
        if (firebaseAuth.currentUser == null) {
            _uiState.value = _uiState.value.copy(errorMessage = "No hay usuario autenticado")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val guest = Guest(name = name, plusOnesAllowed = companions, groupSize = companions + 1)

            when (val result = addGuestUseCase(eventId, guest)) {
                is GuestResult.Success -> {
                    // Recargar la lista para mostrar el nuevo invitado
                    loadGuests()
                }
                is GuestResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                else -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Error inesperado al agregar invitado"
                    )
                }
            }
        }
    }

    fun updateGuest(guestId: String, newName: String, newCompanions: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, updateSuccess = false)
            val currentGuest = _uiState.value.guests.find { it.guestId == guestId }
            if (currentGuest == null) {
                _uiState.value = _uiState.value.copy(errorMessage = "No se encontró el invitado", isLoading = false)
                return@launch
            }

            val updatedGuest = currentGuest.copy(name = newName, plusOnesAllowed = newCompanions, groupSize = newCompanions + 1)

            when (val result = updateGuestUseCase(eventId, guestId, updatedGuest)) {
                is GuestResult.Success -> {
                    _uiState.value = _uiState.value.copy(updateSuccess = true, isLoading = false)
                }
                is GuestResult.Error -> {
                    _uiState.value = _uiState.value.copy(errorMessage = result.message, isLoading = false)
                }
                else -> {
                    _uiState.value = _uiState.value.copy(errorMessage = "Error inesperado al actualizar", isLoading = false)
                }
            }
        }
    }

    fun deleteGuest(guestId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, deleteSuccess = false)
            when (val result = deleteGuestUseCase(eventId, guestId)) {
                is GuestResult.Success -> {
                    _uiState.value = _uiState.value.copy(deleteSuccess = true, isLoading = false)
                }
                is GuestResult.Error -> {
                    _uiState.value = _uiState.value.copy(errorMessage = result.message, isLoading = false)
                }
                else -> {
                    _uiState.value = _uiState.value.copy(errorMessage = "Error inesperado al eliminar", isLoading = false)
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        val filtered = if (query.isBlank()) {
            _uiState.value.guests
        } else {
            _uiState.value.guests.filter { it.name.contains(query, ignoreCase = true) }
        }
        _uiState.value = _uiState.value.copy(searchQuery = query, filteredGuests = filtered)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun resetSuccessFlags() {
        _uiState.value = _uiState.value.copy(updateSuccess = false, deleteSuccess = false)
    }
}