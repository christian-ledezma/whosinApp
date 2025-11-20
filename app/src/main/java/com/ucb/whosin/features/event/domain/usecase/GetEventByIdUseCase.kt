package com.ucb.whosin.features.event.domain.usecase

import com.ucb.whosin.features.event.domain.model.EventResult
import com.ucb.whosin.features.event.domain.repository.IEventRepository

class GetEventByIdUseCase(private val repository: IEventRepository) {
    suspend operator fun invoke(eventId: String): EventResult {
        if (eventId.isBlank()) {
            return EventResult.Error("El ID del evento no puede estar vacío")
        }
        return repository.getEventById(eventId)
    }
}