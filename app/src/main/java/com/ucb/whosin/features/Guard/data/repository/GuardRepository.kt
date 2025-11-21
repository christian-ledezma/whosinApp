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
                    val guests = snapshot.toObjects(Guest::class.java)
                    trySend(guests).isSuccess
                }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun checkInGuest(eventId: String, guestId: String, guardId: String) {
        firestore.collection("events").document(eventId)
            .collection("guests").document(guestId)
            .update(
                mapOf(
                    "checkedIn" to true,
                    "checkedInAt" to FieldValue.serverTimestamp(),
                    "checkedInBy" to guardId
                )
            ).await()
    }
}


// Using a local implementation with hardcoded data for testing
/*
class GuardRepositoryImpl : GuardRepository {

    private val guests = MutableStateFlow<List<Guest>>(listOf(
        Guest(userId = "1", name = "John Doe", checkedIn = false),
        Guest(userId = "2", name = "Jane Smith", checkedIn = true, checkedInAt = System.currentTimeMillis()),
        Guest(userId = "3", name = "Peter Jones", checkedIn = false)
    ))

    override fun getGuests(eventId: String): Flow<List<Guest>> {
        return guests.asStateFlow()
    }

    override suspend fun checkInGuest(eventId: String, guestId: String, guardId: String) {
        val currentGuests = guests.value.toMutableList()
        val guestIndex = currentGuests.indexOfFirst { it.userId == guestId }
        if (guestIndex != -1) {
            val updatedGuest = currentGuests[guestIndex].copy(
                checkedIn = true,
                checkedInAt = System.currentTimeMillis(),
                checkedInBy = guardId
            )
            currentGuests[guestIndex] = updatedGuest
            guests.value = currentGuests
        }
    }
}
*/