package com.spacey.newsbuddy.home

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.spacey.newsbuddy.ListedUiState
import com.spacey.newsbuddy.ui.formatToHomeDateDisplay

@Composable
fun HomeChatList(viewModel: HomeViewModel, navToChat: (String?) -> Unit, navToSummary: (String?) -> Unit) {
    LaunchedEffect(true) {
        viewModel.loadHome()
    }

    val homeUiState by viewModel.uiState.collectAsState()

    Column {
        ListUiState("Recent Chats", homeUiState.chatHistory, navToChat)
        if (homeUiState.summarySupported) {
            ListUiState("Recent Summaries", homeUiState.summaryHistory, navToSummary)
        }
    }
}

@Composable
fun ListUiState(title: String, uiState: ListedUiState<HomeBubble>, navToChat: (String?) -> Unit) {
    Column(Modifier.height(275.dp)) {
        Text(
            title,
            Modifier.padding(16.dp),
            style = MaterialTheme.typography.headlineLarge
        )
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
                LazyRow(contentPadding = PaddingValues(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    itemsIndexed(uiState.resultList) { i, chat ->
                        HomeCard(onClick = {
                            navToChat(chat.date)
                        }, bottomContent = {
                            Text(text = chat.date.formatToHomeDateDisplay(), Modifier.padding(12.dp))
                        }) {
                            Text(chat.title, Modifier.padding(16.dp))
                        }
                    }
                }
            }

            is ListedUiState.Error -> {
                HomeCard(Modifier.fillMaxWidth().padding(16.dp), null, bottomContent = {}) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Log.e("HomeError", uiState.message)
                        val errorText = uiState.message
                        Text(text = errorText, textAlign = TextAlign.Center, modifier = Modifier)
                    }
                }
            }
        }
    }
}

@Composable
fun HomeCard(modifier: Modifier = Modifier, onClick: (() -> Unit)?, bottomContent: @Composable () -> Unit, content: @Composable () -> Unit) {
    val clickModifier: Modifier.() -> Modifier = {
        if (onClick != null) {
            clickable { onClick() }
        } else this
    }
    Card(
        modifier
            .widthIn(200.dp, 250.dp)
            .clip(RoundedCornerShape(20.dp))
            .fillMaxHeight()
            .clickModifier(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiary.copy(0.7f),
            contentColor = MaterialTheme.colorScheme.onTertiary
        )
    ) {
        Column {
            Box(Modifier.weight(1f)) {
                content()
            }
            Card(
                Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                bottomContent()
            }
        }
    }
}