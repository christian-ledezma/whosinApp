
package com.ucb.whosin.features.event.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.ucb.whosin.features.event.domain.model.EventModel
import com.ucb.whosin.features.event.domain.repository.IEventRepository
import com.ucb.whosin.features.event.domain.usecase.GetEventsWhereUserIsGuardUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class GuardEventsUiState(
    val events: List<EventModel> = emptyList(),
    val totalInvitedByEvent: Map<String, Int> = emptyMap(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class GuardEventsViewModel(
    private val getEventsWhereUserIsGuardUseCase: GetEventsWhereUserIsGuardUseCase,
    private val firebaseAuth: FirebaseAuth,
    private val eventRepository: IEventRepository
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

                val totalsMap = mutableMapOf<String, Int>()
                events.forEach { event ->
                    totalsMap[event.eventId] = calculateTotalGuestsForEvent(event.eventId)
                }

                _uiState.value = _uiState.value.copy(
                    events = events,
                    totalInvitedByEvent = totalsMap,
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

    private suspend fun calculateTotalGuestsForEvent(eventId: String): Int {
        return try {
            val snapshot = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                .collection("events")
                .document(eventId)
                .collection("guests")
                .get()
                .await()

            snapshot.documents.sumOf { doc ->
                doc.getLong("groupSize")?.toInt() ?: 0
            }
        } catch (e: Exception) {
            Log.e("GuardEventsVM", "Error calculando total invitados", e)
            0
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
