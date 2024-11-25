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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.HorizontalRuler
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.spacey.newsbuddy.ui.BackIconButton
import com.spacey.newsbuddy.ui.CenteredTopBarScaffold
import com.spacey.newsbuddy.ui.ContentCard
import com.spacey.newsbuddy.ui.RequestNotificationPermission
import com.spacey.newsbuddy.ui.isNotificationAllowed

@Composable
fun SettingsScreen(navigateDataSyncScreen: () -> Unit, navigateBack: () -> Unit) {
    CenteredTopBarScaffold("Settings", navigationIcon = {
        BackIconButton(navigateBack)
    }) {
        ContentCard(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            // TODO: Include viewmodel and store preferences
            var syncEnabled: Boolean by remember { mutableStateOf(false) }
            val syncText = "Daily Sync: ${if (syncEnabled) "Enabled" else "Disabled"}"
            val context = LocalContext.current
            if (syncEnabled && !context.isNotificationAllowed()) {
                RequestNotificationPermission(onPermissionGranted = {}) { }
            }
            SettingsCheckBox(syncText, syncEnabled) {
                syncEnabled = !syncEnabled
            }
            Divider()
            Text("Latest Data Syncs", Modifier.fillMaxWidth().clickable(onClick = navigateDataSyncScreen).padding(16.dp))
            Divider()
            Text("Other Setting", Modifier.padding(16.dp))
            HorizontalRuler()
        }
    }
}

@Composable
private fun SettingsCheckBox(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val colorModifier = if (selected) {
        modifier.background(MaterialTheme.colorScheme.secondaryContainer)
    } else modifier
    Row(modifier = colorModifier.fillMaxWidth().clickable(onClick = onClick), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(text, Modifier.padding(16.dp))
    }
}