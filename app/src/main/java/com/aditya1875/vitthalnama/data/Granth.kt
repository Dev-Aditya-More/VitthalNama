package com.aditya1875.vitthalnama.data

import android.content.Context
import com.google.gson.Gson

/** One text (e.g. a haripath), loaded from assets/<id>.json. See tools/build_texts.py. */
data class Granth(
    val id: String,
    val title: String,
    val author: String,
    val kind: String,
    val refrain: List<String>? = null,
    val parts: List<Part>
) {
    val isPrayer get() = kind == "prayer"

    /** "अभंग" for a haripath, "ओवी" for the pasaydan. */
    val unitLabel get() = if (isPrayer) "ओवी" else "अभंग"
}

/** An abhang (or ovi). Each verse is a list of padas (half-lines). */
data class Part(
    val number: Int,
    val verses: List<List<String>>
)

object GranthRepository {
    /** Display order on the home screen. */
    val ids = listOf("dnyaneshwar", "tukaram", "eknath", "nivruttinath", "pasaydan")

    private val cache = HashMap<String, Granth>()

    @Synchronized
    fun get(context: Context, id: String): Granth? {
        cache[id]?.let { return it }
        return runCatching {
            val json = context.applicationContext.assets.open("$id.json")
                .bufferedReader(Charsets.UTF_8).use { it.readText() }
            Gson().fromJson(json, Granth::class.java)
        }.getOrNull()?.also { cache[id] = it }
    }

    fun all(context: Context): List<Granth> = ids.mapNotNull { get(context, it) }
}

/** 12 -> "१२" */
fun Int.toMarathi(): String =
    toString().map { if (it in '0'..'9') '०' + (it - '0') else it }.joinToString("")
