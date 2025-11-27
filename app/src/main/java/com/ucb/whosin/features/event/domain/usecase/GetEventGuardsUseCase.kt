package com.ucb.whosin.features.event.domain.usecase

import com.ucb.whosin.features.event.domain.model.GuardResult
import com.ucb.whosin.features.event.domain.repository.IEventRepository

class GetEventGuardsUseCase(private val repository: IEventRepository) {
    suspend operator fun invoke(eventId: String): GuardResult {
        if (eventId.isBlank()) {
            return GuardResult.Error("El ID del evento no puede estar vacío")
        }
        return repository.getEventGuards(eventId)
    }
}