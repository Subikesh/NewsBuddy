package com.spacey.newsbuddy.genai

import com.google.firebase.Firebase
import com.google.firebase.vertexai.type.FunctionCallingConfig
import com.google.firebase.vertexai.type.Schema
import com.google.firebase.vertexai.type.ToolConfig
import com.google.firebase.vertexai.type.content
import com.google.firebase.vertexai.type.generationConfig
import com.google.firebase.vertexai.vertexAI
import com.spacey.newsbuddy.common.Dependencies
import com.spacey.newsbuddy.common.log
import com.spacey.newsbuddy.persistance.Preference

actual class SummaryAiService actual constructor(dependencies: Dependencies) {

    private val newsProcessingModel = Firebase.vertexAI.generativeModel(
        GEMINI_1_5_FLASH,
        // Retrieve API key as an environmental variable defined in a Build Configuration
        // see https://github.com/google/secrets-gradle-plugin for further instructions
//        dependencies.getGeminiApiToken(),
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
        toolConfig = ToolConfig(FunctionCallingConfig.any())
    )

    private var ongoingSummaryRequest: Boolean by Preference("ongoing_summary_request")

    actual suspend fun prompt(message: String): Result<String> {
        if (ongoingSummaryRequest) {
            return Result.failure(AiBusyException("Another AI summary request is already running"))
        }
        ongoingSummaryRequest = true
        return runCatching {
            val contentStream = newsProcessingModel.generateContent(content { text(message) })
            val content = contentStream.text?.substringAfter('\n')?.substringBeforeLast('\n') ?: ""
            log("AI response", content)
            content
        }.also { ongoingSummaryRequest = false }
    }

    companion object {
        const val NEWS_CURATION = "news_curation"
        const val CONTENT = "content"
        const val LINK = "link"
        private const val GEMINI_SYSTEM_CMD =
            "I will share you a json array of today's news headlines response. " +
                    "Summarise those and create a conversation styled news curation. \n" +
                    "Give the text in separate key in json and try to provide the given link from the input in 'link' key of the corresponding article's content " +
                    "so the final summary can be framed by combining all summaries like `{ $NEWS_CURATION: [{'$CONTENT': <summary>, '$LINK': <convo_link>}] }` by following responseSchema definition." +
                    "Make the text more engaging and simple. Feel free to shuffle the articles if you think it's better connected" +
                    ", but try to focus on important articles or topics at first. "

        private val schema = Schema.obj(
//            "News article as conversation",
            description = "News articles converted as conversation with proper link mapped for each conversation",
            properties = mapOf(
                "news" to Schema.array(
                    description = "An array of news articles with conversations and links",
                    items = Schema.obj(
                        description = "Single article news item",
                        properties = mapOf(
                            CONTENT to Schema.string(
                                "The conversation content for the news article",
                                nullable = false
                            ),
                            LINK to Schema.string(
                                description = "The link to the conversation for the news article as it is from the input",
                                nullable = true
                            )
                        )
                    ),
                    nullable = false
                )
            )
        )
    }
}
