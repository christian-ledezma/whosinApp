package com.ucb.whosin.features.event.domain.model

import com.google.firebase.Timestamp

data class EventModel(
    val name: String,
    val date: Timestamp,
    val status: String,
    val locationName: String
)
