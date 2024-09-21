package com.spacey.newsbuddy

actual fun log(tag: String, message: String) {
    println("$tag: $message")
}