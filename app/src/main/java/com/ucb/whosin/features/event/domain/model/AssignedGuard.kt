package com.ucb.whosin.features.event.domain.model

import com.google.firebase.Timestamp

data class AssignedGuard(
    val guardId: String = "",
    val email: String = "",
    val fullName: String = "",
    val addedAt: Timestamp = Timestamp.now(),
    val addedBy: String = ""
)

sealed class GuardResult {
    data class Success(val assignedGuard: AssignedGuard) : GuardResult()
    data class SuccessList(val assignedGuard: List<AssignedGuard>) : GuardResult()
    data class Error(val message: String) : GuardResult()
}