package com.spacey.newsbuddy.base

import android.content.Context
import android.content.SharedPreferences

class PreferenceImpl(context: Context) : AppPreference, SharedPreferences by context.getSharedPreferences("news_buddy", Context.MODE_PRIVATE) {

    override fun getString(key: String): String = getString(key, null) ?: ""
    override fun putString(key: String, value: String) {
        edit().putString(key, value).apply()
    }
}