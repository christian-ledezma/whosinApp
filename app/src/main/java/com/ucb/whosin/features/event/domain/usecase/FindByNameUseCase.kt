package com.ucb.whosin.features.event.domain.usecase

import com.google.firebase.Timestamp
import com.ucb.whosin.features.event.domain.model.EventModel
import com.ucb.whosin.features.event.domain.repository.IEventRepository

class FindByNameUseCase(
    val repository: IEventRepository
) {
    fun invoke(searchEvent : String) : Result<EventModel> {
        return repository.findByName(searchEvent)
    }
}