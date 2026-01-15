package com.spacey.newsbuddy.common

import android.content.Context
import com.spacey.newsbuddy.ui.isInternetConnected

class ConnectivityManagerImpl(private val context: Context) : ConnectivityManager {
    override fun isInternetConnected(): Boolean {
        return context.isInternetConnected()
    }
}