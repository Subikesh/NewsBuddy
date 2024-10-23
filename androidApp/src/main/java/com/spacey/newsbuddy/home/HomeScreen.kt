package com.spacey.newsbuddy.home

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spacey.newsbuddy.AppBarContent
import com.spacey.newsbuddy.FabConfig
import com.spacey.newsbuddy.genai.Conversation
import com.spacey.newsbuddy.summary.SummaryScreen


@Composable
fun HomeScreen(
    setAppBarContent: (AppBarContent?) -> Unit,
    setFabConfig: (FabConfig) -> Unit,
    navigateToChat: () -> Unit,
    homeViewModel: HomeViewModel = viewModel()
) {
    LaunchedEffect(key1 = true) {
        setAppBarContent(null)
        setFabConfig(FabConfig {})
    }
    val uiState by homeViewModel.uiState.collectAsState()
    Column(Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            IconButton(
                onClick = { /*TODO*/ }, colors = IconButtonDefaults.iconButtonColors(
                    MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Icon(
                    Icons.Default.AccountCircle,
                    contentDescription = "Account",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            IconButton(colors = IconButtonDefaults.iconButtonColors(MaterialTheme.colorScheme.secondaryContainer),
                onClick = {
//                    viewModel.promptTodaysNews(true)
                }) {
                Icon(Icons.Default.Refresh, "Refresh", tint = MaterialTheme.colorScheme.onSurface)
            }
        }

        Button(shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = .7f),
                contentColor = MaterialTheme.colorScheme.onTertiary
            ),
            modifier = Modifier
                .size(250.dp)
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally),
            onClick = {
                navigateToChat()
            }) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Hi, User👋", modifier = Modifier.padding(bottom = 8.dp))
                Text(text = "Tap to Chat", style = MaterialTheme.typography.headlineLarge)
            }
        }
        Text(
            text = "Explore",
            modifier = Modifier.padding(vertical = 16.dp),
            style = MaterialTheme.typography.headlineLarge
        )
        LazyRow {
            when (val state = uiState) {
                HomeUiState.Loading -> item {
                    CircularProgressIndicator()
                }
                is HomeUiState.Success -> items(state.summaryList) {
                    Card() {

                    }
                }
                is HomeUiState.Failure -> TODO()
            }
        }
        SummaryScreen()
    }

//
//    val shape = RoundedCornerShape(topStart = 35.dp, topEnd = 35.dp)
//    val textPadding = if (i == 0) PaddingValues(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp) else PaddingValues(vertical = 8.dp, horizontal = 16.dp)
//    Card(shape = shape, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
//        Text(text = conversation.content,
//            modifier = Modifier
//                .padding(textPadding)
//                .combinedClickable(
//                    onLongClickLabel = "Open Url",
//                    onLongClick = {
//                        conversation.link?.let {
//                            uriHandler.openUri(it)
//                        }
//                    }) {
//                    textToSpeech.converse(state.conversations, i)
//                },
//            style = MaterialTheme.typography.bodyLarge.copy(
//                fontWeight = weight,
//                color = MaterialTheme.colorScheme.onSurface
//            )
//        )
//    }

}

fun TextToSpeech.converse(conversations: List<Conversation>, index: Int) {
    stop()
    for (i in index until conversations.size) {
        speak(conversations[i].escapedContent, TextToSpeech.QUEUE_ADD, null, i.toString())
    }
}