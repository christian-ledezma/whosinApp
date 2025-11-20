package com.ucb.whosin.features.Guest.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.ucb.whosin.features.Guard.data.model.Guest
import com.ucb.whosin.features.Guest.domain.model.GuestResult
import com.ucb.whosin.features.Guest.domain.usecase.AddGuestUseCase
import com.ucb.whosin.features.event.domain.model.EventModel
import com.ucb.whosin.features.event.domain.model.EventResult
import com.ucb.whosin.features.event.domain.usecase.GetEventByIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AcceptInvitationUiState(
    val isLoading: Boolean = false,
    val isSearching: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val event: EventModel? = null
)

class AcceptInvitationViewModel(
    private val getEventByIdUseCase: GetEventByIdUseCase,
    private val addGuestUseCase: AddGuestUseCase,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(AcceptInvitationUiState())
    val uiState: StateFlow<AcceptInvitationUiState> = _uiState.asStateFlow()

    fun searchEvent(eventId: String) {
        if (eventId.isBlank()) {
            _uiState.value = _uiState.value.copy(
                event = null,
                errorMessage = "El ID del evento no puede estar vacío"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSearching = true, errorMessage = null)

            when (val result = getEventByIdUseCase(eventId)) {
                is EventResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        event = result.event,
                        isSearching = false,
                        errorMessage = null
                    )
                }
                is EventResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        event = null,
                        isSearching = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }

    fun confirmAttendance(eventId: String, companions: Int) {
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Debes iniciar sesión para confirmar tu asistencia"
            )
            return
        }

        if (_uiState.value.event == null) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "No se ha encontrado el evento"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val guest = Guest(
                userId = currentUser.uid,
                name = currentUser.email ?: "Usuario sin nombre",
                plusOnesAllowed = companions,
                groupSize = companions + 1, // El invitado + acompañantes
                checkedIn = false,
                checkedInAt = null,
                checkedInBy = null,
                qrCode = "",
                inviteStatus = "confirmed",
                note = "Auto-invitado"
            )

            Log.d("AcceptInvitation", "Confirmando asistencia para evento: $eventId")
            Log.d("AcceptInvitation", "Usuario: ${currentUser.email}, Acompañantes: $companions")

            when (val result = addGuestUseCase(eventId, guest)) {
                is GuestResult.Success -> {
                    Log.d("AcceptInvitation", "✅ Asistencia confirmada exitosamente")
                    _uiState.value = _uiState.value.copy(
                        isSuccess = true,
                        isLoading = false
                    )
                }
                is GuestResult.Error -> {
                    Log.e("AcceptInvitation", "❌ Error al confirmar asistencia: ${result.message}")
                    _uiState.value = _uiState.value.copy(
                        errorMessage = result.message,
                        isLoading = false
                    )
                }
                else -> {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Error inesperado al confirmar asistencia",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}