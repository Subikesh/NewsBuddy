package com.spacey.newsbuddy.common

actual fun log(tag: String, message: String) {
    println("$tag: $message")
}

actual fun getCurrentTime(): Long {
    TODO("Not yet implemented")
}

actual fun Result<*>.isAiServerException(): Boolean {
    return false
}