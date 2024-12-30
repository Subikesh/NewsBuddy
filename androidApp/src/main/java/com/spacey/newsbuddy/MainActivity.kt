package com.spacey.newsbuddy

import android.os.Build
import android.os.Bundle
import android.util.Log
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
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest.*
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.spacey.newsbuddy.android.R
import com.spacey.newsbuddy.chat.ChatScreen
import com.spacey.newsbuddy.settings.SettingsScreen
import com.spacey.newsbuddy.settings.sync.DataSyncScreen
import com.spacey.newsbuddy.ui.enterSlideTransition
import com.spacey.newsbuddy.ui.exitSlideTransition
import com.spacey.newsbuddy.ui.getLatestDate
import com.spacey.newsbuddy.ui.navigateFromHome
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

class MainActivity : ComponentActivity() {

    private var interstitialAd: InterstitialAd? = null
    private var adIsLoading: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
        }
        setupFirebaseRemoteConfig()

        setupAdMobInitialization()

        mainContent()
    }

    fun showInterstitialAd() {
        if (interstitialAd != null) {
            // TODO: Remove the logs after testing
            interstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
                override fun onAdClicked() {
                    // Called when a click is recorded for an ad.
                    Log.d("AdCallback", "Ad was clicked.")
                }

                override fun onAdDismissedFullScreenContent() {
                    // Called when ad is dismissed.
                    Log.d("AdCallback", "Ad dismissed fullscreen content.")
                    interstitialAd = null
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    // Called when ad fails to show.
                    Log.e("AdCallback", "Ad failed to show fullscreen content. $adError")
                    interstitialAd = null
                }

                override fun onAdImpression() {
                    // Called when an impression is recorded for an ad.
                    Log.d("AdCallback", "Ad recorded an impression.")
                }

                override fun onAdShowedFullScreenContent() {
                    // Called when ad is shown.
                    Log.d("AdCallback", "Ad showed fullscreen content.")
                }
            }
            interstitialAd?.show(this)
        } else {
            Log.d("AdMessage", "Interstitial ad is not ready yet!")
            loadInterstitialAd()
        }
    }

    private fun mainContent() {
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

    private fun setupAdMobInitialization() {
        val backgroundScope = CoroutineScope(Dispatchers.IO)
        backgroundScope.launch {
            MobileAds.initialize(this@MainActivity) {}
            MobileAds.setAppVolume(0.5f)
        }
    }

    fun loadInterstitialAd() {
        if (adIsLoading || interstitialAd != null) {
            return
        }

        adIsLoading = true
        val adRequest = Builder().build()

        InterstitialAd.load(this,"ca-app-pub-4784093806834711/2767723047", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.e("AdMessage", "Ad failure: $adError")
                interstitialAd = null
                adIsLoading = false
            }

            override fun onAdLoaded(loadedAd: InterstitialAd) {
                Log.d("AdMessage", "Ad was loaded.")
                interstitialAd = loadedAd
                adIsLoading = false
            }
        })
    }

    private fun setupFirebaseRemoteConfig() {
        val configSettings = remoteConfigSettings {
            setFetchTimeoutInSeconds(3600)
        }
        FirebaseRemoteConfig.getInstance().apply {
            setConfigSettingsAsync(configSettings)
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
            composable<Home>(exitTransition = {
                exitSlideTransition(AnimatedContentTransitionScope.SlideDirection.Start, 300)
            }, popEnterTransition = {
                enterSlideTransition(AnimatedContentTransitionScope.SlideDirection.End, 300)
            }) {
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
            }, popExitTransition = {
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
                    navigateBack = {
                        showInterstitialAd()
                        navController.navigateUp()
                    }
                )
            }

            composable<Settings>(enterTransition =  {
                enterSlideTransition(AnimatedContentTransitionScope.SlideDirection.Start, 300)
            }, exitTransition = {
                exitSlideTransition(AnimatedContentTransitionScope.SlideDirection.Start, 300)
            }, popEnterTransition = {
                enterSlideTransition(AnimatedContentTransitionScope.SlideDirection.End, 300)
            }, popExitTransition = {
                exitSlideTransition(AnimatedContentTransitionScope.SlideDirection.End, 300)
            }) {
                SettingsScreen(
                    navigateDataSyncScreen = { navController.navigate(DataSyncList) },
                    navigateBack = { navController.navigateUp() }
                )
            }
            composable<DataSyncList>(enterTransition =  {
                enterSlideTransition(AnimatedContentTransitionScope.SlideDirection.Start, 300)
            }, exitTransition = {
                exitSlideTransition(AnimatedContentTransitionScope.SlideDirection.End, 300)
            }) {
                DataSyncScreen(navigateBack = { navController.navigateUp() })
            }
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