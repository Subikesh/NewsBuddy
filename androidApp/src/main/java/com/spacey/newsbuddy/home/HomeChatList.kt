package com.spacey.newsbuddy.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.spacey.newsbuddy.ListedUiState

@Composable
fun HomeChatList(viewModel: HomeViewModel) {
    LaunchedEffect(true) {
        viewModel.loadHome()
    }

    val homeUiState by viewModel.uiState.collectAsState()

    Column {
        Text(
            "Recent Chats",
            Modifier.padding(vertical = 16.dp),
            style = MaterialTheme.typography.headlineLarge
        )
        ListUiState(homeUiState.chatHistory)
        Text(
            "Recent Summaries",
            Modifier.padding(vertical = 16.dp),
            style = MaterialTheme.typography.headlineLarge
        )
        ListUiState(homeUiState.summaryHistory)
    }
}

@Composable
fun ListUiState(uiState: ListedUiState<String>) {
    when (uiState) {
        is ListedUiState.Loading -> {
            Column(
                Modifier
                    .height(50.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        }

        is ListedUiState.Success -> {
            LazyRow(contentPadding = PaddingValues(16.dp)) {
                items(uiState.resultList) { chat ->
                    Card(Modifier.height(100.dp)) {
                        Text(chat, Modifier.padding(16.dp))
                    }
                }
            }
        }

        is ListedUiState.Error -> {
            Card(
                Modifier
                    .height(100.dp)
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiary.copy(0.7f),
                    contentColor = MaterialTheme.colorScheme.onTertiary
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = uiState.message, textAlign = TextAlign.Center, modifier = Modifier)
                }
            }
        }
    }
}