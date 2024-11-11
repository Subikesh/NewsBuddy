package com.spacey.newsbuddy.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.RenderEffect
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun CenteredColumn(
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    verticalArrangement: Arrangement.Vertical = Arrangement.Center,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier.fillMaxSize(), verticalArrangement, horizontalAlignment, content)
}

fun getGlassEffect(): RenderEffect {
    return BlurEffect(25f, 25f)
}

fun getLatestDate(): String {
    return LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE)
}

// TODO: Add notification permission kotlin contract
fun Context.isNotificationAllowed() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
        applicationContext.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED