package com.ucb.whosin.features.event.domain.usecase

import com.ucb.whosin.features.event.domain.model.GuardResult
import com.ucb.whosin.features.event.domain.repository.IEventRepository

class AddGuardUseCase(private val repository: IEventRepository) {
    suspend operator fun invoke(
        eventId: String,
        guardEmail: String,
        addedBy: String
    ): GuardResult {
        if (eventId.isBlank()) {
            return GuardResult.Error("El ID del evento no puede estar vacío")
        }
        if (guardEmail.isBlank()) {
            return GuardResult.Error("El email del guardia no puede estar vacío")
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(guardEmail).matches()) {
            return GuardResult.Error("El formato del email no es válido")
        }

        return repository.addGuard(eventId, guardEmail, addedBy)
    }
}