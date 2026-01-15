package com.spacey.newsbuddy.ui

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.provider.Settings.EXTRA_APP_PACKAGE
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import com.spacey.newsbuddy.MainActivity
import com.spacey.newsbuddy.R
import com.spacey.newsbuddy.settings.PermissionState
import com.spacey.newsbuddy.settings.SettingsAccessor


@Composable
fun RequestNotificationPermission(onPermissionGranted: () -> Unit, onPermissionDeclined: () -> Unit, onPermanentlyDenied: () -> Unit) {
    val context = LocalContext.current
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || context.isNotificationAllowed()) {
        onPermissionGranted()
        return
    }
    val isFirstTime = SettingsAccessor.notificationCount == 0
    var isAlertPositive by remember { mutableStateOf(PermissionState.DISABLED) }

    if (isFirstTime || shouldShowRequestPermissionRationale(context as Activity, POST_NOTIFICATIONS)) {
        when (isAlertPositive) {
            PermissionState.DISABLED -> {
                AlertDialog(
                    title = { Text(stringResource(R.string.notification_permission_title)) },
                    text = { Text(stringResource(R.string.notification_permission_dialog)) },
                    onDismissRequest = onPermissionDeclined,
                    confirmButton = {
                        Button(onClick = {
                            isAlertPositive = PermissionState.ENABLED
                        }) { Text(stringResource(R.string.notification_permission_positive)) }
                    }, dismissButton = {
                        Button(
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            ), onClick = {
                            onPermissionDeclined()
                            isAlertPositive = PermissionState.DENIED
                        }) { Text(stringResource(R.string.notification_permission_negative)) }
                    }, shape = RoundedCornerShape(20.dp),
                )
            }
            PermissionState.ENABLED -> {
                SettingsAccessor.notificationCount += 1
                RequestPermission(POST_NOTIFICATIONS, onPermissionGranted, onPermissionDeclined, onPermanentlyDenied)
            }
            PermissionState.DENIED -> {
                onPermissionDeclined()
            }
        }
    } else {
        onPermanentlyDenied()
    }
}

@Composable
fun ShowNotificationDeniedAlert(action: String? = null, onPermissionGranted: () -> Unit, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val settingsActivity = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
        if (context.isNotificationAllowed()) {
            Toast.makeText(context, "We'll keep you updated when the sync happens 😃", Toast.LENGTH_SHORT).show()
            onPermissionGranted()
        }
    }
    AlertDialog(title = { Text(text = "We couldn't send notifications 😞") }, text = {
        Text(text = "The notification permission is disabled. Kindly open the app settings allow notification permission${if (action != null) " to enable $action" else ""}.")
    }, onDismissRequest = onDismiss, dismissButton = {
        Button(onClick = onDismiss) {
            Text(text = "Later")
        }
    }, confirmButton = {
        Button(onClick = {
            settingsActivity.launch(Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(EXTRA_APP_PACKAGE, context.packageName)
            })
        }) {
            Text(text = "Enable Notifications!")
        }
    }, shape = RoundedCornerShape(20.dp))
}

@Composable
fun RequestPermission(
    permission: String,
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit,
    onPermanentlyDeclined: () -> Unit
) {
    val context = LocalContext.current
    if (ContextCompat.checkSelfPermission(LocalContext.current, permission) != PackageManager.PERMISSION_GRANTED) {
        val permissionLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted)
                onPermissionGranted()
            else if (!shouldShowRequestPermissionRationale(context as MainActivity, permission))
                onPermanentlyDeclined()
            else
                onPermissionDenied()
        }

        SideEffect {
            permissionLauncher.launch(permission)
        }
    } else {
        onPermissionGranted()
    }
}