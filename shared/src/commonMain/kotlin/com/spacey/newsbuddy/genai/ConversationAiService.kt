package com.spacey.newsbuddy.genai

import com.spacey.newsbuddy.Dependencies
import com.spacey.newsbuddy.GEMINI_1_5_PRO
import dev.shreyaspatil.ai.client.generativeai.GenerativeModel
import dev.shreyaspatil.ai.client.generativeai.type.content
import dev.shreyaspatil.ai.client.generativeai.type.generationConfig

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
        systemInstruction = content(role = "system") { text(
            "INSTRUCTIONS: You are being used as a voice chat companion. " +
                    "You will be provided an elaborate summary of news articles and recent happenings at first with the heading: 'news_input'. " +
                    "Now the user may chat with you regarding the given news articles and you will have to converse on different topics and questions " +
                    "that the user talks, also be sure to add some questions and interesting facts to make the conversation flowing. As a voice assistant, " +
                    "you will provide brief response to the prompts when you are asked to elaborate on particular matter. Prioritise on the actual " +
                    "facts and logic over speculation or guess and try to find the most relevant news article to the prompt and respond. Start with a " +
                    "greeting and a moderate summary to start the conversation just after I share the news_input for the chat."
        ) },
    )

    private val chatHistory = listOf(
        content("user") {
            text(
                "news_input: {&#10; &quot;news_curation&quot;: [&#10;  {&#10;   &quot;content&quot;: &quot;Hey there! Big news from the world of finance - India's foreign exchange reserves have hit an all-time high of $692.3 billion! This is a jump of $2.8 billion from the previous week.  Seems like the RBI's efforts to manage liquidity are paying off, wouldn't you say?&quot;,&#10;   &quot;link&quot;: &quot;https://economictimes.indiatimes.com/news/economy/indicators/indias-forex-reserves-reach-all-time-high-at-692-3-billion-up-2-8-billion-as-of-sept-20/articleshow/113739420.cms&quot;&#10;  },&#10;  {&#10;   &quot;content&quot;: &quot;Speaking of the economy, Rahul Gandhi has some strong words for PM Modi's economic policies. He claims they've led to job losses and hurt small businesses.  Gandhi is calling for a simpler GST system and better banking support for small enterprises. Do you think this is the solution to boosting job creation?&quot;,&#10;   &quot;link&quot;: &quot;https://economictimes.indiatimes.com/news/politics-and-nation/pm-modis-monopoly-model-has-taken-away-jobs-devastated-msmes-rahul-gandhi/articleshow/113740393.cms&quot;&#10;  },&#10;  {&#10;   &quot;content&quot;: &quot;Meanwhile, in Jammu and Kashmir, BJP president J.P. Nadda says the Assembly polls are running smoothly and peacefully.  He praised the youth for choosing peace and development over terrorism. What's your take on the situation in J&amp;K?&quot;,&#10;   &quot;link&quot;: &quot;https://economictimes.indiatimes.com/news/elections/assembly-elections/jammu-kashmir/jammu-and-kashmir-assembly-polls-people-of-jk-rejected-bullets-chose-ballots-says-bjp-chief-j-p-nadda/articleshow/113740583.cms&quot;&#10;  },&#10;  {&#10;   &quot;content&quot;: &quot;Switching gears to international news, Japan's potential next PM, Shigeru Ishiba, has a bold idea – an 'Asian NATO'! He wants to station Japanese troops on U.S. soil to counter China. Washington seems skeptical, but it'll be interesting to see how this plays out. Could this be the start of a new era in Asian security?&quot;,&#10;   &quot;link&quot;: &quot;https://economictimes.indiatimes.com/news/defence/incoming-japan-pm-ishibas-asian-nato-idea-test-for-us-diplomacy/articleshow/113740278.cms&quot;&#10;  },&#10;  {&#10;   &quot;content&quot;: &quot;And finally, some news closer to home.  Amazon India is teaming up with the Labour Ministry to post job openings on the National Career Service portal. This could be a game-changer for job seekers in the booming e-commerce sector. Have you ever used online job portals to find work?&quot;,&#10;   &quot;link&quot;: &quot;https://economictimes.indiatimes.com/tech/startups/amazon-india-inks-pact-with-labour-ministry-to-post-job-opportunities-on-ncs-portal/articleshow/113740577.cms&quot;&#10;  }&#10; ]&#10;}"
            )
        }
    )

    private val chat = convoProcessingModel.startChat(chatHistory)

    /*// Note that sendMessage() is a suspend function and should be called from
// a coroutine scope or another suspend function
    val response = chat.sendMessage("INSERT_INPUT_HERE")

// Get the first text part of the first candidate
    println(response.text)
// Alternatively
    println(response.candidates.first().content.parts.first().asTextOrNull())*/
}