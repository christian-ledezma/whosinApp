package com.ucb.whosin.features.Guard.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.ucb.whosin.features.Guard.data.model.Guest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

interface GuardRepository {
    fun getGuests(eventId: String): Flow<List<Guest>>
    suspend fun checkInGuest(eventId: String, guestId: String, guardId: String)
}

class GuardRepositoryFirebase(private val firestore: FirebaseFirestore) : GuardRepository {

    override fun getGuests(eventId: String): Flow<List<Guest>> = callbackFlow {
        val listener = firestore.collection("events").document(eventId)
            .collection("guests")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }
                if (snapshot != null) {

                    val guests = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Guest::class.java)?.copy(guestId = doc.id)
                    }
                    trySend(guests).isSuccess
                }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun checkInGuest(eventId: String, guestId: String, guardId: String) {
        val eventRef = firestore.collection("events").document(eventId)
        val guestRef = eventRef.collection("guests").document(guestId)

        firestore.runBatch {
            batch ->
            // 1. Actualiza el estado del invitado
            batch.update(guestRef, mapOf(
                "checkedIn" to true,
                "checkedInAt" to FieldValue.serverTimestamp(),
                "checkedInBy" to guardId
            ))

            // 2. Incrementa el contador de check-ins en el evento
            batch.update(eventRef, "totalCheckedIn", FieldValue.increment(1))
        }.await()
    }
}
