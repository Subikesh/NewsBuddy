package com.spacey.newsbuddy

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.spacey.newsbuddy.home.HomeScreen
import com.spacey.newsbuddy.settings.SettingsAccessor
import com.spacey.newsbuddy.summary.SummaryScreen
import com.spacey.newsbuddy.ui.getLatestDate
import com.spacey.newsbuddy.ui.navigateFromHome
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

@Composable
fun MainScaffold(navigateToChat: (String?) -> Unit, navigateToSettings: () -> Unit) {
    val navController = rememberNavController()
    var appBarContent: AppBarContent? by remember {
        mutableStateOf(null)
    }
    var fabIcon by remember {
        mutableStateOf(Icons.Filled.Chat)
    }
    var fabConfig: FabConfig? by remember {
        mutableStateOf(FabConfig(onClick = { navigateToChat(null) }))
    }
    var bottomSelectedIndex by remember {
        mutableIntStateOf(0)
    }
    val bottomBarItems: List<BottomNavItem> = remember {
        getBottomBarItems(navController, navigateToChat)
    }

    navController.addOnDestinationChangedListener { _, destination, _ ->
        bottomBarItems.indexOfFirst { destination.route?.contains(it.navClass.qualifiedName!!) == true }.let {
            bottomSelectedIndex = it
        }
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
                bottomBarItems.forEachIndexed { i, bottomBarItem ->
                    NavigationBarItem(selected = bottomSelectedIndex == i, onClick = {
                        if (bottomSelectedIndex != i) {
                            bottomBarItem.navOnClick()
                            bottomSelectedIndex = i
                        } else {
                            bottomBarItem.onClickWhenSelected()
                        }
                    }, colors = navBarColors, icon = {
                        Icon(imageVector = bottomBarItem.icon, contentDescription = bottomBarItem.contentDescription)
                    })
                }
            }
        }, floatingActionButton = {
            /*val fab = fabConfig
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
            }*/
        }
    ) { padding ->
        NavHost(navController = navController, startDestination = NewsHome, modifier = Modifier
            .padding(padding)
            .background(Color.Transparent)) {
            composable<NewsHome> {
                val todayDate = getLatestDate()
                HomeScreen(
                    setAppBarContent = { appBarContent = it },
                    setFabConfig = { fabConfig = it },
                    navigateToChat = navigateToChat,
                    navigateToSummary = { navController.navigateFromHome(Summary(it ?: todayDate)) },
                    navigateToSettings = navigateToSettings
                )
            }

            composable<Summary> {
                SummaryScreen(it.toRoute<Summary>().date)
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

data class BottomNavItem(val navClass: KClass<*>, val icon: ImageVector, val contentDescription: String, val navOnClick: () -> Unit, val onClickWhenSelected: () -> Unit)

private fun getBottomBarItems(navController: NavController, navigateToChat: (String?) -> Unit): List<BottomNavItem> {
    val defaultList = mutableListOf(
        BottomNavItem(NewsHome::class, Icons.Default.Home, "News home", navOnClick = {
            navController.navigateFromHome(NewsHome, true)
        }) {
            // TODO: Refresh page or something
        },
        BottomNavItem(Chat::class, Icons.Default.Chat, "Chat", navOnClick = {
            navigateToChat(null)
        }) {}
    )
    if (SettingsAccessor.summaryFeatureEnabled) {
        defaultList.add(1, BottomNavItem(Summary::class, Icons.Default.Newspaper, "Summary", navOnClick = {
            navController.navigateFromHome(Summary(getLatestDate()))
        }) { })
    }
    return defaultList
}

data class AppBarContent(val content: (@Composable () -> Unit)? = null)
data class FabConfig(val showFab: Boolean = true, val onClick: () -> Unit)

@Serializable
data class Summary(val date: String)

@Serializable
data object NewsHome

@Serializable
data object User