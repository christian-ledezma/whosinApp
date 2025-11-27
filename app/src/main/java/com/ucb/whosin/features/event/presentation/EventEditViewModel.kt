package com.ucb.whosin.features.event.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.ucb.whosin.features.event.domain.model.AssignedGuard
import com.ucb.whosin.features.event.domain.model.EventModel
import com.ucb.whosin.features.event.domain.model.EventResult
import com.ucb.whosin.features.event.domain.model.GuardResult
import com.ucb.whosin.features.event.domain.usecase.AddGuardUseCase
import com.ucb.whosin.features.event.domain.usecase.GetEventByIdUseCase
import com.ucb.whosin.features.event.domain.usecase.GetEventGuardsUseCase
import com.ucb.whosin.features.event.domain.usecase.RemoveGuardUseCase
import com.ucb.whosin.features.event.domain.usecase.UpdateEventUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EventEditUiState(
    val event: EventModel? = null,
    val guards: List<AssignedGuard> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isAddingGuard: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val selectedTab: Int = 0 // 0: Datos del evento, 1: Guardias
)

class EventEditViewModel(
    private val getEventByIdUseCase: GetEventByIdUseCase,
    private val updateEventUseCase: UpdateEventUseCase,
    private val addGuardUseCase: AddGuardUseCase,
    private val removeGuardUseCase: RemoveGuardUseCase,
    private val getEventGuardsUseCase: GetEventGuardsUseCase,
    private val firebaseAuth: FirebaseAuth,
    savedStateHandle: SavedStateHandle
) : ViewModel()  {
    val eventId: String = savedStateHandle.get<String>("eventId") ?: ""

    private val _uiState = MutableStateFlow(EventEditUiState())
    val uiState: StateFlow<EventEditUiState> = _uiState.asStateFlow()

    init {
        loadEvent()
        loadGuards()
    }

    private fun loadEvent() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = getEventByIdUseCase(eventId)) {
                is EventResult.Success -> {
                    _uiState.update {
                        it.copy(
                            event = result.event,
                            isLoading = false
                        )
                    }
                }
                is EventResult.Error -> {
                    _uiState.update {
                        it.copy(
                            errorMessage = result.message,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    private fun loadGuards() {
        viewModelScope.launch {
            when (val result = getEventGuardsUseCase(eventId)) {
                is GuardResult.SuccessList -> {
                    _uiState.update { it.copy(guards = result.assignedGuard) }
                }
                is GuardResult.Error -> {
                    // Error silencioso para guardias (puede no haber ninguno)
                }
                else -> {}
            }
        }
    }

    fun updateEvent(
        name: String,
        date: Timestamp,
        locationName: String,
        latitude: Double,
        longitude: Double,
        capacity: Int
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null, successMessage = null) }

            when (val result = updateEventUseCase(
                eventId = eventId,
                name = name,
                date = date,
                locationName = locationName,
                latitude = latitude,
                longitude = longitude,
                capacity = capacity
            )) {
                is EventResult.Success -> {
                    _uiState.update {
                        it.copy(
                            event = result.event,
                            isSaving = false,
                            successMessage = "Evento actualizado correctamente"
                        )
                    }
                }
                is EventResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }

    fun addGuard(guardEmail: String) {
        val currentUserId = firebaseAuth.currentUser?.uid ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isAddingGuard = true, errorMessage = null) }

            when (val result = addGuardUseCase(eventId, guardEmail, currentUserId)) {
                is GuardResult.Success -> {
                    loadGuards() // Recargar lista
                    _uiState.update {
                        it.copy(
                            isAddingGuard = false,
                            successMessage = "Guardia agregado: ${result.assignedGuard.fullName}"
                        )
                    }
                }
                is GuardResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isAddingGuard = false,
                            errorMessage = result.message
                        )
                    }
                }
                else -> {}
            }
        }
    }

    fun removeGuard(guardId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(errorMessage = null) }

            when (val result = removeGuardUseCase(eventId, guardId)) {
                is GuardResult.Success -> {
                    loadGuards() // Recargar lista
                    _uiState.update {
                        it.copy(successMessage = "Guardia removido correctamente")
                    }
                }
                is GuardResult.Error -> {
                    _uiState.update {
                        it.copy(errorMessage = result.message)
                    }
                }
                else -> {}
            }
        }
    }

    fun selectTab(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}