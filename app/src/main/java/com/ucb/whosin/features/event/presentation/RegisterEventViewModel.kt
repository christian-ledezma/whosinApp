package com.ucb.whosin.features.event.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.ucb.whosin.features.event.domain.model.EventResult
import com.ucb.whosin.features.event.domain.usecase.RegisterEventUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RegisterEventUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

class RegisterEventViewModel(
    private val registerEventUseCase: RegisterEventUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterEventUiState())
    val uiState: StateFlow<RegisterEventUiState> = _uiState.asStateFlow()

    fun registerEvent(
        eventId: String,
        name: String,
        date: Timestamp,
        locationName: String,
        latitude: Double,
        longitude: Double,
        capacity: Int,
        status: String,
        guardModeEnabled: Boolean,
        createdAt: Timestamp,
        totalCheckedIn: Int,
        totalInvited: Int
    ) {
        viewModelScope.launch {
            _uiState.value = RegisterEventUiState(isLoading = true)

            val result = registerEventUseCase(
                eventId = eventId,
                name = name,
                date = date,
                locationName = locationName,
                latitude = latitude,
                longitude = longitude,
                capacity = capacity,
                status = status,
                guardModeEnabled = guardModeEnabled,
                createdAt = createdAt,
                totalCheckedIn = totalCheckedIn,
                totalInvited = totalInvited
            )

            when (result) {
                is EventResult.Success -> {
                    _uiState.value = RegisterEventUiState(isSuccess = true)
                }
                is EventResult.Error -> {
                    _uiState.value = RegisterEventUiState(errorMessage = result.message)
                }
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun resetState() {
        _uiState.value = RegisterEventUiState()
    }
}
