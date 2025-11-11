package com.ucb.whosin.features.Guest.domain.usecase

import com.ucb.whosin.features.Guest.domain.model.GuestResult
import com.ucb.whosin.features.Guest.domain.repository.IGuestRepository

class GetGuestsUseCase(private val repository: IGuestRepository) {
    suspend operator fun invoke(eventId: String): GuestResult {
        if (eventId.isBlank()) {
            return GuestResult.Error("El ID del evento no puede estar vacío")
        }
        return repository.getGuestsByEvent(eventId)
    }
}