package com.spacey.newsbuddy.home

import android.speech.tts.TextToSpeech
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.spacey.newsbuddy.AppBarContent
import com.spacey.newsbuddy.FabConfig
import com.spacey.newsbuddy.genai.Conversation
import com.spacey.newsbuddy.summary.SummaryScreen

@Composable
fun HomeScreen(
    setAppBarContent: (AppBarContent?) -> Unit,
    setFabConfig: (FabConfig) -> Unit
) {
    LaunchedEffect(key1 = true) {
        setAppBarContent(null)
        setFabConfig(FabConfig {})
    }

    SummaryScreen()
}

fun TextToSpeech.converse(conversations: List<Conversation>, index: Int) {
    stop()
    for (i in index until conversations.size) {
        speak(conversations[i].escapedContent, TextToSpeech.QUEUE_ADD, null, i.toString())
    }
}