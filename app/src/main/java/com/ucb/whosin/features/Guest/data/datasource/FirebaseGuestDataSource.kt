package com.ucb.whosin.features.Guest.data.datasource

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.ucb.whosin.features.Guest.domain.model.Guest
import com.ucb.whosin.features.Guest.domain.model.GuestResult
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirebaseGuestDataSource(
    private val firestore: FirebaseFirestore
) {
    suspend fun addGuest(eventId: String, guest: Guest): GuestResult {
        return try {
            Log.d("FirebaseGuest", "🔹 Iniciando registro de invitado: ${guest.name}")

            // Referencia a la subcolección guests
            val guestsRef = firestore.collection("events")
                .document(eventId)
                .collection("guests")

            // Generar ID único si no existe
            val guestId = if (guest.guestId.isNotBlank())
                guest.guestId
            else
                guestsRef.document().id

            // Generar QR Code único
            val qrCode = if (guest.qrCode.isNotBlank())
                guest.qrCode
            else
                UUID.randomUUID().toString()

            // Datos a guardar
            val guestData = hashMapOf(
                "guestId" to guestId,
                "userId" to guest.userId,
                "name" to guest.name,
                "plusOnesAllowed" to guest.plusOnesAllowed,
                "groupSize" to guest.groupSize,
                "checkedIn" to guest.checkedIn,
                "checkedInAt" to guest.checkedInAt,
                "checkedInBy" to guest.checkedInBy,
                "qrCode" to qrCode,
                "inviteStatus" to guest.inviteStatus,
                "note" to guest.note
            )

            // Guardar en Firestore
            guestsRef.document(guestId).set(guestData).await()

            // Actualizar totalInvited en el evento
            updateEventTotalInvited(eventId, increment = true)

            Log.d("FirebaseGuest", "✅ Invitado guardado correctamente (ID: $guestId)")
            GuestResult.Success(guest.copy(guestId = guestId, qrCode = qrCode))

        } catch (e: Exception) {
            Log.e("FirebaseGuest", "❌ Error al agregar invitado", e)
            GuestResult.Error(e.message ?: "Error desconocido al agregar invitado")
        }
    }

    suspend fun getGuestsByEvent(eventId: String): GuestResult {
        return try {
            Log.d("FirebaseGuest", "🔹 Obteniendo invitados del evento: $eventId")

            val snapshot = firestore.collection("events")
                .document(eventId)
                .collection("guests")
                .get()
                .await()

            val guests = snapshot.documents.mapNotNull { doc ->
                try {
                    Guest(
                        guestId = doc.id,
                        userId = doc.getString("userId"),
                        name = doc.getString("name") ?: "",
                        plusOnesAllowed = doc.getLong("plusOnesAllowed")?.toInt() ?: 0,
                        groupSize = doc.getLong("groupSize")?.toInt() ?: 0,
                        checkedIn = doc.getBoolean("checkedIn") ?: false,
                        checkedInAt = doc.getTimestamp("checkedInAt"),
                        checkedInBy = doc.getString("checkedInBy"),
                        qrCode = doc.getString("qrCode") ?: "",
                        inviteStatus = doc.getString("inviteStatus") ?: "pending",
                        note = doc.getString("note")
                    )
                } catch (e: Exception) {
                    Log.e("FirebaseGuest", "Error al parsear invitado", e)
                    null
                }
            }

            Log.d("FirebaseGuest", "✅ ${guests.size} invitados obtenidos")
            GuestResult.SuccessList(guests)

        } catch (e: Exception) {
            Log.e("FirebaseGuest", "❌ Error al obtener invitados", e)
            GuestResult.Error(e.message ?: "Error desconocido al obtener invitados")
        }
    }

    suspend fun updateGuest(eventId: String, guestId: String, guest: Guest): GuestResult {
        return try {
            Log.d("FirebaseGuest", "🔹 Actualizando invitado: $guestId")

            val guestData = hashMapOf(
                "userId" to guest.userId,
                "name" to guest.name,
                "plusOnesAllowed" to guest.plusOnesAllowed,
                "groupSize" to guest.groupSize,
                "checkedIn" to guest.checkedIn,
                "checkedInAt" to guest.checkedInAt,
                "checkedInBy" to guest.checkedInBy,
                "qrCode" to guest.qrCode,
                "inviteStatus" to guest.inviteStatus,
                "note" to guest.note
            )

            firestore.collection("events")
                .document(eventId)
                .collection("guests")
                .document(guestId)
                .update(guestData as Map<String, Any>)
                .await()

            Log.d("FirebaseGuest", "✅ Invitado actualizado correctamente")
            GuestResult.Success(guest.copy(guestId = guestId))

        } catch (e: Exception) {
            Log.e("FirebaseGuest", "❌ Error al actualizar invitado", e)
            GuestResult.Error(e.message ?: "Error desconocido al actualizar invitado")
        }
    }

    suspend fun deleteGuest(eventId: String, guestId: String): GuestResult {
        return try {
            Log.d("FirebaseGuest", "🔹 Eliminando invitado: $guestId")

            firestore.collection("events")
                .document(eventId)
                .collection("guests")
                .document(guestId)
                .delete()
                .await()

            // Actualizar totalInvited en el evento
            updateEventTotalInvited(eventId, increment = false)

            Log.d("FirebaseGuest", "✅ Invitado eliminado correctamente")
            GuestResult.Success(Guest(guestId = guestId))

        } catch (e: Exception) {
            Log.e("FirebaseGuest", "❌ Error al eliminar invitado", e)
            GuestResult.Error(e.message ?: "Error desconocido al eliminar invitado")
        }
    }

    private suspend fun updateEventTotalInvited(eventId: String, increment: Boolean) {
        try {
            val eventRef = firestore.collection("events").document(eventId)
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(eventRef)
                val currentTotal = snapshot.getLong("totalInvited") ?: 0
                val newTotal = if (increment) currentTotal + 1 else maxOf(0, currentTotal - 1)
                transaction.update(eventRef, "totalInvited", newTotal)
            }.await()
            Log.d("FirebaseGuest", "✅ totalInvited actualizado: ${if (increment) "+" else "-"}1")
        } catch (e: Exception) {
            Log.e("FirebaseGuest", "⚠️ Error al actualizar totalInvited", e)
        }
    }
}