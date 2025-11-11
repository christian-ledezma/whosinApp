package com.ucb.whosin.features.Guest.domain.usecase



import com.ucb.whosin.features.Guest.domain.model.Guest
import com.ucb.whosin.features.Guest.domain.model.GuestResult
import com.ucb.whosin.features.Guest.domain.repository.IGuestRepository

class AddGuestUseCase(private val repository: IGuestRepository) {
    suspend operator fun invoke(eventId: String, guest: Guest): GuestResult {
        if (eventId.isBlank()) {
            return GuestResult.Error("El ID del evento no puede estar vacío")
        }
        if (guest.name.isBlank()) {
            return GuestResult.Error("El nombre del invitado no puede estar vacío")
        }
        if (guest.plusOnesAllowed < 0) {
            return GuestResult.Error("Los acompañantes no pueden ser negativos")
        }
        return repository.addGuest(eventId, guest)
    }
}