package com.uch.whosin.features.Guest.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.ucb.whosin.features.Guard.data.model.Guest

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class GuestRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    // Obtener lista de invitados de un evento en tiempo real
    fun getGuestsForEvent(eventId: String): Flow<List<Guest>> = callbackFlow {
        val listener = firestore.collection("events")
            .document(eventId)
            .collection("guests")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val guests = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        Guest(
                            userId = doc.getString("userId"),
                            name = doc.getString("name") ?: "",
                            plusOnesAllowed = doc.getLong("plusOnesAllowed")?.toInt() ?: 0,
                            groupSize = doc.getLong("groupSize")?.toInt() ?: 0,
                            checkedIn = doc.getBoolean("checkedIn") ?: false,
                            checkedInAt = doc.getLong("checkedInAt"),
                            qrCode = doc.getString("qrCode") ?: "",
                            inviteStatus = doc.getString("inviteStatus") ?: "",
                            note = doc.getString("note")
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                trySend(guests)
            }

        awaitClose { listener.remove() }
    }

    // Agregar un nuevo invitado
    suspend fun addGuest(eventId: String, guest: Guest): Result<String> {
        return try {
            val guestRef = firestore.collection("events")
                .document(eventId)
                .collection("guests")
                .document()

            val guestData = hashMapOf(
                "userId" to guest.userId,
                "name" to guest.name,
                "plusOnesAllowed" to guest.plusOnesAllowed,
                "groupSize" to guest.groupSize,
                "checkedIn" to guest.checkedIn,
                "checkedInAt" to guest.checkedInAt,
                "qrCode" to guest.qrCode.ifEmpty { guestRef.id },
                "inviteStatus" to guest.inviteStatus,
                "note" to guest.note,
                "checkedInBy" to null
            )

            guestRef.set(guestData).await()

            // Actualizar totalInvited en el evento
            updateEventTotalInvited(eventId, increment = true)

            Result.success(guestRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Actualizar el contador de invitados en el evento
    private suspend fun updateEventTotalInvited(eventId: String, increment: Boolean) {
        try {
            val eventRef = firestore.collection("events").document(eventId)
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(eventRef)
                val currentTotal = snapshot.getLong("totalInvited") ?: 0
                val newTotal = if (increment) currentTotal + 1 else maxOf(0, currentTotal - 1)
                transaction.update(eventRef, "totalInvited", newTotal)
            }.await()
        } catch (e: Exception) {
            // Log error but don't fail the operation
            e.printStackTrace()
        }
    }

    // Actualizar un invitado existente
    suspend fun updateGuest(eventId: String, guestId: String, guest: Guest): Result<Unit> {
        return try {
            val guestData = hashMapOf(
                "userId" to guest.userId,
                "name" to guest.name,
                "plusOnesAllowed" to guest.plusOnesAllowed,
                "groupSize" to guest.groupSize,
                "checkedIn" to guest.checkedIn,
                "checkedInAt" to guest.checkedInAt,
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

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Eliminar un invitado
    suspend fun deleteGuest(eventId: String, guestId: String): Result<Unit> {
        return try {
            firestore.collection("events")
                .document(eventId)
                .collection("guests")
                .document(guestId)
                .delete()
                .await()

            // Actualizar totalInvited en el evento
            updateEventTotalInvited(eventId, increment = false)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Buscar invitados por nombre
    suspend fun searchGuestsByName(eventId: String, query: String): Result<List<Guest>> {
        return try {
            val snapshot = firestore.collection("events")
                .document(eventId)
                .collection("guests")
                .get()
                .await()

            val guests = snapshot.documents.mapNotNull { doc ->
                try {
                    Guest(
                        userId = doc.getString("userId"),
                        name = doc.getString("name") ?: "",
                        plusOnesAllowed = doc.getLong("plusOnesAllowed")?.toInt() ?: 0,
                        groupSize = doc.getLong("groupSize")?.toInt() ?: 0,
                        checkedIn = doc.getBoolean("checkedIn") ?: false,
                        checkedInAt = doc.getLong("checkedInAt"),
                        qrCode = doc.getString("qrCode") ?: "",
                        inviteStatus = doc.getString("inviteStatus") ?: "",
                        note = doc.getString("note")
                    )
                } catch (e: Exception) {
                    null
                }
            }.filter { it.name.contains(query, ignoreCase = true) }

            Result.success(guests)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}