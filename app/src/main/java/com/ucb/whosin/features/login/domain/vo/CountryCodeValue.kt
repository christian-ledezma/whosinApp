package com.ucb.whosin.features.login.domain.vo

import com.ucb.whosin.features.login.domain.model.CountryCode

@JvmInline
value class CountryCodeValue(val value: String) {
    companion object {
        private val VALID_CODES = setOf(
            "+1", "+52", "+502", "+503", "+504", "+505", "+506", "+507",
            "+53", "+54", "+591", "+55", "+56", "+57", "+593", "+595",
            "+51", "+598", "+58", "+49", "+43", "+32", "+359", "+357",
            "+385", "+45", "+421", "+386", "+34", "+372", "+358", "+33",
            "+30", "+36", "+353", "+39", "+371", "+370", "+352", "+356",
            "+31", "+48", "+351", "+420", "+40", "+46", "+44"
        )

        fun create(code: String): Result<CountryCodeValue> {
            return when {
                code.isBlank() -> Result.failure(
                    IllegalArgumentException("El código de país no puede estar vacío")
                )
                code !in VALID_CODES -> Result.failure(
                    IllegalArgumentException("Código de país no válido: $code")
                )
                else -> Result.success(CountryCodeValue(code))
            }
        }

        fun fromEnum(countryCode: CountryCode): CountryCodeValue {
            return CountryCodeValue(countryCode.code)
        }
    }

    override fun toString(): String = value
}