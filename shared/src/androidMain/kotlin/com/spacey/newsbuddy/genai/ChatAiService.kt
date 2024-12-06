package com.spacey.newsbuddy.genai

import com.google.firebase.Firebase
import com.google.firebase.vertexai.type.HarmBlockThreshold
import com.google.firebase.vertexai.type.HarmCategory
import com.google.firebase.vertexai.type.SafetySetting
import com.google.firebase.vertexai.type.content
import com.google.firebase.vertexai.type.generationConfig
import com.google.firebase.vertexai.vertexAI
import com.spacey.newsbuddy.common.Dependencies
import com.spacey.newsbuddy.common.toGeminiContent
import com.spacey.newsbuddy.persistance.Preference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion

actual class ChatAiService actual constructor(
    chatHistory: List<ChatBubble>,
    newsResponseText: String
) {

    // TODO: Authentication?
    private val convoProcessingModel by lazy {
        Firebase.vertexAI.generativeModel(
            GEMINI_1_5_FLASH,
            generationConfig = generationConfig {
                temperature = 0.7f
                topK = 28
                topP = 0.95f
                maxOutputTokens = 600
                responseMimeType = "text/plain"
            },
            // safetySettings = Adjust safety settings
            // See https://ai.google.dev/gemini-api/docs/safety-settings
            systemInstruction = content(role = "system") {
                text(
                    "INSTRUCTIONS: You are being used as a voice chat companion. \n" +
                            "The user will chat with you regarding the given news articles and you can answer user's queries and also lead \n" +
                            "them to different topics and articles to cover all the news of his interest, also be sure to add some \n" +
                            "questions and interesting facts linking to another news article to make the conversation flowing. As a voice assistant, you will provide brief response\n" +
                            " to the prompts unless if you are asked to elaborate on particular matter. Prioritise on the actual \n" +
                            "facts and logic over speculation or guess and try to find the most relevant news article to the prompt and respond. Start with a \n" +
                            "greeting and a moderate summary with the most important topics to start the conversation.\n Here's the news response for context: $newsResponseText"
                )
            },
            safetySettings = listOf(
                SafetySetting(HarmCategory.DANGEROUS_CONTENT, HarmBlockThreshold.ONLY_HIGH),
                SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, HarmBlockThreshold.NONE),
                SafetySetting(HarmCategory.HATE_SPEECH, HarmBlockThreshold.NONE),
                SafetySetting(HarmCategory.HARASSMENT, HarmBlockThreshold.ONLY_HIGH)
            )
        )
    }

    private var ongoingChatRequest: Boolean by Preference("ongoing_chat_request")

    private val chat = convoProcessingModel.startChat(chatHistory.toGeminiContent())

    actual suspend fun prompt(message: String): Result<Flow<String?>> {
        if (ongoingChatRequest) {
            return Result.failure(AiBusyException("Another Chat AI request is already running"))
        }
        ongoingChatRequest = true
        return runCatching {
            val result = chat.sendMessageStream(message)
            result.map { it.text }.catch { ex ->
                ex.printStackTrace()
                emit("Error in generating text: ${ex.message}")
            }.onCompletion {
                ongoingChatRequest = false
            }
        }
    }
}