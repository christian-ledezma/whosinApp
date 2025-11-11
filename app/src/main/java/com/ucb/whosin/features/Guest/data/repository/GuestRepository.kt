package com.ucb.whosin.features.Guest.data.repository

import com.ucb.whosin.features.Guest.data.datasource.FirebaseGuestDataSource
import com.ucb.whosin.features.Guest.domain.model.Guest
import com.ucb.whosin.features.Guest.domain.model.GuestResult
import com.ucb.whosin.features.Guest.domain.repository.IGuestRepository

class GuestRepository(
    private val dataSource: FirebaseGuestDataSource
) : IGuestRepository {

    override suspend fun addGuest(eventId: String, guest: Guest): GuestResult {
        return dataSource.addGuest(eventId, guest)
    }

    override suspend fun getGuestsByEvent(eventId: String): GuestResult {
        return dataSource.getGuestsByEvent(eventId)
    }

    override suspend fun updateGuest(eventId: String, guestId: String, guest: Guest): GuestResult {
        return dataSource.updateGuest(eventId, guestId, guest)
    }

    override suspend fun deleteGuest(eventId: String, guestId: String): GuestResult {
        return dataSource.deleteGuest(eventId, guestId)
    }
}