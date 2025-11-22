package com.ucb.whosin.features.login.domain.model

import com.google.firebase.Timestamp

data class User (
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    val lastname: String = "",
    val secondLastname: String? = null,
    val phone: String = "",
    val countryCode: String = "",
    val createdAt: Timestamp? = null
) {
    fun fullName(): String {
        return buildString {
            append(name)
            append(" ")
            append(lastname)
            secondLastname?.let {
                if (it.isNotBlank()) {
                    append(" ")
                    append(it)
                }
            }
        }.trim()
    }

    fun fullPhone(): String {
        return if (countryCode.isNotBlank() && phone.isNotBlank()) {
            "$countryCode $phone"
        } else {
            phone
        }
    }

    fun toFirestoreMap(): Map<String, Any?> {
        return mapOf(
            "email" to email,
            "name" to name,
            "lastname" to lastname,
            "secondLastname" to secondLastname,
            "phone" to phone,
            "countryCode" to countryCode,
            "createdAt" to (createdAt ?: Timestamp.now())
        )
    }

    companion object {
        fun fromFirestoreMap(uid: String, data: Map<String, Any?>): User {
            return User(
                uid = uid,
                email = data["email"] as? String ?: "",
                name = data["name"] as? String ?: "",
                lastname = data["lastname"] as? String ?: "",
                secondLastname = data["secondLastname"] as? String,
                phone = data["phone"] as? String ?: "",
                countryCode = data["countryCode"] as? String ?: "",
                createdAt = data["createdAt"] as? Timestamp
            )
        }
    }
}


sealed class AuthResult {
    data class Success(val user: User) : AuthResult()
    data class Error(val message: String) : AuthResult()
}