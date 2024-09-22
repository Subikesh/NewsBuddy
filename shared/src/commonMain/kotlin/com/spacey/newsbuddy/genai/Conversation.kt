package com.spacey.newsbuddy.genai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Conversation(
    @SerialName(GenerativeAiService.CONTENT) val content: String,
    @SerialName(GenerativeAiService.LINK) val link: String? = null
)