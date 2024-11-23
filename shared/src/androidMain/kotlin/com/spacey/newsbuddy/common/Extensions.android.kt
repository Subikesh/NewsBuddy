package com.spacey.newsbuddy.common

import android.util.Log
import com.google.firebase.vertexai.type.Content
import com.google.firebase.vertexai.type.content
import com.spacey.newsbuddy.genai.ChatContent

actual fun log(tag: String, message: String) {
    Log.d(tag, message)
}

actual fun getCurrentTime(): Long {
    return System.currentTimeMillis()
}

fun List<ChatContent>.toGeminiContent(): List<Content> = map {
    content(role = it.role.name.lowercase()) {
        text(it.text)
    }
}