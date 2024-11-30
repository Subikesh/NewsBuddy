package com.spacey.newsbuddy.ui

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
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
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import com.spacey.newsbuddy.MainActivity
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

    if (isFirstTime || shouldShowRequestPermissionRationale(context as Activity, POST_NOTIFICATIONS)) { // TODO: returns true
        when (isAlertPositive) {
            PermissionState.DISABLED -> {
                AlertDialog(
                    title = { Text("Permission Request 🙏🏻") },
                    text = { Text("We sync daily news and keep things ready before you open the app. Kindly allow notification permission in the upcoming alert. You can always modify this in the settings.") },
                    onDismissRequest = onPermissionDeclined,
                    confirmButton = {
                        Button(onClick = {
                            isAlertPositive = PermissionState.ENABLED
                        }) { Text("Sure") }
                    }, dismissButton = {
                        if (isFirstTime) {
                            Button(onClick = {
                                onPermissionDeclined()
                                isAlertPositive = PermissionState.DENIED
                            }) { Text(text = "Maybe later") }
                        }
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
    AlertDialog(title = { Text(text = "We Can't Notify you 😞") }, text = {
        Text(text = "The notification permission is denied. Kindly open the app settings and enable notification permission${if (action != null) " to enable $action" else ""}.")
    }, onDismissRequest = onDismiss, dismissButton = {
        Button(onClick = onDismiss) {
            Text(text = "Later")
        }
    }, confirmButton = {
        Button(onClick = {
            settingsActivity.launch(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                val uri = Uri.fromParts("package", context.packageName, null)
                setData(uri)
            })
        }) {
            Text(text = "Get me there!")
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