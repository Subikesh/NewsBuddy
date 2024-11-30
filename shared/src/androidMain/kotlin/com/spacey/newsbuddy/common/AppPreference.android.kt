package com.spacey.newsbuddy.common

import android.content.Context
import android.content.SharedPreferences
import com.spacey.newsbuddy.persistance.AppPreference

class PreferenceImpl(context: Context) : AppPreference, SharedPreferences by context.getSharedPreferences("news_buddy", Context.MODE_PRIVATE) {
    override fun getStringWithDefault(key: String, default: String): String = getString(key, null) ?: default
    override fun putString(key: String, value: String) {
        edit().putString(key, value).apply()
    }

    override fun getInt(key: String): Int = getInt(key, 0)
    override fun putInt(key: String, value: Int) {
        edit().putInt(key, value).apply()
    }

    override fun getBoolean(key: String): Boolean = getBoolean(key, false)
    override fun putBoolean(key: String, value: Boolean) {
        edit().putBoolean(key, value).apply()
    }
}