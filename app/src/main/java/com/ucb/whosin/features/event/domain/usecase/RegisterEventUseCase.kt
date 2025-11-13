package com.ucb.whosin.features.event.domain.usecase

import com.google.firebase.Timestamp
import com.ucb.whosin.features.event.domain.model.EventResult
import com.ucb.whosin.features.event.domain.repository.IEventRepository

class RegisterEventUseCase (private val repository: IEventRepository) {
    suspend operator fun invoke(
                                        eventId: String,
                                        name: String,
                                        date: Timestamp,
                                        locationName: String,
                                        capacity: Int,
                                        status: String,
                                        guardModeEnabled: Boolean,
                                        createdAt: Timestamp,
                                        totalCheckedIn: Int,
                                        totalInvited: Int

                                ) : EventResult {

        if (
                eventId.isBlank() ||
                name.isBlank() ||
                locationName.isBlank() ||
                capacity <= 0 ||
                status.isBlank()
            ) {

            return EventResult.Error("Ninguno de los campos eventId,userId,name,locationName,capacity o estatus al registrar un evento puede estar vacío")
        }

        return repository.registerEvent(
            eventId,
            name,
            date,
            locationName,
            capacity,
            status,
            guardModeEnabled,
            createdAt,
            totalCheckedIn,
            totalInvited
        )
    }
}