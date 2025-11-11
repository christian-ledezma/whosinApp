package com.ucb.whosin.features.Guest.domain.model

import com.google.firebase.Timestamp

data class Guest(
    val guestId: String = "",
    val userId: String? = null,           // si no tiene cuenta
    val name: String = "",
    val plusOnesAllowed: Int = 0,
    val groupSize: Int = 0,
    val checkedIn: Boolean = false,
    val checkedInAt: Timestamp? = null,
    val checkedInBy: String? = null,      // userId del guardia
    val qrCode: String = "",              // token único
    val inviteStatus: String = "pending", // "pending" | "confirmed" | "declined"
    val note: String? = null
)

sealed class GuestResult {
    data class Success(val guest: Guest) : GuestResult()
    data class SuccessList(val guests: List<Guest>) : GuestResult()
    data class Error(val message: String) : GuestResult()
}