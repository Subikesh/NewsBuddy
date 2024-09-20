package com.spacey.newsbuddy

import kotlinx.serialization.SerialInfo
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement

class NewsParser {
    fun parseJson(json: String): List<Conversation> {
        val jsonObject: JsonObject = Json.decodeFromString(json)
        return Json.decodeFromJsonElement(jsonObject["news"]!!)
    }
}

@Serializable
data class Conversation(
    val convo: String,
    val link: String? = null
)