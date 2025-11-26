package com.ucb.whosin.features.event.domain.usecase

import com.ucb.whosin.features.event.domain.model.EventModel
import com.ucb.whosin.features.event.domain.repository.IEventRepository

class FindEventsByNameUseCase(
    private val repository: IEventRepository
) {
    suspend operator fun invoke(name: String): List<EventModel> {
        if (name.isBlank()) return emptyList()
        return repository.findEventsByName(name)
    }
}