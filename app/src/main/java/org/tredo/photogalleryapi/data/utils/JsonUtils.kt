package org.tredo.photogalleryapi.data.utils

import android.content.Context
import com.google.gson.Gson

private val gson = Gson()

internal inline fun <reified T> fromJson(value: String): T? {
    return gson.fromJson(value, T::class.java)
}

internal fun Context.jsonFromAssets(fileName: String): String {
    return this.assets.open(fileName).bufferedReader().use { it.readText() }
}