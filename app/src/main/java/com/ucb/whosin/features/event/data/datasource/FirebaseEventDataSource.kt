package com.ucb.whosin.features.event.data.datasource

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ucb.whosin.features.event.domain.model.AssignedGuard
import com.ucb.whosin.features.event.domain.model.EventModel
import com.ucb.whosin.features.event.domain.model.EventResult
import com.ucb.whosin.features.event.domain.model.GuardResult
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

    suspend fun updateEvent(
        eventId: String,
        name: String,
        date: Timestamp,
        locationName: String,
        latitude: Double,
        longitude: Double,
        capacity: Int
    ): EventResult {
        return try {
            Log.d("FirebaseEvent", "🔹 Actualizando evento: $eventId")

            val updateData = hashMapOf<String, Any>(
                "name" to name,
                "date" to date,
                "locationName" to locationName,
                "latitude" to latitude,
                "longitude" to longitude,
                "capacity" to capacity
            )

            firestore.collection("events")
                .document(eventId)
                .update(updateData)
                .await()

            Log.d("FirebaseEvent", "✅ Evento actualizado correctamente")

            // Obtener el evento actualizado
            getEventById(eventId)
        } catch (e: Exception) {
            Log.e("FirebaseEvent", "❌ Error al actualizar evento", e)
            EventResult.Error(e.message ?: "Error al actualizar el evento")
        }
    }

    suspend fun cancelEvent(eventId: String): EventResult {
        return try {
            Log.d("FirebaseEvent", "🔹 Cancelando evento: $eventId")

            firestore.collection("events")
                .document(eventId)
                .update("status", "cancelled")
                .await()

            Log.d("FirebaseEvent", "✅ Evento cancelado correctamente")
            getEventById(eventId)
        } catch (e: Exception) {
            Log.e("FirebaseEvent", "❌ Error al cancelar evento", e)
            EventResult.Error(e.message ?: "Error al cancelar el evento")
        }
    }

    suspend fun addGuard(
        eventId: String,
        guardEmail: String,
        addedBy: String
    ): GuardResult {
        return try {
            Log.d("FirebaseEvent", "🔹 Agregando guardia: $guardEmail al evento: $eventId")

            // 1. Buscar usuario por email
            val userSnapshot = firestore.collection("users")
                .whereEqualTo("email", guardEmail)
                .limit(1)
                .get()
                .await()

            if (userSnapshot.isEmpty) {
                return GuardResult.Error("No se encontró un usuario con ese email")
            }

            val userDoc = userSnapshot.documents.first()
            val guardId = userDoc.id
            val fullName = "${userDoc.getString("name") ?: ""} ${userDoc.getString("lastname") ?: ""}".trim()

            // 2. Verificar si ya es guardia
            val existingGuard = firestore.collection("events")
                .document(eventId)
                .collection("guards")
                .document(guardId)
                .get()
                .await()

            if (existingGuard.exists()) {
                return GuardResult.Error("Este usuario ya es guardia del evento")
            }

            // 3. Crear el documento del guardia
            val guard = AssignedGuard(
                guardId = guardId,
                email = guardEmail,
                fullName = fullName,
                addedAt = Timestamp.now(),
                addedBy = addedBy
            )

            val guardData = hashMapOf(
                "guardId" to guard.guardId,
                "email" to guard.email,
                "fullName" to guard.fullName,
                "addedAt" to guard.addedAt,
                "addedBy" to guard.addedBy
            )

            firestore.collection("events")
                .document(eventId)
                .collection("guards")
                .document(guardId)
                .set(guardData)
                .await()

            Log.d("FirebaseEvent", "✅ Guardia agregado correctamente")
            GuardResult.Success(guard)
        } catch (e: Exception) {
            Log.e("FirebaseEvent", "❌ Error al agregar guardia", e)
            GuardResult.Error(e.message ?: "Error al agregar guardia")
        }
    }

    suspend fun removeGuard(eventId: String, guardId: String): GuardResult {
        return try {
            Log.d("FirebaseEvent", "🔹 Removiendo guardia: $guardId del evento: $eventId")

            firestore.collection("events")
                .document(eventId)
                .collection("guards")
                .document(guardId)
                .delete()
                .await()

            Log.d("FirebaseEvent", "✅ Guardia removido correctamente")
            GuardResult.Success(AssignedGuard(guardId = guardId))
        } catch (e: Exception) {
            Log.e("FirebaseEvent", "❌ Error al remover guardia", e)
            GuardResult.Error(e.message ?: "Error al remover guardia")
        }
    }

    suspend fun getEventGuards(eventId: String): GuardResult {
        return try {
            Log.d("FirebaseEvent", "🔹 Obteniendo guardias del evento: $eventId")

            val snapshot = firestore.collection("events")
                .document(eventId)
                .collection("guards")
                .get()
                .await()

            val guards = snapshot.documents.mapNotNull { doc ->
                AssignedGuard(
                    guardId = doc.id,
                    email = doc.getString("email") ?: "",
                    fullName = doc.getString("fullName") ?: "",
                    addedAt = doc.getTimestamp("addedAt") ?: Timestamp.now(),
                    addedBy = doc.getString("addedBy") ?: ""
                )
            }

            Log.d("FirebaseEvent", "✅ ${guards.size} guardias obtenidos")
            GuardResult.SuccessList(guards)
        } catch (e: Exception) {
            Log.e("FirebaseEvent", "❌ Error al obtener guardias", e)
            GuardResult.Error(e.message ?: "Error al obtener guardias")
        }
    }

}