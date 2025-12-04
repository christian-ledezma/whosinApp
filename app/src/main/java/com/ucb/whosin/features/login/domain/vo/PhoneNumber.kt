package com.ucb.whosin.features.login.domain.vo

@JvmInline
value class PhoneNumber  private constructor(val value: String) {
    companion object {
        private val DIGIT_ONLY_PATTERN = Regex("[^0-9]")

        fun create(phone: String, countryCode: String): Result<PhoneNumber> {
            val normalized = normalize(phone)
            val phoneConfig = getPhoneConfig(countryCode)

            return when {
                normalized.isBlank() -> Result.failure(
                    IllegalArgumentException("El número de teléfono no puede estar vacío")
                )
                normalized.length < phoneConfig.minLength -> Result.failure(
                    IllegalArgumentException(
                        "El número de teléfono debe tener al menos ${phoneConfig.minLength} dígitos"
                    )
                )
                normalized.length > phoneConfig.maxLength -> Result.failure(
                    IllegalArgumentException(
                        "El número de teléfono no puede exceder ${phoneConfig.maxLength} dígitos"
                    )
                )
                else -> Result.success(PhoneNumber(normalized))
            }
        }

        private fun normalize(phone: String): String {
            // Remover todo excepto dígitos
            return phone.replace(DIGIT_ONLY_PATTERN, "").trim()
        }

        private data class PhoneConfig(val minLength: Int, val maxLength: Int)

        private fun getPhoneConfig(countryCode: String): PhoneConfig {
            return when (countryCode) {
                "+1" -> PhoneConfig(7, 10)      // US, CA, DO, JM, PR, TT
                "+52" -> PhoneConfig(10, 10)    // MX
                "+502" -> PhoneConfig(8, 8)     // GT
                "+503" -> PhoneConfig(8, 8)     // SV
                "+504" -> PhoneConfig(8, 8)     // HN
                "+505" -> PhoneConfig(8, 8)     // NI
                "+506" -> PhoneConfig(8, 8)     // CR
                "+507" -> PhoneConfig(8, 8)     // PA
                "+53" -> PhoneConfig(8, 8)      // CU
                "+54" -> PhoneConfig(10, 11)    // AR
                "+591" -> PhoneConfig(8, 8)     // BO
                "+55" -> PhoneConfig(10, 11)    // BR
                "+56" -> PhoneConfig(9, 9)      // CL
                "+57" -> PhoneConfig(10, 10)    // CO
                "+593" -> PhoneConfig(9, 9)     // EC
                "+595" -> PhoneConfig(9, 9)     // PY
                "+51" -> PhoneConfig(9, 9)      // PE
                "+598" -> PhoneConfig(8, 9)     // UY
                "+58" -> PhoneConfig(10, 10)    // VE
                "+49" -> PhoneConfig(10, 11)    // DE
                "+43" -> PhoneConfig(10, 13)    // AT
                "+32" -> PhoneConfig(9, 9)      // BE
                "+359" -> PhoneConfig(9, 9)     // BG
                "+357" -> PhoneConfig(8, 8)     // CY
                "+385" -> PhoneConfig(8, 9)     // HR
                "+45" -> PhoneConfig(8, 8)      // DK
                "+421" -> PhoneConfig(9, 9)     // SK
                "+386" -> PhoneConfig(8, 8)     // SI
                "+34" -> PhoneConfig(9, 9)      // ES
                "+372" -> PhoneConfig(7, 8)     // EE
                "+358" -> PhoneConfig(9, 10)    // FI
                "+33" -> PhoneConfig(9, 9)      // FR
                "+30" -> PhoneConfig(10, 10)    // GR
                "+36" -> PhoneConfig(9, 9)      // HU
                "+353" -> PhoneConfig(9, 9)     // IE
                "+39" -> PhoneConfig(9, 10)     // IT
                "+371" -> PhoneConfig(8, 8)     // LV
                "+370" -> PhoneConfig(8, 8)     // LT
                "+352" -> PhoneConfig(9, 9)     // LU
                "+356" -> PhoneConfig(8, 8)     // MT
                "+31" -> PhoneConfig(9, 9)      // NL
                "+48" -> PhoneConfig(9, 9)      // PL
                "+351" -> PhoneConfig(9, 9)     // PT
                "+420" -> PhoneConfig(9, 9)     // CZ
                "+40" -> PhoneConfig(10, 10)    // RO
                "+46" -> PhoneConfig(9, 10)     // SE
                "+44" -> PhoneConfig(10, 10)    // GB
                else -> PhoneConfig(7, 15)      // Default genérico
            }
        }
    }

    override fun toString(): String = value
}