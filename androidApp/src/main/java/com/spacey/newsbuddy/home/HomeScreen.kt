package com.spacey.newsbuddy.home

import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spacey.newsbuddy.AppBarContent
import com.spacey.newsbuddy.FabConfig
import com.spacey.newsbuddy.genai.Conversation
import com.spacey.newsbuddy.ui.CenteredColumn

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    viewModel: BuddyViewModel = viewModel(),
    setAppBarContent: (AppBarContent) -> Unit,
    setFabIcon: (ImageVector) -> Unit,
    setFabConfig: (FabConfig) -> Unit
) {
    setAppBarContent(AppBarContent {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxSize()) {
            Text("Latest news!", Modifier.weight(1f))
            IconButton(
                modifier = Modifier.padding(end = 16.dp),
                onClick = {
                    viewModel.promptTodaysNews(true)
                },
            ) {
                Icon(Icons.Default.Refresh, "Refresh")
            }
        }
    })
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(key1 = true) {
        viewModel.promptTodaysNews()
    }
    val context = LocalContext.current
    val textToSpeech = TextToSpeech(context) {
        if (it != TextToSpeech.ERROR) {
            Log.d("Speak", "Success")
        }
        Log.d("Speak", it.toString())
    }
    val uriHandler = LocalUriHandler.current
    when (val state = uiState) {
        is BuddyScreenState.Loading -> {
            CenteredColumn {
                CircularProgressIndicator()
                Text(state.message)
            }
        }

        is BuddyScreenState.Success -> {
            var currentSpeaking: Int by remember {
                mutableIntStateOf(-1)
            }
            setFabConfig(FabConfig {
                if (textToSpeech.isSpeaking) {
                    setFabIcon(Icons.Outlined.PlayArrow)
                    textToSpeech.stop()
                } else {
                    textToSpeech.converse(state.conversations, 0)
                }
            })
            textToSpeech.setOnUtteranceProgressListener(NewsSpeechListener(setFabIcon) {
                currentSpeaking = it
            })
            LazyColumn(contentPadding = PaddingValues(16.dp)) {
                itemsIndexed(items = state.conversations) { i, conversation ->
                    val weight = if (i == currentSpeaking) FontWeight.ExtraBold else null
                    Text(text = conversation.content,
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .combinedClickable(onLongClickLabel = "Open Url", onLongClick = {
                                conversation.link?.let {
                                    uriHandler.openUri(it)
                                }
                            }) {
                                textToSpeech.converse(state.conversations, i)
                            },
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = weight)
                    )
                }
            }
        }

        is BuddyScreenState.Error -> {
            CenteredColumn {
                Text(
                    state.message,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

fun TextToSpeech.converse(conversations: List<Conversation>, index: Int) {
    stop()
    for (i in index until conversations.size) {
        speak(conversations[i].escapedContent, TextToSpeech.QUEUE_ADD, null, i.toString())
    }
}