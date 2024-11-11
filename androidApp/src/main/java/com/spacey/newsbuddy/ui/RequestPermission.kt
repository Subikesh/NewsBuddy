package com.spacey.newsbuddy.ui

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

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