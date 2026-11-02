package com.aditya1875.vitthalnama.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.aditya1875.vitthalnama.R

/** Serif Devanagari for the sacred text itself. */
val TiroMarathi = FontFamily(
    Font(R.font.tiro_devanagari_marathi_regular, FontWeight.Normal),
    Font(R.font.tiro_devanagari_marathi_regular, FontWeight.Medium),
    Font(R.font.tiro_devanagari_marathi_regular, FontWeight.Bold),
    Font(R.font.tiro_devanagari_marathi_italic, FontWeight.Normal, FontStyle.Italic),
)

/** Clean sans Devanagari for UI chrome. */
val Mukta = FontFamily(
    Font(R.font.mukta_regular, FontWeight.Normal),
    Font(R.font.mukta_semibold, FontWeight.Medium),
    Font(R.font.mukta_semibold, FontWeight.SemiBold),
    Font(R.font.mukta_bold, FontWeight.Bold),
)

private fun ui(size: Int, line: Int, weight: FontWeight = FontWeight.Normal) =
    TextStyle(fontFamily = Mukta, fontWeight = weight, fontSize = size.sp, lineHeight = line.sp)

private fun scripture(size: Int, line: Int, weight: FontWeight = FontWeight.Normal) =
    TextStyle(fontFamily = TiroMarathi, fontWeight = weight, fontSize = size.sp, lineHeight = line.sp)

// Devanagari has tall matras, so line heights are generous.
val Typography = Typography(
    displayLarge = scripture(52, 68),
    displayMedium = scripture(44, 58),
    displaySmall = scripture(36, 48),
    headlineLarge = scripture(32, 44),
    headlineMedium = scripture(28, 40),
    headlineSmall = scripture(24, 34),
    titleLarge = ui(22, 32, FontWeight.SemiBold),
    titleMedium = ui(17, 26, FontWeight.SemiBold),
    titleSmall = ui(15, 22, FontWeight.SemiBold),
    bodyLarge = ui(17, 28),
    bodyMedium = ui(15, 24),
    bodySmall = ui(13, 20),
    labelLarge = ui(15, 22, FontWeight.SemiBold),
    labelMedium = ui(13, 18, FontWeight.Medium),
    labelSmall = ui(12, 16, FontWeight.Medium),
)
