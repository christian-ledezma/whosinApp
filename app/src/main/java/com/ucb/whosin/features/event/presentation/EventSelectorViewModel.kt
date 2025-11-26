package com.ucb.whosin.features.event.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.whosin.features.event.domain.model.EventModel
import com.ucb.whosin.features.event.domain.usecase.DeleteEventUseCase
import com.ucb.whosin.features.event.domain.usecase.GetAllEventsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EventSelectorViewModel(
    private val deleteEventUseCase: DeleteEventUseCase,
    private val getAllEventsUseCase: GetAllEventsUseCase
) : ViewModel() {

    private val _deleteResult = MutableStateFlow<Boolean?>(null)
    val deleteResult: StateFlow<Boolean?> = _deleteResult

    private val _events = MutableStateFlow<List<EventModel>>(emptyList())
    val events: StateFlow<List<EventModel>> = _events

    fun loadEvents(userId: String) {
        viewModelScope.launch {
            _events.value = getAllEventsUseCase(userId)
        }
    }

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

