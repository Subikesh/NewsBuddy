package com.spacey.newsbuddy.settings

import androidx.compose.runtime.Composable
import com.spacey.newsbuddy.ui.BackIconButton
import com.spacey.newsbuddy.ui.CenteredTopBarScaffold

@Composable
fun SettingsScreen(navigateBack: () -> Unit) {

    CenteredTopBarScaffold("Settings", navigationIcon = {
        BackIconButton(navigateBack)
    }) {

    }
}