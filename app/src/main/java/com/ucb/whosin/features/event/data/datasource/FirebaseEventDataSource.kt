package com.ucb.whosin.features.event.data.datasource

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.ucb.whosin.features.event.domain.model.EventModel
import com.ucb.whosin.features.event.domain.model.EventResult
import kotlinx.coroutines.tasks.await

class FirebaseEventDataSource(
    private val firestore: FirebaseFirestore
) {

    suspend fun register(event: EventModel): EventResult {
        return try {
            Log.d("FirebaseEvent", "🔹 Iniciando registro de evento: ${event.name}")

            // Referencia a la colección "events"
            val eventsRef = firestore.collection("events")

            // Si el evento tiene un ID definido, lo usamos; si no, dejamos que Firestore genere uno
            val eventId = if (event.eventId.isNotBlank()) event.eventId else eventsRef.document().id

            // Datos a guardar
            val eventData = hashMapOf(
                "eventId" to eventId,
                "userId" to event.userId,
                "name" to event.name,
                "date" to event.date,
                "locationName" to event.locationName,
                "capacity" to event.capacity,
                "status" to event.status,
                "guardModeEnabled" to event.guardModeEnabled,
                "createdAt" to event.createdAt,
                "totalCheckedIn" to event.totalCheckedIn,
                "totalInvited" to event.totalInvited
            )

            // Guardar en Firestore
            eventsRef.document(eventId).set(eventData).await()
            Log.d("FirebaseEvent", "✅ Evento guardado correctamente en Firestore (ID: $eventId)")

            EventResult.Success(event.copy(eventId = eventId))

        } catch (e: Exception) {
            Log.e("FirebaseEvent", "❌ Error al registrar evento", e)
            EventResult.Error(e.message ?: "Error desconocido al registrar evento")
        }
    }
}

//private val firebaseAuth: FirebaseAuth,