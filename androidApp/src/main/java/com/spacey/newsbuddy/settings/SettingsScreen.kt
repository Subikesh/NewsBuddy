package com.spacey.newsbuddy.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.HorizontalRuler
import androidx.compose.ui.unit.dp
import com.spacey.newsbuddy.ui.BackIconButton
import com.spacey.newsbuddy.ui.CenteredTopBarScaffold
import com.spacey.newsbuddy.ui.ContentCard

@Composable
fun SettingsScreen(navigateDataSyncScreen: () -> Unit, navigateBack: () -> Unit) {
    CenteredTopBarScaffold("Settings", navigationIcon = {
        BackIconButton(navigateBack)
    }) {
        val textModifier = Modifier.fillMaxWidth().clickable(onClick = navigateDataSyncScreen).padding(16.dp)
        ContentCard(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            Text("Latest Data Syncs", textModifier)
            Divider()
            Text("Other Setting", Modifier.padding(16.dp))
            HorizontalRuler()
        }
    }
}