package com.ucb.whosin.features.event.data.repository

import com.google.firebase.Timestamp
import com.ucb.whosin.features.event.data.datasource.FirebaseEventDataSource
import com.ucb.whosin.features.event.domain.model.EventModel
import com.ucb.whosin.features.event.domain.model.EventResult
import com.ucb.whosin.features.event.domain.repository.IEventRepository

class EventRepository(
    private val dataSource: FirebaseEventDataSource
) : IEventRepository {

    override suspend fun registerEvent(
        eventId: String,
        name: String,
        date: Timestamp,
        locationName: String,
        capacity: Int,
        status: String,
        guardModeEnabled: Boolean,
        createdAt: Timestamp,
        totalCheckedIn: Int,
        totalInvited: Int
    ): EventResult {
        val event = EventModel(
            eventId = eventId,
            name = name,
            date = date,
            locationName = locationName,
            capacity = capacity,
            status = status,
            guardModeEnabled = guardModeEnabled,
            createdAt = createdAt,
            totalCheckedIn = totalCheckedIn,
            totalInvited = totalInvited
        )

        return dataSource.register(event)
    }

    override suspend fun findByName(value: String): EventResult {
        // 🔍 Ejemplo de búsqueda simple por nombre (por si quieres implementarlo después)
        // Se puede dejar vacío o implementarlo en tu DataSource luego.
        return EventResult.Error("Función findByName() no implementada aún")
    }

    override suspend fun getEventById(eventId: String): EventResult {
        return dataSource.getEventById(eventId)
    }
}
