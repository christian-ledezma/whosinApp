package com.ucb.whosin.features.Guard.data.model

import java.util.Date

data class Guest(
    val guestId: String = "",
    val userId: String? = null,
    val name: String = "",
    val plusOnesAllowed: Int = 0,
    val groupSize: Int = 0,
    val checkedIn: Boolean = false,
    val checkedInAt: Date? = null,
    val checkedInBy: String? = null,
    val qrCode: String = "",
    val inviteStatus: String = "",
    val note: String? = null
)