package com.spacey.newsbuddy.summary

import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spacey.newsbuddy.ListedUiState
import com.spacey.newsbuddy.ui.CenteredTopBar
import com.spacey.newsbuddy.ui.LoadingScreen
import com.spacey.newsbuddy.ui.MessageScreen
import com.spacey.newsbuddy.ui.formatToHomeDateDisplay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SummaryScreen(date: String, viewModel: SummaryViewModel = viewModel(), navigateBack: () -> Unit) {
    val refreshState by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(refreshState) {
        viewModel.promptNews(date)
    }

    BackHandler(true, navigateBack)

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
            LoadingScreen(text = "Summarizing today's news 🗞️\nPlease give me a minute...")
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
            LazyColumn(contentPadding = PaddingValues(16.dp)) {
                item {
                    CenteredTopBar("News on ${date.formatToHomeDateDisplay()}", navigationIcon = {}, trailingIcon = {})
                }

                itemsIndexed(items = state.resultList) { i, conversation ->
                    val weight = if (i == currentSpeaking) FontWeight.ExtraBold else null
                    val shape = if (i == 0) RoundedCornerShape(topStart = 35.dp, topEnd = 35.dp)
                    else if (i == state.resultList.size - 1) RoundedCornerShape(bottomStart = 35.dp, bottomEnd = 35.dp)
                    else RoundedCornerShape(0.dp)
                    val textPadding = if (i == 0) PaddingValues(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp) else PaddingValues(vertical = 8.dp, horizontal = 16.dp)
                    Card(shape = shape, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary)) {
                        Text(text = conversation.content,
                            modifier = Modifier
                                .padding(textPadding)
                                .combinedClickable(
                                    onLongClickLabel = "Open Url",
                                    onLongClick = {
                                        // TODO: Text to speech disabled
//                                        textToSpeech.converse(state.resultList, i)
                                    }) {
                                    conversation.link?.let {
                                        uriHandler.openUri(it)
                                    }
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
            MessageScreen(text = state.message, contentColor = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
        }
    }
}