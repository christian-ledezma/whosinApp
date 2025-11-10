package com.ucb.whosin.features.event.domain.usecase

import com.ucb.whosin.features.event.domain.model.EventResult
import com.ucb.whosin.features.event.domain.repository.IEventRepository

class FindByNameUseCase(private val repository: IEventRepository) {
    suspend operator fun invoke(searchEvent : String) : EventResult {

        if (searchEvent.isBlank()) {
            return EventResult.Error("El nombre de evento no puede estar vacío")
        }

        return repository.findByName(searchEvent)
    }
}