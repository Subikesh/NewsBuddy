package com.spacey.newsbuddy.buddy

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuddyScreen(viewModel: BuddyViewModel = viewModel(), navigateToHome: () -> Unit) {
    val conversations by viewModel.conversationList.collectAsState()
    LaunchedEffect(key1 = true) {
        viewModel.promptTodaysNews()
    }
    val uriHandler = LocalUriHandler.current
    if (conversations.isEmpty()) {
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            CircularProgressIndicator()
        }
    }
    LazyColumn {
        items(conversations) { conversation ->
            Card(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), onClick = {
                conversation.link?.let {
                    uriHandler.openUri(it)
                }
            }) {
                Text(text = conversation.convo, modifier = Modifier.padding(8.dp))
            }
        }
    }
}