package com.spacey.newsbuddy.buddy

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spacey.newsbuddy.ui.CenteredColumn

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuddyScreen(viewModel: BuddyViewModel = viewModel(), navigateToHome: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(key1 = true) {
        viewModel.promptTodaysNews()
    }
    val uriHandler = LocalUriHandler.current
    Scaffold(topBar = {
        TopAppBar(title = {
            Text("Let's talk today's news")
        })
    }) { padding ->
        when(val state = uiState) {
            is BuddyScreenState.Loading -> {
                CenteredColumn(Modifier.padding(padding)) {
                    CircularProgressIndicator()
                    Text(state.message)
                }
            }
            is BuddyScreenState.Success -> {
                LazyColumn(Modifier.padding(padding)) {
                    items(state.conversations) { conversation ->
                        Card(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            onClick = {
                                conversation.link?.let {
                                    uriHandler.openUri(it)
                                }
                            }) {
                            Text(text = conversation.content, modifier = Modifier.padding(8.dp))
                        }
                    }
                }
            }
            is BuddyScreenState.Error -> {
                CenteredColumn(Modifier.padding(padding)) {
                    Text(state.message, style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}