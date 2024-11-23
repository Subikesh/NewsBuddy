package com.spacey.newsbuddy.common

import android.app.Application
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.initialize
import com.google.firebase.vertexai.type.Content
import com.google.firebase.vertexai.type.content
import com.spacey.newsbuddy.genai.ChatBubble

fun initiateFireBaseSdk(context: Application) {
    Firebase.initialize(context)
}

actual fun log(tag: String, message: String) {
    Log.d(tag, message)
}

actual fun getCurrentTime(): Long {
    return System.currentTimeMillis()
}

fun List<ChatBubble>.toGeminiContent(): List<Content> = map {
    content(role = it.type.name.lowercase()) {
        text(it.chatText)
    }
}