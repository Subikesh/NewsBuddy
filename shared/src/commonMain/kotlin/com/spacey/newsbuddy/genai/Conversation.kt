package com.spacey.newsbuddy.genai

import kotlinx.serialization.Serializable

@Serializable
data class Conversation(
    val content: String,
    val link: String? = null
)