package com.spacey.newsbuddy

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.spacey.newsbuddy.ui.CenteredColumn

@Composable
fun EmptyScreen() {
    CenteredColumn {
        Text(text = "Screen coming soon!")
    }
}