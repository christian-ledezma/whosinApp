package com.ucb.whosin.features.event.domain.usecase

import com.google.firebase.Timestamp
import com.ucb.whosin.features.event.domain.model.EventModel
import com.ucb.whosin.features.event.domain.repository.IEventRepository
import java.util.Calendar

class GetEventsWhereUserIsGuardUseCase(
    private val repository: IEventRepository
) {
    suspend operator fun invoke(guardId: String): List<EventModel> {
        return repository.getEventsWhereUserIsGuard(guardId)
    }
}