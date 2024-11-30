package com.spacey.newsbuddy.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.HorizontalRuler
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spacey.newsbuddy.ui.BackIconButton
import com.spacey.newsbuddy.ui.CenteredTopBarScaffold
import com.spacey.newsbuddy.ui.ContentCard
import com.spacey.newsbuddy.ui.RequestNotificationPermission
import com.spacey.newsbuddy.ui.ShowNotificationDeniedAlert
import com.spacey.newsbuddy.ui.capitalize
import java.util.Locale

@Composable
fun SettingsScreen(navigateDataSyncScreen: () -> Unit, navigateBack: () -> Unit, viewModel: SettingsViewModel = viewModel()) {
    CenteredTopBarScaffold("Settings", navigationIcon = {
        BackIconButton(navigateBack)
    }) {
        ContentCard(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)) {
            val settingsUiState by viewModel.syncState.collectAsState()
            SettingsCheckBox(syncState = settingsUiState.syncState, viewModel = viewModel)

            Divider()
            Text("Latest Data Syncs",
                Modifier
                    .fillMaxWidth()
                    .clickable(onClick = navigateDataSyncScreen)
                    .padding(16.dp))
            Divider()
            Text("Other Setting", Modifier.padding(16.dp))
            HorizontalRuler()
        }
    }
}

@Composable
private fun SettingsCheckBox(
    syncState: PermissionState,
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val syncText = syncState.name.lowercase(Locale.ROOT).capitalize()

    var requestNotificationPermission by remember { mutableStateOf(false) }
    var showNotificationDenied by remember { mutableStateOf(false) }

    val colorModifier = if (syncState == PermissionState.ENABLED) {
        modifier.background(MaterialTheme.colorScheme.secondaryContainer)
    } else modifier
    Row(modifier = colorModifier
        .fillMaxWidth()
        .clickable(onClick = {
            when (syncState) {
                PermissionState.ENABLED -> viewModel.disableSync(context)
                PermissionState.DISABLED -> requestNotificationPermission = true
                PermissionState.DENIED -> showNotificationDenied = true
            }
        }), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(text = "Data Sync $syncText", Modifier.padding(16.dp))
//        Text(text = syncText, Modifier.padding(16.dp))
    }
    if (requestNotificationPermission) {
        RequestNotificationPermission(
            onPermissionGranted = {
                viewModel.enableDataSync(context)
                requestNotificationPermission = false
            },
            onPermissionDeclined = {
                viewModel.disableSync(context)
                requestNotificationPermission = false
            },
            onPermanentlyDenied = {
                viewModel.denyPermission()
                requestNotificationPermission = false
            }
        )
    }
    if (showNotificationDenied) {
        ShowNotificationDeniedAlert(action = "data sync", onPermissionGranted = {
            viewModel.enableDataSync(context)
            showNotificationDenied = false
        }, onDismiss =  {
            showNotificationDenied = false
        })
    }
}