package com.spacey.newsbuddy.settings.sync

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
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
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.spacey.newsbuddy.ListedUiState
import com.spacey.newsbuddy.ui.BackIconButton
import com.spacey.newsbuddy.ui.CenteredTopBarScaffold
import com.spacey.newsbuddy.ui.MessageScreen
import com.spacey.newsbuddy.workers.NewsSyncWorker
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@Composable
fun DataSyncScreen(navigateBack: () -> Unit, viewModel: DataSyncViewModel = viewModel()) {
    LaunchedEffect(true) {
        viewModel.fetchLatestDataSyncs()
    }
    val syncList by viewModel.uiState.collectAsState()
    CenteredTopBarScaffold("Data Sync History", navigationIcon = {
        BackIconButton(navigateBack)
    }) {
        val context = LocalContext.current
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            when (val dataSyncList = syncList) {
                is ListedUiState.Loading -> {
                    Column(
                        Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is ListedUiState.Success -> {
                    if (dataSyncList.resultList.isEmpty()) {
                        MessageScreen(text = "No data syncs done yet!")
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

            Button(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), onClick = {
                WorkManager.getInstance(context).enqueue(OneTimeWorkRequestBuilder<NewsSyncWorker>().run {
                    setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                    build()
                })
            }) {
                Text("Sync Now")
            }
        }

    }
}