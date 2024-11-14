package com.spacey.newsbuddy.persistance

import com.spacey.newsbuddy.serviceLocator
import kotlin.reflect.KProperty

interface AppPreference {
    fun getString(key: String): String
    fun putString(key: String, value: String)
}

internal class Preference(private val key: String) {
    inline operator fun <reified T> getValue(thisRef: Any?, property: KProperty<*>): T {
        return when (T::class) {
            String::class -> serviceLocator.preference.getString(key)
            else -> TODO("Not yet implemented")
        } as T
    }

    operator fun <T> setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        when (value) {
            is String -> serviceLocator.preference.putString(key, value)
            else -> TODO("Not yet implemented")
        }
    }

    companion object {
        // Usage
        // private var somePrefs: String by Preference("some_key")

        const val NEWS_RESPONSE = "news_response"
        const val AI_RESPONSE = "ai_response"
        const val CACHE_DATE = "cache_date"
    }
}