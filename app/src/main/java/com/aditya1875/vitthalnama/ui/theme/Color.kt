package com.aditya1875.vitthalnama.ui.theme

import androidx.compose.ui.graphics.Color

// Light: warm cream paper, deep saffron, indigo accents.
val SaffronDeep = Color(0xFF9A3F00)
val SaffronContainer = Color(0xFFFFDCC2)
val OnSaffronContainer = Color(0xFF331200)
val Indigo = Color(0xFF454A8F)
val IndigoContainer = Color(0xFFE0E0FF)
val OnIndigoContainer = Color(0xFF00006E)
val Gold = Color(0xFF7A5900)
val GoldContainer = Color(0xFFFFDF9E)
val Cream = Color(0xFFFFF8EE)
val CreamSurface = Color(0xFFFFFDF8)
val CreamVariant = Color(0xFFF6E8D6)
val Ink = Color(0xFF241A12)
val InkVariant = Color(0xFF52443A)
val OutlineWarm = Color(0xFF85736A)

// Dark: deep night-indigo with glowing saffron.
val SaffronGlow = Color(0xFFFFB787)
val SaffronGlowContainer = Color(0xFF7A3000)
val OnSaffronGlowContainer = Color(0xFFFFDCC2)
val IndigoGlow = Color(0xFFBFC2FF)
val IndigoGlowContainer = Color(0xFF2F3477)
val GoldGlow = Color(0xFFF1BF48)
val GoldGlowContainer = Color(0xFF5B4300)
val Night = Color(0xFF14111E)
val NightSurface = Color(0xFF1C1829)
val NightVariant = Color(0xFF2A2538)
val Moon = Color(0xFFEDE0EA)
val MoonVariant = Color(0xFFCFC2CC)
val OutlineNight = Color(0xFF988D96)

/** Gradient pairs per aarti category. */
object AartiColors {
    fun of(categoryId: String): Pair<Color, Color> = when (categoryId) {
        "ganpati" -> Color(0xFFC2410C) to Color(0xFFF08A24)
        "shankar" -> Color(0xFF334155) to Color(0xFF64748B)
        "devi" -> Color(0xFFA11D3F) to Color(0xFFDB5A74)
        "vitthal" -> Color(0xFF3B3F86) to Color(0xFF6A63B8)
        "datta" -> Color(0xFF8A6200) to Color(0xFFD1A02B)
        "sant" -> Color(0xFF1F6B63) to Color(0xFF3F9A8B)
        "sai" -> Color(0xFF8E3B2E) to Color(0xFFCB6A4C)
        else -> Color(0xFF5B4636) to Color(0xFF9A7B5F)
    }
}

/** Gradient pairs for the granth cards (top-left -> bottom-right). */
object GranthColors {
    val dnyaneshwar = Color(0xFFB4530A) to Color(0xFFE08A2B)
    val tukaram = Color(0xFF3B3F86) to Color(0xFF6A63B8)
    val eknath = Color(0xFF8E3B2E) to Color(0xFFCB6A4C)
    val nivruttinath = Color(0xFF1F6B63) to Color(0xFF3F9A8B)
    val pasaydan = Color(0xFF8A6200) to Color(0xFFD1A02B)

    fun of(id: String): Pair<Color, Color> = when (id) {
        "dnyaneshwar" -> dnyaneshwar
        "tukaram" -> tukaram
        "eknath" -> eknath
        "nivruttinath" -> nivruttinath
        else -> pasaydan
    }
}
