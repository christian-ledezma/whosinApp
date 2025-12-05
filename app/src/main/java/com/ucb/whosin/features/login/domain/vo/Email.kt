package com.ucb.whosin.features.login.domain.vo

import android.util.Patterns

@JvmInline
value class Email private constructor(val value: String){
    companion object {
        private val EMAIL_PATTERN = Regex(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

        fun create(email: String): Result<Email> {
            val normalized = normalize(email)

            return when {
                normalized.isBlank() -> Result.failure(
                    IllegalArgumentException("El correo electrónico no puede estar vacío")
                )
                !isValid(normalized) -> Result.failure(
                    IllegalArgumentException("El formato del correo electrónico no es válido")
                )
                else -> Result.success(Email(normalized))
            }
        }

        private fun normalize(email: String): String {
            return email.trim().lowercase()
        }

        private fun isValid(email: String): Boolean {
            return EMAIL_PATTERN.matches(email)
        }
    }

    override fun toString(): String = value
}