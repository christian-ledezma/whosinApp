package com.ucb.whosin.features.event.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.ucb.whosin.features.event.domain.model.EventModel
import com.ucb.whosin.features.event.domain.usecase.DeleteEventUseCase
import com.ucb.whosin.features.event.domain.usecase.CancelEventUseCase
import com.ucb.whosin.features.event.domain.usecase.FindEventsByNameUseCase
import com.ucb.whosin.features.event.domain.usecase.GetAllEventsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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
            val events = getAllEventsUseCase(userId)

            // Calcular totales reales basados en groupSize
            val eventsWithCalculatedTotals = events.map { event ->
                val guestsData = calculateGuestsDataForEvent(event.eventId)
                event.copy(
                    calculatedTotalInvited = guestsData.first,
                    totalCheckedIn = guestsData.second
                )
            }

            _events.value = eventsWithCalculatedTotals
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

    private suspend fun calculateGuestsDataForEvent(eventId: String): Pair<Int, Int> {
        return try {
            val snapshot = FirebaseFirestore.getInstance()
                .collection("events")
                .document(eventId)
                .collection("guests")
                .get()
                .await()

            var totalInvited = 0
            var totalCheckedIn = 0

            snapshot.documents.forEach { doc ->
                val groupSize = doc.getLong("groupSize")?.toInt() ?: 0
                val isCheckedIn = doc.getBoolean("checkedIn") ?: false

                totalInvited += groupSize
                if (isCheckedIn) {
                    totalCheckedIn += groupSize
                }
            }

            Pair(totalInvited, totalCheckedIn)
        } catch (e: Exception) {
            Log.e("EventSelectorVM", "Error calculando datos de invitados", e)
            Pair(0, 0)
        }
    }
}

