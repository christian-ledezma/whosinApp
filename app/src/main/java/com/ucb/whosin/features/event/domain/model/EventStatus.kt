package com.ucb.whosin.features.event.domain.model

import com.google.firebase.Timestamp
import java.util.Calendar
import java.util.Date
enum class EventStatus(val displayName: String) {
    UPCOMING("Próximo"),
    ACTIVE("En curso"),
    ENDED("Finalizado"),
    CANCELLED("Cancelado");

    companion object {
        fun fromString(status: String?): EventStatus {
            return when (status?.lowercase()) {
                "upcoming" -> UPCOMING
                "active" -> ACTIVE
                "ended" -> ENDED
                "cancelled" -> CANCELLED
                else -> UPCOMING
            }
        }

        fun calculateStatus(eventDate: Timestamp, currentStatus: String): EventStatus {
            // Si está cancelado, se mantiene cancelado
            if (currentStatus.lowercase() == "cancelled") {
                return CANCELLED
            }

            val eventCalendar = Calendar.getInstance().apply {
                time = eventDate.toDate()
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

            val eventDateOnly = eventCalendar.timeInMillis
            val todayDateOnly = todayCalendar.timeInMillis

            return when {
                eventDateOnly > todayDateOnly -> UPCOMING
                eventDateOnly == todayDateOnly -> ACTIVE
                else -> ENDED
            }
        }
    }
}