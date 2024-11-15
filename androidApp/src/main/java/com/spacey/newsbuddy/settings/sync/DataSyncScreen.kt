package com.spacey.newsbuddy.settings.sync

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
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
import com.spacey.newsbuddy.ui.ContentCard
import com.spacey.newsbuddy.workers.NewsSyncWorker

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
        Button(onClick = {
            WorkManager.getInstance(context).enqueue(OneTimeWorkRequestBuilder<NewsSyncWorker>().run {
                setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                build()
            })
        }) {
            Text("Sync Now")
        }

        ContentCard(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)) {
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
                    LazyColumn {
                        itemsIndexed(dataSyncList.resultList) { i, syncDetails ->
                            Column(Modifier.padding(16.dp)) {
                                Text("Sync time: ${syncDetails.syncTime}")
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

                is ListedUiState.Error -> {
                    Text(dataSyncList.message, Modifier.padding(16.dp))
                }

            }
        }
    }
}