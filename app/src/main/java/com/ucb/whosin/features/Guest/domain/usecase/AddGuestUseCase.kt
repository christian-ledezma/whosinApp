package com.ucb.whosin.features.Guest.domain.usecase



import com.ucb.whosin.features.Guest.domain.model.Guest
import com.ucb.whosin.features.Guest.domain.model.GuestResult
import com.ucb.whosin.features.Guest.domain.repository.IGuestRepository
import com.ucb.whosin.features.event.domain.model.EventResult
import com.ucb.whosin.features.event.domain.repository.IEventRepository

class AddGuestUseCase(
    private val repository: IGuestRepository,
    private val eventRepository: IEventRepository
) {
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

        // 1) Obtener evento
        val event = eventRepository.getEventById(eventId)
        if (event is EventResult.Error) {
            return GuestResult.Error("No se pudo obtener el evento")
        }
        val eventData = (event as EventResult.Success).event

        // 2) Obtener número actual de invitados
        val guestsResult = repository.getGuestsByEvent(eventId)
        if (guestsResult is GuestResult.Error) {
            return GuestResult.Error("No se pudieron obtener los invitados")
        }
        val currentGuests = (guestsResult as GuestResult.SuccessList).guests.size

        // 3) Validar capacidad
        if (currentGuests >= eventData.capacity) {
            return GuestResult.Error("El evento ya alcanzó su capacidad máxima")
        }

        // 4) Registrar invitado
        return repository.addGuest(eventId, guest)
    }
}
