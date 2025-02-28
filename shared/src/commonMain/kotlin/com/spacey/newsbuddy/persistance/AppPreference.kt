package com.spacey.newsbuddy.persistance

import com.spacey.newsbuddy.serviceLocator
import kotlin.reflect.KProperty

interface AppPreference {
    fun getStringWithDefault(key: String, default: String): String
    fun putString(key: String, value: String)
    fun getInt(key: String): Int
    fun putInt(key: String, value: Int)
    fun getBooleanOrDefault(key: String, default: Boolean): Boolean
    fun putBoolean(key: String, value: Boolean)
}

internal class Preference(private val key: String, private val default: String = "") {
    inline operator fun <reified T> getValue(thisRef: Any?, property: KProperty<*>): T {
        return when (T::class) {
            String::class -> serviceLocator.preference.getStringWithDefault(key, default)
            Boolean::class -> serviceLocator.preference.getBooleanOrDefault(key, default.toBoolean())
            Int::class -> serviceLocator.preference.getInt(key)
            else -> TODO("Not yet implemented")
        } as T
    }

    operator fun <T> setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        when (value) {
            is String -> serviceLocator.preference.putString(key, value)
            is Boolean -> serviceLocator.preference.putBoolean(key, value)
            is Int -> serviceLocator.preference.putInt(key, value)
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