package com.ucb.whosin.features.Guest.domain.usecase

import com.ucb.whosin.features.Guard.data.model.Guest
import com.ucb.whosin.features.Guest.domain.model.GuestResult
import com.ucb.whosin.features.Guest.domain.repository.IGuestRepository

class UpdateGuestUseCase(private val repository: IGuestRepository) {
    suspend operator fun invoke(eventId: String, guestId: String, guest: Guest): GuestResult {
        if (eventId.isBlank()) {
            return GuestResult.Error("El ID del evento no puede estar vacío")
        }
        if (guestId.isBlank()) {
            return GuestResult.Error("El ID del invitado no puede estar vacío")
        }
        return repository.updateGuest(eventId, guestId, guest)
    }
}