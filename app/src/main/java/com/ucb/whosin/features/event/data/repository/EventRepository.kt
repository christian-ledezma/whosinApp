package com.ucb.whosin.features.event.data.repository

import com.google.firebase.Timestamp
import com.ucb.whosin.features.event.data.datasource.FirebaseEventDataSource
import com.ucb.whosin.features.event.domain.model.EventModel
import com.ucb.whosin.features.event.domain.model.EventResult
import com.ucb.whosin.features.event.domain.model.GuardResult
import com.ucb.whosin.features.event.domain.repository.IEventRepository

class EventRepository(
    private val dataSource: FirebaseEventDataSource
) : IEventRepository {

    override suspend fun registerEvent(
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
    ): EventResult {
        val event = EventModel(
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

        return dataSource.register(event)
    }

    override suspend fun findEventsByName(name: String): List<EventModel> {
        return dataSource.findEventsByName(name)
    }


    override suspend fun getEventById(eventId: String): EventResult {
        return dataSource.getEventById(eventId)
    }

    override suspend fun getAllEventsByUser(userId: String): List<EventModel> {
        return dataSource.getAllEventsByUser(userId)
    }

    override suspend fun updateEvent(
        eventId: String,
        name: String,
        date: Timestamp,
        locationName: String,
        latitude: Double,
        longitude: Double,
        capacity: Int
    ): EventResult {
        return dataSource.updateEvent(
            eventId = eventId,
            name = name,
            date = date,
            locationName = locationName,
            latitude = latitude,
            longitude = longitude,
            capacity = capacity
        )
    }

    override suspend fun cancelEvent(eventId: String): EventResult {
        return dataSource.cancelEvent(eventId)
    }

    override suspend fun addGuard(
        eventId: String,
        guardEmail: String,
        addedBy: String
    ): GuardResult {
        return dataSource.addGuard(eventId, guardEmail, addedBy)
    }

    override suspend fun removeGuard(eventId: String, guardId: String): GuardResult {
        return dataSource.removeGuard(eventId, guardId)
    }

    override suspend fun getEventGuards(eventId: String): GuardResult {
        return dataSource.getEventGuards(eventId)
    }

    override suspend fun getEventsWhereUserIsGuard(guardId: String): List<EventModel> {
        return dataSource.getEventsWhereUserIsGuard(guardId)
    }

}
