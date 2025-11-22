package com.ucb.whosin.features.event.domain.usecase

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class DeleteEventUseCase(
    private val firestore: FirebaseFirestore
) {
    suspend operator fun invoke(eventId: String): Boolean {
        return try {
            val eventRef = firestore.collection("events").document(eventId)

            // 1. Eliminar subcolección "guests"
            val guestsSnapshot = eventRef.collection("guests").get().await()
            for (guestDoc in guestsSnapshot.documents) {
                guestDoc.reference.delete().await()
            }

            // 2. Eliminar el documento principal del evento
            eventRef.delete().await()

            true
        } catch (e: Exception) {
            false
        }
    }
}
