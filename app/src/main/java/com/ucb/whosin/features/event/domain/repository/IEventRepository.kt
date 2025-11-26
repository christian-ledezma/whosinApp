package com.ucb.whosin.features.event.domain.repository

import com.google.firebase.Timestamp
import com.ucb.whosin.features.event.domain.model.EventModel
import com.ucb.whosin.features.event.domain.model.EventResult

interface IEventRepository {
    suspend fun findEventsByName(name: String): List<EventModel>

    suspend fun registerEvent(
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
                             ): EventResult

    suspend fun getEventById(eventId: String): EventResult

    suspend fun getAllEventsByUser(userId: String): List<EventModel>
}