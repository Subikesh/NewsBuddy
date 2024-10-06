package com.spacey.newsbuddy.summary

import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spacey.newsbuddy.ListedUiState
import com.spacey.newsbuddy.home.converse
import com.spacey.newsbuddy.ui.CenteredColumn

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SummaryScreen(viewModel: BuddyViewModel = viewModel()) {

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
    val uiState by viewModel.uiState.collectAsState()
    when (val state = uiState) {
        is ListedUiState.Loading -> {
            CenteredColumn {
                CircularProgressIndicator()
                Text(state.message)
            }
        }

        is ListedUiState.Success -> {
            var currentSpeaking: Int by remember {
                mutableIntStateOf(-1)
            }
//            setFabConfig(FabConfig {
//                if (textToSpeech.isSpeaking) {
//                    setFabIcon(Icons.Outlined.PlayArrow)
//                    textToSpeech.stop()
//                } else {
//                    textToSpeech.converse(state.conversations, 0)
//                }
//            })
//            textToSpeech.setOnUtteranceProgressListener(NewsSpeechListener(setFabIcon) {
//                currentSpeaking = it
//            })
            LazyColumn(contentPadding = PaddingValues()) {
                item {
                    Row(
                        Modifier
                            .padding(16.dp)
                            .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        IconButton(onClick = { /*TODO*/ }, colors = IconButtonDefaults.iconButtonColors(
                            MaterialTheme.colorScheme.secondaryContainer)) {
                            Icon(Icons.Default.AccountCircle, contentDescription = "Account", tint = MaterialTheme.colorScheme.onSurface)
                        }
                        IconButton(
                            colors = IconButtonDefaults.iconButtonColors(MaterialTheme.colorScheme.secondaryContainer),
                            onClick = {
                                viewModel.promptTodaysNews(true)
                            }
                        ) {
                            Icon(Icons.Default.Refresh, "Refresh", tint = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
                item {
                    Text(
                        "Good Morning!",
                        Modifier.padding(16.dp),
                        // TODO: Text size based on screen
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                itemsIndexed(items = state.conversations) { i, conversation ->
                    val weight = if (i == currentSpeaking) FontWeight.ExtraBold else null
                    val shape = if (i == 0) RoundedCornerShape(topStart = 35.dp, topEnd = 35.dp) else RoundedCornerShape(0.dp)
                    val textPadding = if (i == 0) PaddingValues(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp) else PaddingValues(vertical = 8.dp, horizontal = 16.dp)
                    Card(shape = shape, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                        Text(text = conversation.content,
                            modifier = Modifier
                                .padding(textPadding)
                                .combinedClickable(
                                    onLongClickLabel = "Open Url",
                                    onLongClick = {
                                        conversation.link?.let {
                                            uriHandler.openUri(it)
                                        }
                                    }) {
                                    textToSpeech.converse(state.conversations, i)
                                },
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = weight,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }
            }
        }

        is ListedUiState.Error -> {
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