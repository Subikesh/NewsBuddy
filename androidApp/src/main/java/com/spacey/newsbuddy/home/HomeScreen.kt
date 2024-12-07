package com.spacey.newsbuddy.home

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spacey.newsbuddy.AppBarContent
import com.spacey.newsbuddy.FabConfig
import com.spacey.newsbuddy.genai.SummaryParagraph

@Composable
fun HomeScreen(
    padding: PaddingValues,
    setAppBarContent: (AppBarContent?) -> Unit,
    setFabConfig: (FabConfig) -> Unit,
    navigateToChat: (String?) -> Unit,
    navigateToSummary: (String?) -> Unit,
    navigateToSettings: () -> Unit
) {
    LaunchedEffect(key1 = true) {
        setAppBarContent(null)
        setFabConfig(FabConfig { navigateToChat(null) })
    }
    // TODO: Ask permission after second launch
//    RequestNotificationPermission(
//        isFirstTime = true,
//        onPermissionGranted = {
//            SettingsAccessor.dataSyncEnabled = PermissionState.ENABLED
//            scheduleDataSync(context)
//        },
//        onPermissionDeclined = {},
//        onPermanentlyDenied = {
//        }
//    )
    Column(Modifier.padding(padding).verticalScroll(rememberScrollState())) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            IconButton(
                onClick = {
                    navigateToSettings()
                }, colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "Settings"
                )
            }
        }

        Button(shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = .7f),
                contentColor = MaterialTheme.colorScheme.onTertiary
            ),
            modifier = Modifier
                .size(250.dp)
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally),
            onClick = {
                navigateToChat(null)
            }) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Hey there 👋", modifier = Modifier.padding(bottom = 8.dp))
                Text(text = "Tap to Chat", style = MaterialTheme.typography.headlineLarge)
            }
        }

        HomeChatList(viewModel(), navigateToChat, navigateToSummary)
//        SummaryScreen()
    }
}

fun TextToSpeech.converse(summaries: List<SummaryParagraph>, index: Int) {
    stop()
    for (i in index until summaries.size) {
        speak(summaries[i].escapedContent, TextToSpeech.QUEUE_ADD, null, i.toString())
    }
}