package com.spacey.newsbuddy.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spacey.newsbuddy.ui.BackIconButton
import com.spacey.newsbuddy.ui.CenteredTopBarScaffold
import com.spacey.newsbuddy.ui.ContentCard
import com.spacey.newsbuddy.ui.RequestNotificationPermission
import com.spacey.newsbuddy.ui.ShowNotificationDeniedAlert
import java.text.SimpleDateFormat
import java.util.Calendar
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
            SettingsCheckBox(label = "Enable Summaries", subTitle = "This is an experimental feature.", checked = settingsUiState.summaryFeatureEnabled) {
                if (settingsUiState.summaryFeatureEnabled) {
                    viewModel.disableSummary()
                } else {
                    viewModel.enableSummary()
                }
            }
            Divider()

            DataSyncCheckBox(
                syncState = settingsUiState.syncState,
                viewModel = viewModel,
                settingsUiState = settingsUiState,
                navigateDataSyncScreen = navigateDataSyncScreen
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ColumnScope.DataSyncCheckBox(
    syncState: PermissionState,
    viewModel: SettingsViewModel,
    settingsUiState: SettingsUiState,
    navigateDataSyncScreen: () -> Unit
) {
    val context = LocalContext.current

    var requestNotificationPermission by remember { mutableStateOf(false) }
    var showNotificationDenied by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    val isEnabled = syncState == PermissionState.ENABLED

    SettingsCheckBox(label = "Daily News Sync", subTitle = "No more boring loading screens. Let me read the news before you enter the app.", checked = syncState == PermissionState.ENABLED) {
        when (syncState) {
            PermissionState.ENABLED -> viewModel.disableSync(context)
            PermissionState.DISABLED -> requestNotificationPermission = true
            PermissionState.DENIED -> showNotificationDenied = true
        }
    }
    val timePickerState: TimePickerState = rememberTimePickerState(initialHour = settingsUiState.syncHour, initialMinute = settingsUiState.syncMinute, is24Hour = false)
    val calendarTime = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, timePickerState.hour)
        set(Calendar.MINUTE, timePickerState.minute)
    }
    val timeFormat = SimpleDateFormat("h:mm a", Locale.ROOT)
    AnimatedVisibility(isEnabled) {
        Column {
            Row(
                Modifier
                    .setBgColor(isEnabled)
                    .clickable { showTimePicker = true }
                    .fillMaxWidth()
                    .padding(16.dp)
                    , horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Choose Sync Time")
                Text(text = timeFormat.format(calendarTime.time))
            }

            Divider()

            Text("Latest Data Syncs",
                Modifier
                    .fillMaxWidth()
                    .clickable(onClick = navigateDataSyncScreen)
                    .padding(16.dp))
        }
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
    if (showTimePicker) {
        AlertDialog(onDismissRequest = {
            showTimePicker = false
        }, confirmButton = {
            TextButton(onClick = {
                viewModel.enableDataSync(context, timePickerState.hour, timePickerState.minute)
                showTimePicker = false
            }) {
                Text(text = "OK")
            }
        }, text = {
            TimePicker(
                state = timePickerState,
                colors = TimePickerDefaults.colors(
                    clockDialColor = MaterialTheme.colorScheme.tertiary,
                    selectorColor = MaterialTheme.colorScheme.primaryContainer,
                    timeSelectorUnselectedContainerColor = MaterialTheme.colorScheme.tertiary,
                    timeSelectorUnselectedContentColor = MaterialTheme.colorScheme.onTertiary,
                    periodSelectorSelectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    periodSelectorSelectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        })
    }
}

@Composable
private fun SettingsCheckBox(
    label: String,
    checked: Boolean,
    modifier: Modifier = Modifier,
    subTitle: String? = null,
    onClick: () -> Unit
) {
    Column {
        Row(modifier = modifier
            .setBgColor(checked)
            .fillMaxWidth()
            .clickable(onClick = onClick), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(
                Modifier
                    .weight(1f)
                    .padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = label, style = MaterialTheme.typography.bodyLarge)
                if (subTitle != null) {
                    Text(text = subTitle, style = MaterialTheme.typography.bodySmall)
                }
            }
            Switch(checked = checked, onCheckedChange = null, Modifier.padding(end = 16.dp))
        }
    }
}

@Composable
private fun Modifier.setBgColor(checked: Boolean): Modifier =
    if (checked) this.background(MaterialTheme.colorScheme.secondaryContainer) else this