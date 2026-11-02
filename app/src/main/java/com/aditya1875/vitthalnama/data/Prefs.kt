package com.aditya1875.vitthalnama.data

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.compositionLocalOf

enum class ThemeMode(val label: String) {
    System("स्वयंचलित"), Light("प्रकाश"), Dark("रात्र")
}

/** Small SharedPreferences wrapper whose values are Compose state. */
class Prefs(context: Context) {
    private val sp = context.applicationContext.getSharedPreferences("vitthalnama", Context.MODE_PRIVATE)

    var themeMode by mutableStateOf(
        ThemeMode.entries.getOrElse(sp.getInt("theme", 0)) { ThemeMode.System }
    )
        private set

    var fontScale by mutableFloatStateOf(sp.getFloat("font_scale", 1f))
        private set

    var keepScreenOn by mutableStateOf(sp.getBoolean("keep_screen_on", true))
        private set

    private val lastPartState = mutableIntStateOf(0)

    fun updateThemeMode(mode: ThemeMode) {
        themeMode = mode
        sp.edit().putInt("theme", mode.ordinal).apply()
    }

    fun updateFontScale(scale: Float) {
        fontScale = scale
        sp.edit().putFloat("font_scale", scale).apply()
    }

    fun updateKeepScreenOn(on: Boolean) {
        keepScreenOn = on
        sp.edit().putBoolean("keep_screen_on", on).apply()
    }

    /** Zero-based index of the abhang the reader was last on. */
    fun lastPart(granthId: String): Int {
        lastPartState.intValue // read so callers recompose after a save
        return sp.getInt("last_$granthId", 0)
    }

    fun saveLastPart(granthId: String, index: Int) {
        sp.edit().putInt("last_$granthId", index).apply()
        lastPartState.intValue++
    }
}

val LocalPrefs = compositionLocalOf<Prefs> { error("Prefs not provided") }
