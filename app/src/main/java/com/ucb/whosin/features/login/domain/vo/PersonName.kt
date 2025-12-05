package com.ucb.whosin.features.login.domain.vo

@JvmInline
value class PersonName private constructor(val value: String) {
    companion object {
        private const val MIN_LENGTH = 2
        private const val MAX_LENGTH = 50
        private val VALID_NAME_PATTERN = Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ]+(\\s[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ]+)*$")

        fun create(name: String, fieldName: String = "nombre"): Result<PersonName> {
            val normalized = normalize(name)

            return when {
                normalized.isBlank() -> Result.failure(
                    IllegalArgumentException("El $fieldName no puede estar vacío")
                )
                normalized.length < MIN_LENGTH -> Result.failure(
                    IllegalArgumentException("El $fieldName debe tener al menos $MIN_LENGTH caracteres")
                )
                normalized.length > MAX_LENGTH -> Result.failure(
                    IllegalArgumentException("El $fieldName no puede exceder $MAX_LENGTH caracteres")
                )
                !VALID_NAME_PATTERN.matches(normalized) -> Result.failure(
                    IllegalArgumentException("El $fieldName solo puede contener letras y un espacio para nombres compuestos")
                )
                else -> Result.success(PersonName(normalized))
            }
        }

        private fun normalize(name: String): String {
            return name.trim()
                .replace(Regex("\\s+"), " ") // para últiples espacios
                .uppercase()
        }
    }

    override fun toString(): String = value
}