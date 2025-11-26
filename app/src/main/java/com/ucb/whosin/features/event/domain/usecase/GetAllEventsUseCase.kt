package com.ucb.whosin.features.event.domain.usecase

import com.ucb.whosin.features.event.domain.model.EventModel
import com.ucb.whosin.features.event.domain.repository.IEventRepository

class GetAllEventsUseCase(
    private val repository: IEventRepository
) {
    suspend operator fun invoke(userId: String): List<EventModel> {
        return repository.getAllEventsByUser(userId)
    }
}
