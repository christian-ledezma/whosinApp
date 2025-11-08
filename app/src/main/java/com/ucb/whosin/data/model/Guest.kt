package com.ucb.whosin.data.model

// Using Long for timestamp to be compatible with Room
data class Guest(
    val userId: String? = null,
    val name: String = "",
    val plusOnesAllowed: Int = 0,
    val groupSize: Int = 0,
    val checkedIn: Boolean = false,
    val checkedInAt: Long? = null, // Changed from Timestamp
    val checkedInBy: String? = null,
    val qrCode: String = "",
    val inviteStatus: String = "",
    val note: String? = null
)