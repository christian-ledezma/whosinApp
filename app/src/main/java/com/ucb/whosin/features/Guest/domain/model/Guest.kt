package com.ucb.whosin.features.Guest.domain.model

data class Guest(

    val userId: String? = null,
    val name: String = "",
    val plusOnesAllowed: Int = 0,
    val groupSize: Int = 0,
    val checkedIn: Boolean = false,
    val checkedInAt: Long? = null,
    val qrCode: String = "",
    val inviteStatus: String = "",
    val note: String? = null
)