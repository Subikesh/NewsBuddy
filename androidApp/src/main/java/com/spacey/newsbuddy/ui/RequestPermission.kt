package com.spacey.newsbuddy.ui

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

@Composable
fun RequestNotificationPermission(onPermissionGranted: () -> Unit, onPermissionDenied: () -> Unit) {
    val context = LocalContext.current
    if (context.isNotificationAllowed()) return

    var isAlertPositive by remember { mutableStateOf(false) }
    if (!isAlertPositive) {
        AlertDialog(
            title = { Text("Permission Request") },
            text = { Text("We sync daily news and keep things ready before you open the app. Kindly allow notification permission in the upcoming alert. You can always modify this in the settings.") },
            onDismissRequest = onPermissionDenied,
            confirmButton = {
                Button(onClick = {
                    isAlertPositive = true
                }) { Text("Sure") }
            }, dismissButton = {
                // TODO: dismiss and don't show notification permission
            }, shape = RoundedCornerShape(20.dp)
        )
    }
    if (isAlertPositive) {
        RequestPermission(POST_NOTIFICATIONS, onPermissionGranted, onPermissionDenied)
    }
}

@Composable
fun RequestPermission(
    permission: String,
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit
) {

    if (ContextCompat.checkSelfPermission(LocalContext.current, permission) != PackageManager.PERMISSION_GRANTED) {
        val permissionLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) onPermissionGranted()
            else onPermissionDenied()
        }

        SideEffect {
            permissionLauncher.launch(permission)
        }
    }
}