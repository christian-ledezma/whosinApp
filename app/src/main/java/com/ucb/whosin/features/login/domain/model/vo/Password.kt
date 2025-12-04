package com.ucb.whosin.features.login.domain.model.vo

@JvmInline
value class Password private constructor(val value: String) {
    companion object {
        private const val MIN_LENGTH = 6

        fun create(password: String): Result<Password> {
            return when {
                password.isBlank() -> Result.failure(
                    IllegalArgumentException("La contraseña no puede estar vacía")
                )
                password.length < MIN_LENGTH -> Result.failure(
                    IllegalArgumentException("La contraseña debe tener al menos $MIN_LENGTH caracteres")
                )
                else -> Result.success(Password(password))
            }
        }

        fun validate(current: Password, new: Password): Result<Unit> {
            return when {
                current.value == new.value -> Result.failure(
                    IllegalArgumentException("La nueva contraseña debe ser diferente a la actual")
                )
                else -> Result.success(Unit)
            }
        }
    }

    override fun toString(): String = "***"
}