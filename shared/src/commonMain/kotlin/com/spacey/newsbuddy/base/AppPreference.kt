package com.spacey.newsbuddy.base

import com.spacey.newsbuddy.serviceLocator
import kotlin.reflect.KProperty

interface AppPreference {
    fun getString(key: String): String

    fun putString(key: String, value: String)
}

class Preference<T>(private val key: String) {
    operator fun getValue(thisRef: T?, property: KProperty<*>): T {
       return when (thisRef) {
            is String? -> serviceLocator.preference.getString(key)
            else -> TODO("Not yet implemented")
        } as T
    }

    operator fun setValue(thisRef: T?, property: KProperty<*>, value: T) {
        when (value) {
            is String -> serviceLocator.preference.putString(key, value)
            else -> TODO("Not yet implemented")
        }
    }
}