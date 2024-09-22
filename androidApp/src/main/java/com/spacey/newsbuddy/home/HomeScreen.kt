package com.spacey.newsbuddy.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
fun HomeScreen(viewModel: BuddyViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(key1 = true) {
        viewModel.promptTodaysNews()
    }
    val uriHandler = LocalUriHandler.current
    when(val state = uiState) {
        is BuddyScreenState.Loading -> {
            CenteredColumn {
                CircularProgressIndicator()
                Text(state.message)
            }
        }
        is BuddyScreenState.Success -> {
            LazyColumn(contentPadding = PaddingValues(16.dp)) {
                items(items = state.conversations) { conversation ->
                    Text(text = conversation.content, modifier = Modifier.padding(bottom = 8.dp), style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
        is BuddyScreenState.Error -> {
            CenteredColumn {
                Text(state.message, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}