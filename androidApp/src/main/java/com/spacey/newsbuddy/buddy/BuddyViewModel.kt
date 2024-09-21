package com.spacey.newsbuddy.buddy

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.FunctionCallingConfig
import com.google.ai.client.generativeai.type.FunctionType
import com.google.ai.client.generativeai.type.RequestOptions
import com.google.ai.client.generativeai.type.Schema
import com.google.ai.client.generativeai.type.ToolConfig
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.spacey.newsbuddy.Conversation
import com.spacey.newsbuddy.NewsParser
import com.spacey.newsbuddy.android.BuildConfig
import com.spacey.newsbuddy.serviceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BuddyViewModel : ViewModel() {

    private val _conversationList: MutableStateFlow<List<Conversation>> = MutableStateFlow(emptyList())
    val conversationList: StateFlow<List<Conversation>> = _conversationList

    private val newsParser = NewsParser()

    private val model = GenerativeModel(
        "gemini-1.5-pro",
        // Retrieve API key as an environmental variable defined in a Build Configuration
        // see https://github.com/google/secrets-gradle-plugin for further instructions
        BuildConfig.GEMINI_API_KEY,
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
            text(
                "I will share you a json array of today's news headlines response. " +
                    "Summarise those and create a conversation styled news curation. " +
                    "Give the text in separate key in json and try to provide proper link to that article near the corresponding article's content like { '$CONVO': \"Some long conversation text\", '$LINK': 'That link here'} so all the convo text can be combined by me to frame the final news summary." +
                    "Here, each item in article summarization, add catchy and linking conversation like statements. "
            )
        },
        toolConfig = ToolConfig(FunctionCallingConfig(FunctionCallingConfig.Mode.ANY))
    )

    fun promptTodaysNews() {
        viewModelScope.launch {
            model.startChat()
            val news = serviceLocator.newsRepository.getTodaysNews()
            if (news.isSuccess) {
                val contentStream = model.generateContentStream(content { text(news.getOrThrow().toString()) })
                Log.i("News", "News response: $news")
                val content = buildString {
                    contentStream.collect {
                        if (it.text != null) {
                            append(it.text)
                        }
                    }
                }
                Log.d("AI response", content)
                _conversationList.value = newsParser.parseJson(content)
            } else {
                Log.e("News", "News error: ${news.exceptionOrNull()}")
                _conversationList.value = listOf(Conversation("Error occurred when fetching today's news!"))
            }
        }
    }

    companion object {
        const val CONVO = "convo"
        const val LINK = "link"
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
                        "newsItem", "Single article news item", type = FunctionType.OBJECT, required = listOf(CONVO), properties = mapOf(
                            CONVO to Schema(CONVO, "The conversation content for the news article", type = FunctionType.STRING),
                            LINK to Schema(LINK, "The link to the conversation for the news article", type = FunctionType.STRING)
                        )
                    )
                )
            )
        )

        const val PROMPT = "{" +
                "  \"status\": \"ok\"," +
                "  \"totalResults\": 17," +
                "  \"articles\": [" +
                "    {" +
                "      \"source\": {" +
                "        \"id\": \"nbc-news\"," +
                "        \"name\": \"NBC News\"" +
                "      }," +
                "      \"author\": \"Sakshi Venkatraman\"," +
                "      \"title\": \"Harris targets Asian American voters with ad about her mother\"," +
                "      \"description\": \"Vice President Kamala Harris’ campaign highlights her mom, an Indian immigrant and cancer researcher who fought for civil rights.\"," +
                "      \"url\": \"https://www.nbcnews.com/news/asian-america/harris-targets-asian-american-voters-ad-mother-rcna171651\"," +
                "      \"urlToImage\": \"https://media-cldnry.s-nbcnews.com/image/upload/t_nbcnews-fp-1200-630,f_auto,q_auto:best/rockcms/2024-09/240918-Kamala-Harris-Shyamala-Harris-al-1119-0dfaa0.jpg\"," +
                "      \"publishedAt\": \"2024-09-18T16:32:20Z\"," +
                "      \"content\": \"Vice President Kamala Harris presidential campaign is making its latest push to Asian American voters, releasing a personal ad about her mother, Shyamala Gopalan. \r\nThe 60-second ad, titled My Mother… [+1466 chars]\"" +
                "    }," +
                "    {" +
                "      \"source\": {" +
                "        \"id\": \"google-news-in\"," +
                "        \"name\": \"Google News (India)\"" +
                "      }," +
                "      \"author\": \"The Hindu\"," +
                "      \"title\": \"Mpox in India: Second case of infection confirmed in UAE returnee in Kerala’s Malappuram - The Hindu\"," +
                "      \"description\": null," +
                "      \"url\": \"https://news.google.com/rss/articles/CBMiugFBVV95cUxQOXBENDNSZ3UxZTVHYnNoYUY4dG11NjJORV9haTBXems4NmFqTU16SG95cmR3MXEzd1ZTVkJXWnVJNG5WQ1NYWUUzc0VUcjlaWVJnMkJocjBnVzMyeHVWZ0JzeFBBSURod0F2WFVuaE1fVVUtYW51RnhJNHBrNHhZMzVoVktnTTE5Z2JQMTZUQzBnZUlONW1mdmhfVE9wc09heDBuNTdaN0lwWldjMENQUFdxdjc4eWpPRFHSAcABQVVfeXFMTXJUMWJkbEtyOXRETEcwNWhhV20tUzdpN2xpUTIwdGhCWTZJeXBJWFFWZGlfY2w3VkZqWUZkZ1JNX2tWRlA5T2p1OHpBV1pwd0JRNkdTdWRmeFNyMnVtZDFBNGJzVkxGRDJQNmFELWg2T3hidWZTdWh1TjhwYnpoNDV2Mjc3YzdQaHFVWGNRTmxKZzZuU1I2dmNkZDZfZEFoS3U0c0dKejkwN0V4MGJsWXJ0U0N1N2hjUEVhMm0wMGRV?oc=5\"," +
                "      \"urlToImage\": null," +
                "      \"publishedAt\": \"2024-09-18T14:44:00+00:00\"," +
                "      \"content\": null" +
                "    }," +
                "    {" +
                "      \"source\": {" +
                "        \"id\": \"google-news-in\"," +
                "        \"name\": \"Google News (India)\"" +
                "      }," +
                "      \"author\": \"The Hindu\"," +
                "      \"title\": \"Indus Water Treaty: No more Indus Commission meetings till Treaty renegotiated, India tells Pakistan - The Hindu\"," +
                "      \"description\": null," +
                "      \"url\": \"https://news.google.com/rss/articles/CBMixgFBVV95cUxOZTdOZTAzZkJjVU9pM0VXVV9XVjJubWpNRkdRWG5KbVBxNFpfZ1dUQWdIQ2xlNWNTMUplQ2QtUEtKdEN3QzducWUxM0Y4NnJoaXd5TFRqWTAydHNiYjU2TlhhU3RGY0JFeEw5bUNGVC1XRExxZ0ZFS2VSTy1Td3IxQ1JrS2N4OWdXY09sTGx6bjlfTGtBOEh2WFdzVVBmQUZRamdOTjNNbWRVdC1acWx4eXZPWE1NVjZhb2dRaTdIc3dxZGNBMUHSAcwBQVVfeXFMTVE4SFpjTHA2X2haVUtFU19zempJNnpMWC0tLXFkZlI1OHdyeTJNMnVQRzJJSDEteFNBOVpPQ3lWaFctQmdlU1YybEdHMlNWRVFweVYxcXFwU1RFUXpkMHlkSW9XWmZvOTNaOXRUdXlMY0FtM1NVa3pkWjFBd3ZBN1BDZUFCcVQyWkN3TVd6b19oN0ZKM1lOWnZXTGdOd3l3dnpDclhzOVB2WkNwQkhISEpmV05pUWI5VkxqRHViM0NkZUR2bjJQNmlrSGcy?oc=5\"," +
                "      \"urlToImage\": null," +
                "      \"publishedAt\": \"2024-09-18T12:14:00+00:00\"," +
                "      \"content\": null" +
                "    }" +
                "  ]" +
                "}"
    }
}

