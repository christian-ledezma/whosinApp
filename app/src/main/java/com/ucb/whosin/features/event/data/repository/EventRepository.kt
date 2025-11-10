package com.ucb.whosin.features.event.data.repository

import com.google.firebase.Timestamp
import com.ucb.whosin.features.event.domain.model.EventModel
import com.ucb.whosin.features.event.domain.repository.IEventRepository

class EventRepository : IEventRepository {
    override fun findByName(value: String): Result<EventModel> {
        return Result.success(
            EventModel(
                eventId = "AfdasfdSjbjthkjkahsd7687dashdfg",
                userId = "SEcvhtB3lEUbYjUAE1Ou7YQyIvI3",
                name = "Cumpleaños de Mordecai",
                date = Timestamp.now(), // 15 de noviembre de 2025
                locationName = "Casa de Mordecai",
                capacity = 20,
                status = "upcoming",
                guardModeEnabled = true,
                createdAt = Timestamp.now(), // 11 de noviembre de 2025
                totalCheckedIn = 0,
                totalInvited = 0
            )
        )
    }
}