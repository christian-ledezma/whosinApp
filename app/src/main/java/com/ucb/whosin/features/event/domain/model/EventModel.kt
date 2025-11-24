package com.ucb.whosin.features.event.domain.model

import com.google.firebase.Timestamp

data class EventModel(
    val eventId: String = "",
    val userId: String = "",
    val name: String = "",
    val date: Timestamp = Timestamp.now(),
    val locationName: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val capacity: Int = 0,
    val status: String = "",
    val guardModeEnabled: Boolean = false,
    val createdAt: Timestamp = Timestamp.now(),
    val totalCheckedIn: Int = 0,
    val totalInvited: Int = 0
)

sealed class EventResult {
    data class Success(val event: EventModel) : EventResult()
    data class Error(val message: String) : EventResult()
}