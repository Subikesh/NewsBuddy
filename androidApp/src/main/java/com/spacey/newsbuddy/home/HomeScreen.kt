package com.spacey.newsbuddy.home

import android.speech.tts.TextToSpeech
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spacey.newsbuddy.AppBarContent
import com.spacey.newsbuddy.FabConfig
import com.spacey.newsbuddy.genai.SummaryParagraph
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

    HomeChatList(viewModel())
    SummaryScreen()
}

fun TextToSpeech.converse(summaries: List<SummaryParagraph>, index: Int) {
    stop()
    for (i in index until summaries.size) {
        speak(summaries[i].escapedContent, TextToSpeech.QUEUE_ADD, null, i.toString())
    }
}