package com.ucb.whosin.features.Guard.data.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.ucb.whosin.features.Guard.data.model.Guest
import com.ucb.whosin.features.Guard.data.repository.GuardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class GuardStats(val checkedIn: Int = 0, val total: Int = 0)

class GuardViewModel(
    private val guardRepository: GuardRepository,
    private val firebaseAuth: FirebaseAuth,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val eventId: String = savedStateHandle.get<String>("eventId") ?: ""

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _guests = MutableStateFlow<List<Guest>>(emptyList())

    private val _checkInStatus = MutableStateFlow<CheckInStatus>(CheckInStatus.Idle)
    val checkInStatus = _checkInStatus.asStateFlow()

    val filteredGuests: StateFlow<List<Guest>> = combine(_guests, _searchQuery) { guests, query ->
        if (query.isBlank()) {
            guests
        } else {
            guests.filter { it.name.contains(query, ignoreCase = true) }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Flujo de datos para las estadísticas
    val stats: StateFlow<GuardStats> = _guests.map { allGuests ->
        GuardStats(
            checkedIn = allGuests.filter { it.checkedIn }.sumOf { it.groupSize },
            total = allGuests.sumOf { it.groupSize }
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, GuardStats())

    init {
        if (eventId.isNotEmpty()) {
            viewModelScope.launch {
                guardRepository.getGuests(eventId).collect { guestsList ->
                    _guests.value = guestsList
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun checkIn(guestId: String) {
        val guardId = firebaseAuth.currentUser?.uid ?: return

        viewModelScope.launch {
            try {
                _checkInStatus.value = CheckInStatus.Loading
                guardRepository.checkInGuest(eventId, guestId, guardId)
                _checkInStatus.value = CheckInStatus.Success
            } catch (e: Exception) {
                _checkInStatus.value = CheckInStatus.Error(e.message ?: "Error al registrar")
            }
        }
    }

    fun checkInByQrCode(qrCode: String) {
        val guardId = firebaseAuth.currentUser?.uid ?: return

        viewModelScope.launch {
            try {
                _checkInStatus.value = CheckInStatus.Loading
                guardRepository.checkInGuestByQrCode(eventId, qrCode, guardId)
                _checkInStatus.value = CheckInStatus.Success
            } catch (e: Exception) {
                _checkInStatus.value = CheckInStatus.Error(e.message ?: "Error al escanear QR")
            }
        }
    }

    fun resetCheckInStatus() {
        _checkInStatus.value = CheckInStatus.Idle
    }
}

sealed class CheckInStatus {
    object Idle : CheckInStatus()
    object Loading : CheckInStatus()
    object Success : CheckInStatus()
    data class Error(val message: String) : CheckInStatus()
}