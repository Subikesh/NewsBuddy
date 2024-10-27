package com.spacey.newsbuddy.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
        ListUiState("What's up today!", homeUiState.chatHistory)
        Text(
            "Recent Summaries",
            Modifier.padding(vertical = 16.dp),
            style = MaterialTheme.typography.headlineLarge
        )
        ListUiState("Today's Summary", homeUiState.summaryHistory)
    }
}

@Composable
fun ListUiState(todayMsg: String, uiState: ListedUiState<String>) {
    when (uiState) {
        is ListedUiState.Loading -> {
            Column(
                Modifier.height(50.dp).fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        }

        is ListedUiState.Success -> {
            LazyRow {
                if (!isTodaysDate(uiState.resultList[0])) {
                    item {
                        HomeCard(modifier = Modifier.padding(horizontal = 8.dp)) {
                            Text(todayMsg, Modifier.padding(16.dp))
                        }
                    }
                }
                itemsIndexed(uiState.resultList) { i, chat ->
                    HomeCard(modifier = Modifier.padding(horizontal = 8.dp)) {
                        val chat = if (i == 0 && isTodaysDate(chat)) {
                            todayMsg
                        } else chat
                        Text(chat, Modifier.padding(16.dp))
                    }
                }
            }
        }

        is ListedUiState.Error -> {
            HomeCard(Modifier.fillMaxWidth()) {
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

@Composable
fun HomeCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Card(
        modifier.height(100.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiary.copy(0.7f),
            contentColor = MaterialTheme.colorScheme.onTertiary
        )
    ) {
        content()
    }
}

private fun isTodaysDate(dateStr: String): Boolean {
    return dateStr == LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
}