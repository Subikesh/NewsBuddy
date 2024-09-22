package com.spacey.newsbuddy.base

import android.content.Context

class PreferenceImpl(context: Context) {
    private val preference = context.getSharedPreferences("news_buddy", Context.MODE_PRIVATE)

    fun getString(key: String): String = preference.getString(key, null) ?: ""
    fun putString(key: String, value: String) {
        preference.edit().putString(key, value).apply()
    }
}