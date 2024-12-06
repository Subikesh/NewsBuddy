package com.spacey.newsbuddy.genai

import com.google.firebase.Firebase
import com.google.firebase.vertexai.type.HarmBlockThreshold
import com.google.firebase.vertexai.type.HarmCategory
import com.google.firebase.vertexai.type.SafetySetting
import com.google.firebase.vertexai.type.content
import com.google.firebase.vertexai.type.generationConfig
import com.google.firebase.vertexai.vertexAI
import com.spacey.newsbuddy.persistance.Preference

actual class TitleAiService {

    // TODO: Configure the temp and topK params?
    private val titleProcessingModel by lazy {
        Firebase.vertexAI.generativeModel(
            GEMINI_1_5_FLASH,
            generationConfig = generationConfig {
                temperature = 0.7f
                topK = 10
                topP = 0.95f
                maxOutputTokens = 50
                responseMimeType = "text/plain"
            },
            // safetySettings = Adjust safety settings
            // See https://ai.google.dev/gemini-api/docs/safety-settings
            systemInstruction = content(role = "system") {
                text("You will be given the introductory message about some news articles about some day. Generate a short and concise title for that chat screen" +
                        "Only include info about the news content included in the message and not anything about conversation or greetings. " +
                        "Just create a catchy phrase headline by picking the important two or three news. If it has commas, end with 'and' to make it complete.")
            },
            safetySettings = listOf(
                SafetySetting(HarmCategory.DANGEROUS_CONTENT, HarmBlockThreshold.LOW_AND_ABOVE),
                SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, HarmBlockThreshold.LOW_AND_ABOVE),
                SafetySetting(HarmCategory.HATE_SPEECH, HarmBlockThreshold.ONLY_HIGH),
                SafetySetting(HarmCategory.HARASSMENT, HarmBlockThreshold.ONLY_HIGH)
            )
        )
    }

    private var ongoingTitleRequest: Boolean by Preference("ongoing_title_request")

    actual suspend fun prompt(message: String): Result<String> {
        if (ongoingTitleRequest) {
            return Result.failure(AiBusyException("Title generation model is already running"))
        }
        ongoingTitleRequest = true
        return runCatching {
            titleProcessingModel.generateContent(message).text ?: ""
        }.also {
            ongoingTitleRequest = false
        }
    }
}