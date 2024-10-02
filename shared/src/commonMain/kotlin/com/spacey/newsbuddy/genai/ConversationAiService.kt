package com.spacey.newsbuddy.genai

import com.spacey.newsbuddy.Dependencies
import com.spacey.newsbuddy.GEMINI_1_5_PRO
import dev.shreyaspatil.ai.client.generativeai.GenerativeModel
import dev.shreyaspatil.ai.client.generativeai.type.BlockThreshold
import dev.shreyaspatil.ai.client.generativeai.type.Content
import dev.shreyaspatil.ai.client.generativeai.type.HarmCategory
import dev.shreyaspatil.ai.client.generativeai.type.SafetySetting
import dev.shreyaspatil.ai.client.generativeai.type.content
import dev.shreyaspatil.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class ConversationAiService(dependencies: Dependencies) {

    private val convoProcessingModel = GenerativeModel(
        GEMINI_1_5_PRO,
        // Retrieve API key as an environmental variable defined in a Build Configuration
        // see https://github.com/google/secrets-gradle-plugin for further instructions
        dependencies.getGeminiApiToken(),
        generationConfig = generationConfig {
            temperature = 0.7f
            topK = 64
            topP = 0.95f
            maxOutputTokens = 600
            responseMimeType = "text/plain"
        },
        // safetySettings = Adjust safety settings
        // See https://ai.google.dev/gemini-api/docs/safety-settings
        systemInstruction = content(role = "system") {
            text(
                "INSTRUCTIONS: You are being used as a voice chat companion. \n" +
                "You will be provided an elaborate summary of news articles and recent happenings at first with the heading: 'news_input'.\n" +
                "The user will chat with you regarding the given news articles and you can answer user's queries and also lead \n" +
                "them to different topics and articles to cover all the news of his interest, also be sure to add some \n" +
                "questions and interesting facts to make the conversation flowing. As a voice assistant, you will provide brief response\n" +
                " to the prompts unless if you are asked to elaborate on particular matter. Prioritise on the actual \n" +
                "facts and logic over speculation or guess and try to find the most relevant news article to the prompt and respond. Start with a \n" +
                "greeting and a moderate summary to start the conversation just after I share the news_input for the chat."
            )
        },
        safetySettings = listOf(
            SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.NONE),
            SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.NONE),
            SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.NONE),
            SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.NONE)
        )
    )

    private val chatHistory: List<Content> = listOf()

    private val chat = convoProcessingModel.startChat(chatHistory)

    suspend fun chat(prompt: String): Result<Flow<String?>> {
        return runCatching {
            val result = chat.sendMessageStream(prompt)
            result.map { it.text }.catch { ex ->
                ex.printStackTrace()
                emit("Error in generating text: ${ex.message}")
            }
        }
    }
    /*// Note that sendMessage() is a suspend function and should be called from
// a coroutine scope or another suspend function
    val response = chat.sendMessage("INSERT_INPUT_HERE")

// Get the first text part of the first candidate
    println(response.text)
// Alternatively
    println(response.candidates.first().content.parts.first().asTextOrNull())*/
}