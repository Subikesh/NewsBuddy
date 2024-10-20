package com.spacey.newsbuddy.common

import android.util.Log

actual fun log(tag: String, message: String) {
    Log.d(tag, message)
}

actual fun getCurrentTime(): Long {
    return System.currentTimeMillis()
}