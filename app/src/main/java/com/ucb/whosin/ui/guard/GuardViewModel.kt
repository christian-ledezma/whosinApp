package com.ucb.whosin.ui.guard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.whosin.data.model.Guest
import com.ucb.whosin.data.repository.GuardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class GuardViewModel(private val guardRepository: GuardRepository) : ViewModel() {

    private val _guests = MutableStateFlow<List<Guest>>(emptyList())
    val guests: StateFlow<List<Guest>> = _guests.asStateFlow()

    // TODO: Replace with dynamic event ID
    private val eventId = "your_event_id"

    init {
        viewModelScope.launch {
            guardRepository.getGuests(eventId).collectLatest {
                _guests.value = it
            }
        }
    }

    fun checkIn(guestId: String) {
        viewModelScope.launch {
            // TODO: Replace with dynamic guard ID
            val guardId = "your_guard_id"
            guardRepository.checkInGuest(eventId, guestId, guardId)
        }
    }
}