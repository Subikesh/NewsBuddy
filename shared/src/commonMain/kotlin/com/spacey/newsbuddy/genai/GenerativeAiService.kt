package com.spacey.newsbuddy.genai

import com.spacey.newsbuddy.Dependencies
import com.spacey.newsbuddy.GEMINI_1_5_PRO
import com.spacey.newsbuddy.log
import dev.shreyaspatil.ai.client.generativeai.GenerativeModel
import dev.shreyaspatil.ai.client.generativeai.type.FunctionCallingConfig
import dev.shreyaspatil.ai.client.generativeai.type.FunctionType
import dev.shreyaspatil.ai.client.generativeai.type.RequestOptions
import dev.shreyaspatil.ai.client.generativeai.type.Schema
import dev.shreyaspatil.ai.client.generativeai.type.ToolConfig
import dev.shreyaspatil.ai.client.generativeai.type.content
import dev.shreyaspatil.ai.client.generativeai.type.generationConfig

class GenerativeAiService(dependencies: Dependencies) {

    private val newsProcessingModel = GenerativeModel(
        GEMINI_1_5_PRO,
        // Retrieve API key as an environmental variable defined in a Build Configuration
        // see https://github.com/google/secrets-gradle-plugin for further instructions
        dependencies.getGeminiApiToken(),
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

    suspend fun runPrompt(news: String): Result<String> {
        return runCatching {
            val contentStream = newsProcessingModel.generateContent(content { text(news) })
            val content = contentStream.text?.substringAfter('\n')?.substringBeforeLast('\n') ?: ""
            log("AI response", content)
            content
        }
    }

    companion object {
        const val NEWS_CURATION = "news_curation"
        const val CONTENT = "content"
        const val LINK = "link"
        private const val GEMINI_SYSTEM_CMD = "I will share you a json array of today's news headlines response. " +
                "Summarise those and create a conversation styled news curation. \n" +
                "Give the text in separate key in json and try to provide the given link from the input in 'link' key of the corresponding article's content " +
                "so the final summary can be framed by combining all summaries like `{ $NEWS_CURATION: [{'$CONTENT': <summary>, '$LINK': <convo_link>}] }` by following responseSchema definition." +
                "Make the text more engaging and simple. Feel free to shuffle the articles if you think it's better connected" +
                ", but try to focus on important articles or topics at first. "

        private val schema = Schema(
            "News article as conversation",
            "News articles converted as conversation with proper link alongside each conversation",
            type = FunctionType.OBJECT,
            required = listOf("news"),
            properties = mapOf(
                "news" to Schema(
                    NEWS_CURATION,
                    "An array of news articles with conversations and links",
                    type = FunctionType.ARRAY,
                    items = Schema(
                        "newsItem",
                        "Single article news item",
                        type = FunctionType.OBJECT,
                        required = listOf(CONTENT),
                        properties = mapOf(
                            CONTENT to Schema(
                                CONTENT,
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
