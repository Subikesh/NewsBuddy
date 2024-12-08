package com.spacey.newsbuddy.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spacey.newsbuddy.ListedUiState
import com.spacey.newsbuddy.android.BuildConfig
import com.spacey.newsbuddy.home.HomeViewModel
import com.spacey.newsbuddy.ui.BackIconButton
import com.spacey.newsbuddy.ui.CenteredTopBarScaffold
import com.spacey.newsbuddy.ui.ContentCard
import com.spacey.newsbuddy.ui.LoadingScreen
import com.spacey.newsbuddy.ui.MessageScreen

@Composable
fun ChatListScreen(navigateUp: () -> Unit, homeViewModel: HomeViewModel = viewModel(), navigateToChat: (String) -> Unit) {
    val uiState by homeViewModel.uiState.collectAsState()

    CenteredTopBarScaffold(title = "Recent Chats", navigationIcon = { BackIconButton {
        navigateUp()
    } }) {
        when (val chatHistory = uiState.chatHistory) {
            is ListedUiState.Error -> {
                MessageScreen(text = "Error in fetching recent chat history! ${if (BuildConfig.DEBUG) chatHistory.message else ""}")
            }
            is ListedUiState.Loading -> {
                LoadingScreen("Collecting our recent conversations...")
            }
            is ListedUiState.Success -> {
                ContentCard {
                    LazyColumn {
                        items(chatHistory.resultList) { chat ->
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.clickable {
                                navigateToChat(chat.date)
                            }.fillMaxWidth()) {
                                Text(text = chat.title, Modifier.padding(16.dp))
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                    ), modifier = Modifier.fillMaxHeight()
                                ) {
                                    Text(text = chat.date, Modifier.padding(16.dp))
                                }
                            }

                            Divider()
                        }
                    }
                }
            }
        }
    }
}