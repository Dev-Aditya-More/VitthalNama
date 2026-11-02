package com.aditya1875.vitthalnama.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.aditya1875.vitthalnama.data.ThemeMode

private val LightColorScheme = lightColorScheme(
    primary = SaffronDeep,
    onPrimary = Cream,
    primaryContainer = SaffronContainer,
    onPrimaryContainer = OnSaffronContainer,
    secondary = Indigo,
    onSecondary = Cream,
    secondaryContainer = IndigoContainer,
    onSecondaryContainer = OnIndigoContainer,
    tertiary = Gold,
    onTertiary = Cream,
    tertiaryContainer = GoldContainer,
    onTertiaryContainer = Ink,
    background = Cream,
    onBackground = Ink,
    surface = CreamSurface,
    onSurface = Ink,
    surfaceVariant = CreamVariant,
    onSurfaceVariant = InkVariant,
    surfaceContainerLowest = CreamSurface,
    surfaceContainerLow = CreamSurface,
    surfaceContainer = CreamVariant,
    surfaceContainerHigh = CreamVariant,
    outline = OutlineWarm,
)

private val DarkColorScheme = darkColorScheme(
    primary = SaffronGlow,
    onPrimary = Color(0xFF4F1D00),
    primaryContainer = SaffronGlowContainer,
    onPrimaryContainer = OnSaffronGlowContainer,
    secondary = IndigoGlow,
    onSecondary = Color(0xFF181C5F),
    secondaryContainer = IndigoGlowContainer,
    onSecondaryContainer = IndigoContainer,
    tertiary = GoldGlow,
    onTertiary = Color(0xFF3F2E00),
    tertiaryContainer = GoldGlowContainer,
    onTertiaryContainer = GoldContainer,
    background = Night,
    onBackground = Moon,
    surface = NightSurface,
    onSurface = Moon,
    surfaceVariant = NightVariant,
    onSurfaceVariant = MoonVariant,
    surfaceContainerLowest = Night,
    surfaceContainerLow = NightSurface,
    surfaceContainer = NightVariant,
    surfaceContainerHigh = NightVariant,
    outline = OutlineNight,
)

/**
 * Dynamic (wallpaper) colour is deliberately not used: the saffron/indigo identity
 * is part of the devotional feel of the app.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun VitthalNamaTheme(
    themeMode: ThemeMode = ThemeMode.System,
    content: @Composable () -> Unit
) {
    val dark = when (themeMode) {
        ThemeMode.System -> isSystemInDarkTheme()
        ThemeMode.Light -> false
        ThemeMode.Dark -> true
    }
    MaterialExpressiveTheme(
        colorScheme = if (dark) DarkColorScheme else LightColorScheme,
        typography = Typography,
        motionScheme = MotionScheme.expressive(),
        content = content
    )
}
