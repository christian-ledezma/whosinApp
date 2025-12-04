package com.ucb.whosin.features.login.domain.model.vo

@JvmInline
value class Email private constructor(val value: String){
    companion object {
        private val EMAIL_PATTERN = android.util.Patterns.EMAIL_ADDRESS

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
            return EMAIL_PATTERN.matcher(email).matches()
        }
    }

    override fun toString(): String = value
}