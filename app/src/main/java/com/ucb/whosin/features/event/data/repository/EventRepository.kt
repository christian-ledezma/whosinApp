package com.ucb.whosin.features.event.data.repository

import com.google.firebase.Timestamp
import com.ucb.whosin.features.event.domain.model.EventModel
import com.ucb.whosin.features.event.domain.repository.IEventRepository

class EventRepository : IEventRepository {
    override fun findByName(value: String): Result<EventModel> {
        return Result.success(EventModel(
            "Sample Event",
            Timestamp.now(),
            "Active",
            "Sample Location Xddd"
        ))
    }
}