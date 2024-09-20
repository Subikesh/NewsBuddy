package com.spacey.newsbuddy.buddy

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun BuddyScreen(viewModel: BuddyViewModel = viewModel(), navigateToHome: () -> Unit) {
    val responseText by viewModel.responseText.collectAsState()
    LaunchedEffect(key1 = true) {
        viewModel.promptTodaysNews()
    }
    Text(text = responseText)
}