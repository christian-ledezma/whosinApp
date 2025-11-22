package com.ucb.whosin.features.event.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.whosin.features.event.domain.usecase.DeleteEventUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EventSelectorViewModel(
    private val deleteEventUseCase: DeleteEventUseCase
) : ViewModel() {

    private val _deleteResult = MutableStateFlow<Boolean?>(null)
    val deleteResult: StateFlow<Boolean?> = _deleteResult

    fun deleteEvent(eventId: String) {
        viewModelScope.launch {
            val result = deleteEventUseCase(eventId)
            _deleteResult.value = result
        }
    }

    fun clearDeleteStatus() {
        _deleteResult.value = null
    }
}
