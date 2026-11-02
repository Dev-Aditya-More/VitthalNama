package com.aditya1875.vitthalnama.utils

import android.content.Context
import com.aditya1875.vitthalnama.data.local.model.Haripath
import com.google.gson.Gson

fun loadHaripath(context: Context): Haripath {
    val json = context.assets.open("HaripathSource.json").bufferedReader().use { it.readText() }
    return Gson().fromJson(json, Haripath::class.java)
}
