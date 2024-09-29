package com.spacey.newsbuddy.genai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Conversation(
    @SerialName(GenerativeAiService.CONTENT) val content: String,
    @SerialName(GenerativeAiService.LINK) val link: String? = null
) {
    val escapedContent: String = content.replace("/[\u2190-\u21FF]|[\u2600-\u26FF]|[\u2700-\u27BF]|[\u3000-\u303F]|[\u1F300-\u1F64F]|[\u1F680-\u1F6FF]/g", "");
}