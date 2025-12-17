package com.ucb.whosin.features.Guest.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.ucb.whosin.features.Guest.domain.model.Guest
import com.ucb.whosin.features.Guest.domain.model.GuestResult
import com.ucb.whosin.features.Guest.domain.usecase.AddGuestUseCase
import com.ucb.whosin.features.Guest.domain.usecase.GetGuestsUseCase
import com.ucb.whosin.features.event.domain.model.EventModel
import com.ucb.whosin.features.event.domain.model.EventResult
import com.ucb.whosin.features.event.domain.usecase.GetEventByIdUseCase
import com.ucb.whosin.features.login.datasource.FirebaseAuthDataSourceImp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AcceptInvitationUiState(
    val isLoading: Boolean = false,
    val isSearching: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val event: EventModel? = null,
    val isUserAlreadyInvited: Boolean = false,
    val guestQrCode: String? = null
)

class AcceptInvitationViewModel(
    private val getEventByIdUseCase: GetEventByIdUseCase,
    private val addGuestUseCase: AddGuestUseCase,
    private val firebaseAuth: FirebaseAuth,
    private val firebaseAuthDataSource: FirebaseAuthDataSourceImp,
    private val getGuestsUseCase: GetGuestsUseCase
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

                    checkIfUserIsInvited(eventId)
                }

                is EventResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        event = null,
                        isSearching = false,
                        errorMessage = result.message,
                        isUserAlreadyInvited = false
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

            val userProfileResult = firebaseAuthDataSource.getUserProfile(currentUser.uid)

            if (userProfileResult.isFailure) {
                Log.e("AcceptInvitation", "❌ No se pudo obtener el perfil del usuario")
                _uiState.value = _uiState.value.copy(
                    errorMessage = "No se pudo obtener tu perfil. Por favor, intenta más tarde.",
                    isLoading = false
                )
                return@launch
            }

            val userProfile = userProfileResult.getOrThrow()

            if (userProfile.name.value.isBlank() || userProfile.lastname.value.isBlank()) {
                Log.e("AcceptInvitation", "❌ Perfil incompleto: name='${userProfile.name}', lastname='${userProfile.lastname}'")
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Tu perfil está incompleto. Por favor, completa tus datos personales en la configuración de tu cuenta antes de aceptar invitaciones.",
                    isLoading = false
                )
                return@launch
            }

            val fullName = userProfile.fullName()
            Log.d("AcceptInvitation", "✅ Perfil completo obtenido: $fullName")


            val guest = Guest(
                userId = currentUser.uid,
                name = fullName,
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
                    val confirmedGuest = result.guest
                    _uiState.value = _uiState.value.copy(
                        isSuccess = true,
                        isLoading = false,
                        guestQrCode = confirmedGuest?.qrCode
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

    private fun checkIfUserIsInvited(eventId: String) {
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            _uiState.value = _uiState.value.copy(isUserAlreadyInvited = false)
            return
        }

        viewModelScope.launch {
            when (val result = getGuestsUseCase(eventId)) {
                is GuestResult.SuccessList -> {
                    // Validar si el userId actual ya está en la lista
                    val guest = result.guests.find { it.userId == currentUser.uid }
                    val isInvited = guest != null

                    _uiState.value = _uiState.value.copy(
                        isUserAlreadyInvited = isInvited,
                        guestQrCode = guest?.qrCode
                    )

                    Log.d("AcceptInvitation", "Usuario ${currentUser.uid} ya invitado: $isInvited")
                }
                is GuestResult.Error -> {
                    // Si falla la consulta, asumimos que no está invitado (permite continuar)
                    _uiState.value = _uiState.value.copy(isUserAlreadyInvited = false)
                    Log.e("AcceptInvitation", "Error al validar invitado: ${result.message}")
                }
                else -> {
                    _uiState.value = _uiState.value.copy(isUserAlreadyInvited = false)
                }
            }
        }
    }

    private fun loadGuestQrCode(eventId: String) {
        val currentUser = firebaseAuth.currentUser ?: return

        viewModelScope.launch {
            when (val result = getGuestsUseCase(eventId)) {
                is GuestResult.SuccessList -> {
                    val guest = result.guests.find { it.userId == currentUser.uid }
                    _uiState.value = _uiState.value.copy(
                        guestQrCode = guest?.qrCode
                    )
                    Log.d("AcceptInvitation", "QR Code cargado: ${guest?.qrCode}")
                }
                else -> {
                    Log.e("AcceptInvitation", "Error al cargar QR Code")
                }
            }
        }
    }
}