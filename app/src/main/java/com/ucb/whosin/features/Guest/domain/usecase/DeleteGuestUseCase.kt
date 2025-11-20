package com.ucb.whosin.features.Guest.domain.usecase

import com.ucb.whosin.features.Guest.domain.model.GuestResult
import com.ucb.whosin.features.Guest.domain.repository.IGuestRepository

class DeleteGuestUseCase(private val repository: IGuestRepository) {
    suspend operator fun invoke(eventId: String, guestId: String): GuestResult {
        if (eventId.isBlank()) {
            return GuestResult.Error("El ID del evento no puede estar vacío")
        }
        if (guestId.isBlank()) {
            return GuestResult.Error("El ID del invitado no puede estar vacío")
        }
        return repository.deleteGuest(eventId, guestId)
    }
}