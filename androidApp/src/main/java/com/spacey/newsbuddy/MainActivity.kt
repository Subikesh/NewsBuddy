package com.spacey.newsbuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.spacey.newsbuddy.android.R
import kotlinx.serialization.Serializable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isDarkTheme = isSystemInDarkTheme()
            MyApplicationTheme(isDarkTheme) {
                Box(
                    modifier = Modifier.fillMaxSize().let {
                        if (isDarkTheme) {
                            it.background(gradientBackground())
                        } else {
                            it.background(MaterialTheme.colorScheme.secondaryContainer)
                        }
                    },
                ) {
                    if (!isSystemInDarkTheme()) {
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
            MainScaffold()
        }

        composable<Login> {
//            HomeScreen(setFabConfig = {}, setFabIcon = {})
        }
    }
}

@Serializable
data object Home

@Serializable
data object Login