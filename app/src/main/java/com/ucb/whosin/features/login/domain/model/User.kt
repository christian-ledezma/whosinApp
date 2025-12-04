package com.ucb.whosin.features.login.domain.model

import com.google.firebase.Timestamp
import com.ucb.whosin.features.login.domain.vo.CountryCodeValue
import com.ucb.whosin.features.login.domain.vo.Email
import com.ucb.whosin.features.login.domain.vo.OptionalPersonName
import com.ucb.whosin.features.login.domain.vo.PersonName
import com.ucb.whosin.features.login.domain.vo.PhoneNumber
import com.ucb.whosin.features.login.domain.vo.UserId

data class User private constructor(
    val uid: UserId,
    val email: Email,
    val name: PersonName,
    val lastname: PersonName,
    val secondLastname: OptionalPersonName,
    val phoneNumber: PhoneNumber,
    val countryCode: CountryCodeValue,
    val createdAt: Timestamp?
) {
    fun fullName(): String {
        return buildString {
            append(name.value)
            append(" ")
            append(lastname.value)
            secondLastname.value?.let {
                if (it.isNotBlank()) {
                    append(" ")
                    append(it)
                }
            }
        }.trim()
    }

    fun fullPhone(): String {
        return "${countryCode.value} ${phoneNumber.value}"
    }

    fun toFirestoreMap(): Map<String, Any?> {
        return mapOf(
            "email" to email.value,
            "name" to name.value,
            "lastname" to lastname.value,
            "secondLastname" to secondLastname.value,
            "phone" to phoneNumber.value,
            "countryCode" to countryCode.value,
            "createdAt" to (createdAt ?: Timestamp.now())
        )
    }

    companion object {
        fun fromFirestoreMap(uid: String, data: Map<String, Any?>): Result<User> {
            return try {
                val userIdVO = UserId.create(uid).getOrElse { return Result.failure(it) }
                val emailVO = Email.create(data["email"] as? String ?: "").getOrElse { return Result.failure(it) }
                val nameVO = PersonName.create(data["name"] as? String ?: "", "nombre").getOrElse { return Result.failure(it) }
                val lastnameVO = PersonName.create(data["lastname"] as? String ?: "", "apellido").getOrElse { return Result.failure(it) }
                val secondLastnameVO = OptionalPersonName.create(data["secondLastname"] as? String).getOrElse { return Result.failure(it) }

                val countryCodeStr = data["countryCode"] as? String ?: ""
                val countryCodeVO = CountryCodeValue.create(countryCodeStr).getOrElse { return Result.failure(it) }

                val phoneStr = data["phone"] as? String ?: ""
                val phoneVO = PhoneNumber.create(phoneStr, countryCodeVO.value).getOrElse { return Result.failure(it) }

                Result.success(
                    User(
                        uid = userIdVO,
                        email = emailVO,
                        name = nameVO,
                        lastname = lastnameVO,
                        secondLastname = secondLastnameVO,
                        phoneNumber = phoneVO,
                        countryCode = countryCodeVO,
                        createdAt = data["createdAt"] as? Timestamp
                    )
                )
            } catch (e: Exception) {
                Result.failure(IllegalArgumentException("Error al crear usuario desde Firestore: ${e.message}"))
            }
        }

        // Builder para casos donde ya tienes Value Objects validados
        fun create(
            uid: UserId,
            email: Email,
            name: PersonName,
            lastname: PersonName,
            secondLastname: OptionalPersonName,
            phoneNumber: PhoneNumber,
            countryCode: CountryCodeValue,
            createdAt: Timestamp? = null
        ): User {
            return User(uid, email, name, lastname, secondLastname, phoneNumber, countryCode, createdAt)
        }
    }
}

sealed class AuthResult {
    data class Success(val user: User) : AuthResult()
    data class Error(val message: String) : AuthResult()
}