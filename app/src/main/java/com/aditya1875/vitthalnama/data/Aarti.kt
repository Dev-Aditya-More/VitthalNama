package com.aditya1875.vitthalnama.data

import android.content.Context
import com.google.gson.Gson

/** assets/aarti.json – see tools/build_aarti.py. */
data class AartiBook(val categories: List<AartiCategory>)

data class AartiCategory(
    val id: String,
    val title: String,
    val aartis: List<Aarti>
)

data class Aarti(
    val id: String,
    val title: String,
    val author: String = "",
    /** ध्रु – sung after every stanza. */
    val refrain: List<String> = emptyList(),
    val stanzas: List<List<String>>,
    val source: String = "",
    val verified: Boolean = false
)

object AartiRepository {
    @Volatile
    private var cache: AartiBook? = null

    fun get(context: Context): AartiBook {
        cache?.let { return it }
        return synchronized(this) {
            cache ?: runCatching {
                val json = context.applicationContext.assets.open("aarti.json")
                    .bufferedReader(Charsets.UTF_8).use { it.readText() }
                Gson().fromJson(json, AartiBook::class.java)
            }.getOrDefault(AartiBook(emptyList())).also { cache = it }
        }
    }

    fun find(context: Context, id: String): Pair<AartiCategory, Aarti>? =
        get(context).categories.firstNotNullOfOrNull { c ->
            c.aartis.firstOrNull { it.id == id }?.let { c to it }
        }
}
