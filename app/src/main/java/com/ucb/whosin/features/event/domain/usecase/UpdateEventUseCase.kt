package com.ucb.whosin.features.event.domain.usecase

import com.google.firebase.Timestamp
import com.ucb.whosin.features.event.domain.model.EventModel
import com.ucb.whosin.features.event.domain.model.EventResult
import com.ucb.whosin.features.event.domain.model.EventStatus
import com.ucb.whosin.features.event.domain.repository.IEventRepository
import java.util.Calendar

class UpdateEventUseCase(private val repository: IEventRepository) {
    suspend operator fun invoke(
        eventId: String,
        name: String,
        date: Timestamp,
        locationName: String,
        latitude: Double,
        longitude: Double,
        capacity: Int
    ): EventResult {
        // Validaciones básicas
        if (eventId.isBlank()) {
            return EventResult.Error("El ID del evento no puede estar vacío")
        }
        if (name.isBlank()) {
            return EventResult.Error("El nombre no puede estar vacío")
        }
        if (locationName.isBlank()) {
            return EventResult.Error("La ubicación no puede estar vacía")
        }
        if (capacity <= 0) {
            return EventResult.Error("La capacidad debe ser mayor a 0")
        }

        // Validar que la fecha no sea hoy o en el pasado
        val eventCalendar = Calendar.getInstance().apply {
            time = date.toDate()
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val todayCalendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (eventCalendar.timeInMillis <= todayCalendar.timeInMillis) {
            return EventResult.Error("No se puede editar eventos que ya iniciaron o finalizaron")
        }

        return repository.updateEvent(
            eventId = eventId,
            name = name,
            date = date,
            locationName = locationName,
            latitude = latitude,
            longitude = longitude,
            capacity = capacity
        )
    }
}