package com.spacey.newsbuddy

actual fun log(tag: String, message: String) {
    println("$tag: $message")
}

actual fun getCurrentTime(): Long {
    TODO("Not yet implemented")
}