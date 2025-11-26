package com.ucb.whosin.features.event.data.datasource

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ucb.whosin.features.event.domain.model.EventModel
import com.ucb.whosin.features.event.domain.model.EventResult
import kotlinx.coroutines.tasks.await

class FirebaseEventDataSource(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) {
    suspend fun getEventById(eventId: String): EventResult {
        return try {
            val doc = firestore.collection("events").document(eventId).get().await()
            if (doc.exists()) {
                val event = doc.toObject(EventModel::class.java)?.copy(eventId = doc.id)
                if (event != null) EventResult.Success(event)
                else EventResult.Error("Error al mapear el evento")
            } else {
                EventResult.Error("Evento no encontrado")
            }
        } catch (e: Exception) {
            EventResult.Error(e.message ?: "Error desconocido")
        }
    }

    suspend fun findEventsByName(name: String): List<EventModel> {
        return try {
            val snapshot = firestore.collection("events")
                .whereGreaterThanOrEqualTo("name", name)
                .whereLessThanOrEqualTo("name", name + "\uf8ff")
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.toObject(EventModel::class.java)?.copy(eventId = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }


    suspend fun register(event: EventModel): EventResult {
        return try {

            val currentUser = firebaseAuth.currentUser
            if (currentUser == null) {
                Log.e("FirebaseEvent", "❌ No hay usuario logueado.")
                return EventResult.Error("No hay usuario autenticado")
            }

            val userId = currentUser.uid

            Log.d("FirebaseEvent", "🔹 Iniciando registro de evento: ${event.name}")

            // Referencia a la colección "events"
            val eventsRef = firestore.collection("events")

            // Si el evento tiene un ID definido, lo usamos; si no, dejamos que Firestore genere uno
            val eventId = if (event.eventId.isNotBlank()) event.eventId else eventsRef.document().id

            // Datos a guardar
            val eventData = hashMapOf(
                "eventId" to eventId,
                "userId" to userId,
                "name" to event.name,
                "date" to event.date,
                "locationName" to event.locationName,
                "latitude" to event.latitude,
                "longitude" to event.longitude,
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

            EventResult.Success(event.copy(eventId = eventId, userId = userId))

        } catch (e: Exception) {
            Log.e("FirebaseEvent", "❌ Error al registrar evento", e)
            EventResult.Error(e.message ?: "Error desconocido al registrar evento")
        }
    }

    suspend fun getAllEventsByUser(userId: String): List<EventModel> {
        return try {
            val snapshot = firestore.collection("events")
                .whereEqualTo("userId", userId)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.toObject(EventModel::class.java)?.copy(eventId = doc.id)
            }

        } catch (e: Exception) {
            emptyList()
        }
    }

}