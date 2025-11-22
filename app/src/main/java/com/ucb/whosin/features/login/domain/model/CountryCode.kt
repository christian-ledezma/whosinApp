package com.ucb.whosin.features.login.domain.model

enum class CountryCode(
    val code: String,
    val country: String,
    val flag: String
) {
    US("+1", "Estados Unidos", "🇺🇸"),
    CA("+1", "Canadá", "🇨🇦"),
    MX("+52", "México", "🇲🇽"),

    GT("+502", "Guatemala", "🇬🇹"),
    SV("+503", "El Salvador", "🇸🇻"),
    HN("+504", "Honduras", "🇭🇳"),
    NI("+505", "Nicaragua", "🇳🇮"),
    CR("+506", "Costa Rica", "🇨🇷"),
    PA("+507", "Panamá", "🇵🇦"),

    // Caribe
    CU("+53", "Cuba", "🇨🇺"),
    DO("+1", "República Dominicana", "🇩🇴"),
    JM("+1", "Jamaica", "🇯🇲"),
    PR("+1", "Puerto Rico", "🇵🇷"),
    TT("+1", "Trinidad y Tobago", "🇹🇹"),

    // América del Sur
    AR("+54", "Argentina", "🇦🇷"),
    BO("+591", "Bolivia", "🇧🇴"),
    BR("+55", "Brasil", "🇧🇷"),
    CL("+56", "Chile", "🇨🇱"),
    CO("+57", "Colombia", "🇨🇴"),
    EC("+593", "Ecuador", "🇪🇨"),
    PY("+595", "Paraguay", "🇵🇾"),
    PE("+51", "Perú", "🇵🇪"),
    UY("+598", "Uruguay", "🇺🇾"),
    VE("+58", "Venezuela", "🇻🇪"),

    // Unión Europea
    DE("+49", "Alemania", "🇩🇪"),
    AT("+43", "Austria", "🇦🇹"),
    BE("+32", "Bélgica", "🇧🇪"),
    BG("+359", "Bulgaria", "🇧🇬"),
    CY("+357", "Chipre", "🇨🇾"),
    HR("+385", "Croacia", "🇭🇷"),
    DK("+45", "Dinamarca", "🇩🇰"),
    SK("+421", "Eslovaquia", "🇸🇰"),
    SI("+386", "Eslovenia", "🇸🇮"),
    ES("+34", "España", "🇪🇸"),
    EE("+372", "Estonia", "🇪🇪"),
    FI("+358", "Finlandia", "🇫🇮"),
    FR("+33", "Francia", "🇫🇷"),
    GR("+30", "Grecia", "🇬🇷"),
    HU("+36", "Hungría", "🇭🇺"),
    IE("+353", "Irlanda", "🇮🇪"),
    IT("+39", "Italia", "🇮🇹"),
    LV("+371", "Letonia", "🇱🇻"),
    LT("+370", "Lituania", "🇱🇹"),
    LU("+352", "Luxemburgo", "🇱🇺"),
    MT("+356", "Malta", "🇲🇹"),
    NL("+31", "Países Bajos", "🇳🇱"),
    PL("+48", "Polonia", "🇵🇱"),
    PT("+351", "Portugal", "🇵🇹"),
    CZ("+420", "República Checa", "🇨🇿"),
    RO("+40", "Rumania", "🇷🇴"),
    SE("+46", "Suecia", "🇸🇪"),

    // Gran Bretaña
    GB("+44", "Reino Unido", "🇬🇧");

    fun displayName(): String = "$flag $country ($code)"

    companion object {
        fun fromCode(code: String): CountryCode? {
            return entries.find { it.code == code }
        }

        fun getAll(): List<CountryCode> = entries.toList()

        // Ordenados por región para mejor UX
        fun getAllGrouped(): Map<String, List<CountryCode>> {
            return mapOf(
                "América del Norte" to listOf(US, CA, MX),
                "Centroamérica" to listOf(GT, SV, HN, NI, CR, PA),
                "Caribe" to listOf(CU, DO, JM, PR, TT),
                "América del Sur" to listOf(AR, BO, BR, CL, CO, EC, PY, PE, UY, VE),
                "Unión Europea" to listOf(DE, AT, BE, BG, CY, HR, DK, SK, SI, ES, EE, FI, FR, GR, HU, IE, IT, LV, LT, LU, MT, NL, PL, PT, CZ, RO, SE),
                "Gran Bretaña" to listOf(GB)
            )
        }
    }
}