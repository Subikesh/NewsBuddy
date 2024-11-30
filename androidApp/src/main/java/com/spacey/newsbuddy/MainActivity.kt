package com.spacey.newsbuddy

import android.os.Build
import android.os.Bundle
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.spacey.newsbuddy.android.R
import com.spacey.newsbuddy.chat.ChatScreen
import com.spacey.newsbuddy.settings.SettingsScreen
import com.spacey.newsbuddy.settings.sync.DataSyncScreen
import com.spacey.newsbuddy.ui.enterSlideTransition
import com.spacey.newsbuddy.ui.exitSlideTransition
import com.spacey.newsbuddy.ui.getLatestDate
import com.spacey.newsbuddy.ui.navigateFromHome
import kotlinx.serialization.Serializable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
        }
        setContent {
            // TODO: Adopt multiple themes
//            val isDarkTheme = isSystemInDarkTheme()
            val isDarkTheme = false
            MyApplicationTheme(isDarkTheme) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .let {
                            if (isDarkTheme) {
                                it.background(gradientBackground())
                            } else {
                                it.background(Color.LightGray)
                            }
                        },
                ) {
                    if (!isDarkTheme) {
                        Image(
                            painter = painterResource(R.drawable.polka_dot_background),
                            contentDescription = "background image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            alpha = .5f
                        )
                    }
                    MainNavigation()
                }
            }
        }
    }
}

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Home,
        Modifier.background(Color.Transparent)
    ) {
        composable<Home> {
            MainScaffold(
                navigateToChat = {
                    navController.navigateFromHome(Chat(it ?: getLatestDate()))
                },
                navigateToSettings = {
                    navController.navigateFromHome(Settings)
                }
            )
        }

        composable<Login> {
//            HomeScreen(setFabConfig = {}, setFabIcon = {})
        }


        composable<Chat>(enterTransition =  {
            enterSlideTransition(AnimatedContentTransitionScope.SlideDirection.Start, 300)
        }, exitTransition = {
            exitSlideTransition(AnimatedContentTransitionScope.SlideDirection.End, 300)
        }) {
            val route: Chat = it.toRoute()
            ChatScreen(
                route.date,
//                setFabConfig = {
//                    fabConfig = it
//                }, setAppBarContent = {
//                    appBarContent = it
//                },
                navBackToHome = {
                    navController.navigateUp()
                }
            )
        }

        composable<Settings>(enterTransition =  {
            enterSlideTransition(AnimatedContentTransitionScope.SlideDirection.Start, 300)
        }, exitTransition = {
            exitSlideTransition(AnimatedContentTransitionScope.SlideDirection.End, 300)
        }) {
            SettingsScreen(
                navigateDataSyncScreen = { navController.navigate(DataSyncList) },
                navigateBack = { navController.navigateUp() }
            )
        }
        composable<DataSyncList> {
            DataSyncScreen(navigateBack = { navController.navigateUp() })
        }
    }
}

// Bottom nav
@Serializable
private data object Home

@Serializable
private data object Login

@Serializable
data class Chat(val date: String)

// Settings
@Serializable
private data object Settings

@Serializable
private data object DataSyncList