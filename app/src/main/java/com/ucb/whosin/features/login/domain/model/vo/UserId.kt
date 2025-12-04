package com.ucb.whosin.features.login.domain.model.vo

@JvmInline
value class UserId private constructor(val value: String) {
    companion object {
        fun create(id: String): Result<UserId> {
            return when {
                id.isBlank() -> Result.failure(
                    IllegalArgumentException("El ID de usuario no puede estar vacío")
                )
                else -> Result.success(UserId(id))
            }
        }
    }

    override fun toString(): String = value
}