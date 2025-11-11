package com.ucb.whosin.features.Guest.domain.repository

import com.ucb.whosin.features.Guest.domain.model.Guest
import com.ucb.whosin.features.Guest.domain.model.GuestResult

interface IGuestRepository {
    suspend fun addGuest(eventId: String, guest: Guest): GuestResult
    suspend fun getGuestsByEvent(eventId: String): GuestResult
    suspend fun updateGuest(eventId: String, guestId: String, guest: Guest): GuestResult
    suspend fun deleteGuest(eventId: String, guestId: String): GuestResult
}