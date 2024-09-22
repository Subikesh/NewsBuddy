package com.spacey.newsbuddy.genai

import com.spacey.newsbuddy.Dependencies
import com.spacey.newsbuddy.log
import com.spacey.newsbuddy.serviceLocator
import dev.shreyaspatil.ai.client.generativeai.GenerativeModel
import dev.shreyaspatil.ai.client.generativeai.type.FunctionCallingConfig
import dev.shreyaspatil.ai.client.generativeai.type.FunctionType
import dev.shreyaspatil.ai.client.generativeai.type.RequestOptions
import dev.shreyaspatil.ai.client.generativeai.type.Schema
import dev.shreyaspatil.ai.client.generativeai.type.ToolConfig
import dev.shreyaspatil.ai.client.generativeai.type.content
import dev.shreyaspatil.ai.client.generativeai.type.generationConfig
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement

class GenerativeAiService(dependencies: Dependencies) {

    private val newsProcessingModel = GenerativeModel(
        "gemini-1.5-pro",
        // Retrieve API key as an environmental variable defined in a Build Configuration
        // see https://github.com/google/secrets-gradle-plugin for further instructions
        dependencies.getNewsApiToken(),
        requestOptions = RequestOptions(),
        generationConfig = generationConfig {
            temperature = 1f
            topK = 64
            topP = 0.95f
            maxOutputTokens = 8192
            responseMimeType = "application/json"
            responseSchema = schema
        },
        // safetySettings = Adjust safety settings
        // See https://ai.google.dev/gemini-api/docs/safety-settings
        systemInstruction = content(role = "system") {
            text(GEMINI_SYSTEM_CMD)
        },
        toolConfig = ToolConfig(FunctionCallingConfig(FunctionCallingConfig.Mode.ANY))
    )

    suspend fun promptTodaysNews(): List<Conversation> {
        newsProcessingModel.startChat()
        val news = serviceLocator.newsRepository.getTodaysNews()
        return if (news.isSuccess) {
            val contentStream = newsProcessingModel.generateContentStream(content {
                text(
                    news.getOrThrow().toString()
                )
            })
            log("News", "News response: $news")
            val content = buildString {
                contentStream.collect {
                    if (it.text != null) {
                        append(it.text)
                    }
                }
            }
            log("AI response", content)
            parseJson(content)
        } else {
            log("News", "News error: ${news.exceptionOrNull()}")
            listOf(Conversation("Error occurred when fetching today's news!"))
        }
    }

    private fun parseJson(json: String): List<Conversation> {
        val jsonObject: JsonObject = Json.decodeFromString(json)
        return Json.decodeFromJsonElement(jsonObject["news"]!!)
    }

    companion object {
        private const val CONVO = "convo"
        private const val LINK = "link"
        private const val GEMINI_SYSTEM_CMD = "I will share you a json array of today's news headlines response. " +
                "Summarise those and create a conversation styled news curation. " +
                "Give the text in separate key in json and try to provide the given link from the input near the corresponding article's content " +
                "so all the convo text can be combined by me to frame the final news summary." +
                "Make the text more engaging and simple. Feel free to shuffle the articles if you think it's better connected" +
                ", but try to focus on important articles or topics at first. "

        private val schema = Schema(
            "News article as conversation",
            "News articles converted as conversation with proper link alongside each conversation",
            type = FunctionType.OBJECT,
            required = listOf("news"),
            properties = mapOf(
                "news" to Schema(
                    "news",
                    "An array of news articles with conversations and links",
                    type = FunctionType.ARRAY,
                    items = Schema(
                        "newsItem",
                        "Single article news item",
                        type = FunctionType.OBJECT,
                        required = listOf(CONVO),
                        properties = mapOf(
                            CONVO to Schema(
                                CONVO,
                                "The conversation content for the news article",
                                type = FunctionType.STRING
                            ),
                            LINK to Schema(
                                LINK,
                                "The link to the conversation for the news article",
                                type = FunctionType.STRING
                            )
                        )
                    )
                )
            )
        )
    }
}
