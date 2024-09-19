package com.spacey.newsbuddy.home

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Feed
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.spacey.newsbuddy.EmptyScreen
import kotlinx.serialization.Serializable

@Composable
fun HomeScreen(navigateToNews: () -> Unit) {
    var bottomSelectedIndex by remember {
        mutableIntStateOf(0)
    }
    val context = LocalContext.current
    val navController = rememberNavController()
    val backstack by navController.currentBackStackEntryAsState()
    bottomSelectedIndex = when (backstack?.destination?.route) {
        // TODO: will reflection change on proguard names
        NewsHome::class.qualifiedName -> 0
        Feed::class.qualifiedName -> 1
        User::class.qualifiedName -> 2
        else -> 0
    }
    Scaffold(bottomBar = {
        NavigationBar {
            NavigationBarItem(selected = bottomSelectedIndex == 0, onClick = {
                if (bottomSelectedIndex != 0) {
                    navController.navigate(NewsHome)
                    bottomSelectedIndex = 0
                } else {
                    // TODO: Refresh page or something
                }
            }, icon = { Icon(imageVector = Icons.Default.Newspaper, contentDescription = "News home") })

            NavigationBarItem(selected = bottomSelectedIndex == 1, onClick = {
                if (bottomSelectedIndex != 1) {
                    navController.navigate(Feed)
                    bottomSelectedIndex = 1
                }
            }, icon = { Icon(imageVector = Icons.Default.Feed, contentDescription = "Feed") })

            NavigationBarItem(selected = bottomSelectedIndex == 2, onClick = {
                if (bottomSelectedIndex != 2) {
                    navController.navigate(User)
                    bottomSelectedIndex = 2
                }
            }, icon = { Icon(imageVector = Icons.Default.AccountCircle, contentDescription = "User") })
        }
    }, floatingActionButton = {
        LargeFloatingActionButton(onClick = {
            navigateToNews()
        }) {
            Icon(Icons.Outlined.PlayArrow, contentDescription = "Play news")
        }
    }) { padding ->
        NavHost(navController = navController, startDestination = NewsHome) {
            composable<NewsHome> {
                Column(modifier = Modifier
                    .fillMaxSize()
                    .padding(padding), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Hello there!")
                }
            }

            composable<Feed> { EmptyScreen() }
            composable<User> { EmptyScreen() }
        }
    }
}

@Serializable
data object NewsHome

@Serializable
data object Feed

@Serializable
data object User