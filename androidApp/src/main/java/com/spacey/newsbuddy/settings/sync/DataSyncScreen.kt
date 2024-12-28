package com.spacey.newsbuddy.settings.sync

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spacey.newsbuddy.ListedUiState
import com.spacey.newsbuddy.ui.BackIconButton
import com.spacey.newsbuddy.ui.CenteredTopBarScaffold
import com.spacey.newsbuddy.ui.LoadingScreen
import com.spacey.newsbuddy.ui.MessageScreen
import com.spacey.newsbuddy.ui.RoundIconButton
import com.spacey.newsbuddy.ui.isInternetConnected
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@Composable
fun DataSyncScreen(navigateBack: () -> Unit, viewModel: DataSyncViewModel = viewModel()) {
    LaunchedEffect(true) {
        viewModel.fetchLatestDataSyncs()
    }
    val syncList by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    CenteredTopBarScaffold("Data Sync History", navigationIcon = {
        BackIconButton(navigateBack)
    }, trailingIcon = {
        if (syncList !is ListedUiState.Loading) {
            RoundIconButton(icon = Icons.Default.Sync, iconColors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary), contentDescription = "Sync now") {
                if (context.isInternetConnected()) {
                    viewModel.syncNow(context)
                }
            }
        }
    }) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            when (val dataSyncList = syncList) {
                is ListedUiState.Loading -> {
                    LoadingScreen("Syncing latest news, and summarizing it...")
                }

                is ListedUiState.Success -> {
                    if (dataSyncList.resultList.isEmpty()) {
                        MessageScreen(text = "No data syncs done yet!", actionButton = {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                ),
                                modifier = Modifier.fillMaxWidth().clickable {
                                    viewModel.syncNow(context)
                                }
                            ) {
                                Text("Sync Now!", Modifier.align(Alignment.CenterHorizontally).padding(16.dp))
                            }
                        })
                    } else {
                        LazyColumn {
                            itemsIndexed(dataSyncList.resultList) { i, syncDetails ->
                                Column(Modifier.padding(16.dp)) {
                                    val dateString = LocalDateTime.ofInstant(Instant.ofEpochMilli(syncDetails.syncTimeMillis), ZoneId.systemDefault())
                                    Text("Sync time: $dateString")
                                    Text("News Result: ${syncDetails.newsResult}")
                                    Text("Summary Result: ${syncDetails.summaryResult}")
                                    Text("Chat Result: ${syncDetails.chatResult}")
                                }
                                if (i < dataSyncList.resultList.size-1) {
                                    Divider()
                                }
                            }
                        }
                    }
                }

                is ListedUiState.Error -> {
                    MessageScreen(text = dataSyncList.message, contentColor = MaterialTheme.colorScheme.error)
                }

            }
        }

    }
}