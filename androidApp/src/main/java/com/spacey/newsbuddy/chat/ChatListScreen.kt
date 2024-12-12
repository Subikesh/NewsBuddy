package com.spacey.newsbuddy.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spacey.newsbuddy.ListedUiState
import com.spacey.newsbuddy.android.BuildConfig
import com.spacey.newsbuddy.home.HomeViewModel
import com.spacey.newsbuddy.ui.CenteredTopBarScaffold
import com.spacey.newsbuddy.ui.ContentCard
import com.spacey.newsbuddy.ui.LoadingScreen
import com.spacey.newsbuddy.ui.MessageScreen
import com.spacey.newsbuddy.ui.RoundIconButton

@Composable
fun ChatListScreen(homeViewModel: HomeViewModel = viewModel(), navigateToSettings: () -> Unit, navigateToChat: (String?) -> Unit) {
    val uiState by homeViewModel.uiState.collectAsState()

    LaunchedEffect(key1 = true) {
        homeViewModel.loadHome()
    }

    CenteredTopBarScaffold(title = "Recent Chats", navigationIcon = {}, trailingIcon = {
        RoundIconButton(icon = Icons.Default.Add, contentDescription = "Start chat", onClick = { navigateToChat(null) })
    }) {
        when (val chatHistory = uiState.chatHistory) {
            is ListedUiState.Error -> {
                if (chatHistory.message == HomeViewModel.NO_CHAT_ERROR) {
                    MessageScreen(text = "No recent chats found.")
                } else {
                    MessageScreen(text = "Error in fetching recent chat history! ${if (BuildConfig.DEBUG) chatHistory.message else ""}")
                }
            }
            is ListedUiState.Loading -> {
                LoadingScreen("Collecting our recent conversations...")
            }
            is ListedUiState.Success -> {
                ContentCard(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)) {
                    LazyColumn {
                        itemsIndexed(chatHistory.resultList) { i, chat ->
                            Column {
                                if (i > 0) {
                                    Divider()
                                }
                                Row(modifier = Modifier
                                    .clickable {
                                        navigateToChat(chat.date)
                                    }
                                    .fillMaxWidth()) {
                                    Text(text = chat.title,
                                        maxLines = 3,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(16.dp))
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                        ),
                                        shape = RoundedCornerShape(topStart = 20.dp),
                                        modifier = Modifier.fillMaxHeight().align(Alignment.Bottom)
                                    ) {
                                        Text(text = chat.date, Modifier.padding(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}