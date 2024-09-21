package com.spacey.newsbuddy

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.spacey.newsbuddy.ui.CenteredColumn

@Composable
fun EmptyScreen() {
    CenteredColumn {
        Text(text = "Screen coming soon!")
    }
}