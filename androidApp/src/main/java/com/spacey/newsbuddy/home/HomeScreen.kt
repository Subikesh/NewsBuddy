package com.spacey.newsbuddy.home

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spacey.newsbuddy.AppBarContent
import com.spacey.newsbuddy.FabConfig
import com.spacey.newsbuddy.genai.SummaryParagraph

@Composable
fun HomeScreen(
    setAppBarContent: (AppBarContent?) -> Unit,
    setFabConfig: (FabConfig) -> Unit
) {
    LaunchedEffect(key1 = true) {
        setAppBarContent(null)
        setFabConfig(FabConfig {})
    }

    Column {
        Row(
            Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { /*TODO*/ }, colors = IconButtonDefaults.iconButtonColors(
                MaterialTheme.colorScheme.secondaryContainer)) {
                Icon(Icons.Default.AccountCircle, contentDescription = "Account", tint = MaterialTheme.colorScheme.onSurface)
            }
            IconButton(
                colors = IconButtonDefaults.iconButtonColors(MaterialTheme.colorScheme.secondaryContainer),
                onClick = {
//                    viewModel.promptTodaysNews(true)
                }
            ) {
                Icon(Icons.Default.Refresh, "Refresh", tint = MaterialTheme.colorScheme.onSurface)
            }
        }

        Text(
            "Good Morning!",
            Modifier.padding(16.dp),
            // TODO: Text size based on screen
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        HomeChatList(viewModel())
    }

//    SummaryScreen()
}

fun TextToSpeech.converse(summaries: List<SummaryParagraph>, index: Int) {
    stop()
    for (i in index until summaries.size) {
        speak(summaries[i].escapedContent, TextToSpeech.QUEUE_ADD, null, i.toString())
    }
}