package com.spacey.newsbuddy

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.spacey.newsbuddy.chat.ChatScreen
import com.spacey.newsbuddy.home.HomeScreen
import com.spacey.newsbuddy.summary.SummaryScreen
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun MainScaffold() {
    var bottomSelectedIndex by remember {
        mutableIntStateOf(0)
    }
    val navController = rememberNavController()
    val backstack by navController.currentBackStackEntryAsState()
    val bottomNavList = listOf(NewsHome::class, Summary::class, Chat::class)
    bottomSelectedIndex = bottomNavList.indexOfFirst { backstack?.destination?.route == it.qualifiedName }.takeIf { it != -1 } ?: 0
    var appBarContent: AppBarContent? by remember {
        mutableStateOf(null)
    }
    var fabIcon by remember {
        mutableStateOf(Icons.Filled.Chat)
    }
    var fabConfig: FabConfig? by remember {
        mutableStateOf(FabConfig(onClick = {}))
    }
    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            val navBarColors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.primary.copy(alpha = .4f),
                indicatorColor = MaterialTheme.colorScheme.surfaceColorAtElevation(LocalAbsoluteTonalElevation.current)
            )
            NavigationBar(containerColor = MaterialTheme.colorScheme.secondary) {
                NavigationBarItem(selected = bottomSelectedIndex == 0, onClick = {
                    if (bottomSelectedIndex != 0) {
                        navController.navigate(NewsHome)
                        bottomSelectedIndex = 0
                    } else {
                        // TODO: Refresh page or something
                    }
                }, colors = navBarColors, icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "News home") })


                NavigationBarItem(selected = bottomSelectedIndex == 1, onClick = {
                    if (bottomSelectedIndex != 1) {
                        navController.navigate(Summary)
                        bottomSelectedIndex = 1
                    } else {
                        // TODO: Refresh page or something
                    }
                }, colors = navBarColors, icon = { Icon(imageVector = Icons.Default.Newspaper, contentDescription = "Summary") })

                NavigationBarItem(selected = bottomSelectedIndex == 2, onClick = {
                    if (bottomSelectedIndex != 2) {
                        navController.navigate(Chat)
                        bottomSelectedIndex = 2
                    }
                }, colors = navBarColors, icon = { Icon(imageVector = Icons.Default.Chat, contentDescription = "Feed") })
            }
        }, floatingActionButton = {
            val fab = fabConfig
            if (fab != null) {
                AnimatedVisibility(visible = fab.showFab) {
                    LargeFloatingActionButton(
                        shape = CircleShape,
                        onClick = fab.onClick,
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ) {
                        AnimatedContent(targetState = fabIcon, label = "Pause/Play") {
                            Icon(it, contentDescription = "Pause/Play news")
                        }
                    }
                }
            }
        }
    ) { padding ->
        NavHost(navController = navController, startDestination = NewsHome, modifier = Modifier
            .padding(padding)
            .background(Color.Transparent)) {
            composable<NewsHome> {
                val todayDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
                HomeScreen(
                    setAppBarContent = { appBarContent = it },
                    setFabConfig = { fabConfig = it },
                    navigateToChat = { navController.navigate(Chat(it ?: todayDate)) },
                    navigateToSummary = { navController.navigate(Summary(it ?: todayDate)) }
                )
            }

            composable<Summary> {
                SummaryScreen(it.toRoute<Summary>().date)
            }

            composable<Chat> {
                val route: Chat = it.toRoute()
                ChatScreen(
                    route.date,
                    setFabConfig = {
                        fabConfig = it
                    }, setAppBarContent = {
                        appBarContent = it
                    }, navBackToHome = {
                        navController.navigateUp()
                    }
                )
            }
            composable<User> {
                EmptyScreen()
                appBarContent = AppBarContent {
                    Text(text = "News Buddy")
                }
            }
        }
    }
}

data class AppBarContent(val content: (@Composable () -> Unit)? = null)
data class FabConfig(val showFab: Boolean = true, val onClick: () -> Unit)

@Serializable
data class Summary(val date: String)

@Serializable
data object NewsHome

@Serializable
data class Chat(val date: String)

@Serializable
data object User