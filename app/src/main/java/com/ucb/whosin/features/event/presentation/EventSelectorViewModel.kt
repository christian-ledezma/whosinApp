package com.ucb.whosin.features.event.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.whosin.features.event.domain.model.EventModel
import com.ucb.whosin.features.event.domain.usecase.DeleteEventUseCase
import com.ucb.whosin.features.event.domain.usecase.CancelEventUseCase
import com.ucb.whosin.features.event.domain.usecase.FindEventsByNameUseCase
import com.ucb.whosin.features.event.domain.usecase.GetAllEventsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EventSelectorViewModel(
    private val deleteEventUseCase: DeleteEventUseCase,
    private val getAllEventsUseCase: GetAllEventsUseCase,
    private val findEventsByNameUseCase: FindEventsByNameUseCase,
    private val cancelEventUseCase: CancelEventUseCase
) : ViewModel() {

    private val _events = MutableStateFlow<List<EventModel>>(emptyList())
    val events: StateFlow<List<EventModel>> = _events

    private val _deleteResult = MutableStateFlow<Boolean?>(null)
    val deleteResult: StateFlow<Boolean?> = _deleteResult

    fun loadEvents(userId: String) {
        viewModelScope.launch {
            _events.value = getAllEventsUseCase(userId)
        }
    }

    fun searchEvents(name: String) {
        viewModelScope.launch {
            _events.value = findEventsByNameUseCase(name)
        }
    }

    fun deleteEvent(eventId: String) {
        viewModelScope.launch {
            val result = cancelEventUseCase(eventId)
            _deleteResult.value = result is com.ucb.whosin.features.event.domain.model.EventResult.Success
        }
    }

    fun clearDeleteStatus() {
        _deleteResult.value = null
    }
}

