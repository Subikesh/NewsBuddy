package com.spacey.newsbuddy.common

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.vertexai.type.Content
import com.google.firebase.vertexai.type.ServerException
import com.google.firebase.vertexai.type.content
import com.spacey.newsbuddy.genai.ChatBubble
import com.spacey.newsbuddy.genai.ChatType

fun initiateFireBaseSdk(context: Application) {
    FirebaseApp.initializeApp(context)
}

actual fun log(tag: String, message: String) {
    Log.d(tag, message)
}

actual fun getCurrentTime(): Long {
    return System.currentTimeMillis()
}

actual fun Result<*>.isAiServerException(): Boolean {
    return exceptionOrNull() is ServerException
}

fun List<ChatBubble>.toGeminiContent(): List<Content> = map {
    content(role = it.type.toGeminiRole()) {
        text(it.chatText)
    }
}

fun ChatType.toGeminiRole(): String =
    when (this) {
        ChatType.USER -> "user"
        ChatType.AI -> "model"
    }