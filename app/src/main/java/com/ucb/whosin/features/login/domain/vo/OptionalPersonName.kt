package com.ucb.whosin.features.login.domain.vo

@JvmInline
value class OptionalPersonName private constructor(val value: String?) {
    companion object {
        private const val MIN_LENGTH = 2
        private const val MAX_LENGTH = 50
        private val VALID_NAME_PATTERN = Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ]+(\\s[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ]+)?$")

        fun create(name: String?): Result<OptionalPersonName> {
            // Si es null o vacío, es válido (es opcional)
            if (name.isNullOrBlank()) {
                return Result.success(OptionalPersonName(null))
            }

            val normalized = normalize(name)

            return when {
                normalized.length < MIN_LENGTH -> Result.failure(
                    IllegalArgumentException("El apellido debe tener al menos $MIN_LENGTH caracteres")
                )
                normalized.length > MAX_LENGTH -> Result.failure(
                    IllegalArgumentException("El apellido no puede exceder $MAX_LENGTH caracteres")
                )
                !VALID_NAME_PATTERN.matches(normalized) -> Result.failure(
                    IllegalArgumentException("El apellido solo puede contener letras y un espacio")
                )
                else -> Result.success(OptionalPersonName(normalized))
            }
        }

        private fun normalize(name: String): String {
            return name.trim()
                .replace(Regex("\\s+"), " ")
                .uppercase()
        }
    }

    override fun toString(): String = value ?: ""

    fun orEmpty(): String = value ?: ""
}