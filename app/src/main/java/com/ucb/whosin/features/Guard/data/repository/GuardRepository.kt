package com.ucb.whosin.features.Guard.data.repository

import com.ucb.whosin.features.Guard.data.model.Guest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

interface GuardRepository {
    fun getGuests(eventId: String): Flow<List<Guest>>
    suspend fun checkInGuest(eventId: String, guestId: String, guardId: String)
}

// Using a local implementation with hardcoded data for testing
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