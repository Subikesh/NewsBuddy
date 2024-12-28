package com.spacey.newsbuddy.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.WindowMetrics
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.spacey.newsbuddy.MainActivity
import com.spacey.newsbuddy.NewsHome
import com.spacey.newsbuddy.common.AiFeaturesDisabled
import com.spacey.newsbuddy.common.NoInternetException
import com.spacey.newsbuddy.common.isAiServerException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CenteredColumn(
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    verticalArrangement: Arrangement.Vertical = Arrangement.Center,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier.fillMaxSize(), verticalArrangement, horizontalAlignment, content)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CenteredTopBarScaffold(
    title: String,
    navigationIcon: @Composable () -> Unit,
    trailingIcon: @Composable RowScope.() -> Unit = {},
    fabContent: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(containerColor = Color.Transparent, modifier = Modifier.imePadding(), topBar = {
        CenteredTopBar(title, navigationIcon, windowInsets = TopAppBarDefaults.windowInsets, trailingIcon = trailingIcon)
    }, floatingActionButton = {
        fabContent()
    }, content = { padding ->
        Box(Modifier.padding(padding)) {
            content(padding)
        }
    })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CenteredTopBar(title: String, navigationIcon: @Composable () -> Unit, windowInsets: WindowInsets = WindowInsets(top = 0), trailingIcon: @Composable RowScope.() -> Unit) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
        title = {
            Text(
                title,
                Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
        },
        windowInsets = windowInsets,
        navigationIcon = navigationIcon,
        actions = trailingIcon
    )
}

@Composable
fun RoundIconButton(
    icon: ImageVector, iconColors: IconButtonColors = IconButtonDefaults.iconButtonColors(
        containerColor = MaterialTheme.colorScheme.tertiary,
        contentColor = MaterialTheme.colorScheme.onTertiary
    ), contentDescription: String, onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        colors = iconColors,
        modifier = Modifier
            .padding(vertical = 16.dp, horizontal = 8.dp)
            .size(40.dp)
    ) {
        Icon(icon, contentDescription = contentDescription)
    }
}

@Composable
fun BannerAd(adUnitId: String, adSize: AdSize? = null) {
    AndroidView(
        factory = { context ->
            // Get the ad size with screen width.
            val mainActivity = context as MainActivity
            val displayMetrics = mainActivity.resources.displayMetrics
            val adWidthPixels =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val windowMetrics: WindowMetrics = mainActivity.windowManager.currentWindowMetrics
                    windowMetrics.bounds.width()
                } else {
                    displayMetrics.widthPixels
                }
            val density = displayMetrics.density
            val adWidth = (adWidthPixels / density).toInt()

            AdView(mainActivity).apply {
                setAdSize(adSize ?: AdSize.getCurrentOrientationInlineAdaptiveBannerAdSize(mainActivity, adWidth))
                setAdUnitId(adUnitId)
                loadAd(AdRequest.Builder().build())
            }
        },
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    )
}

@Composable
fun BackIconButton(onClick: () -> Unit) {
    RoundIconButton(icon = Icons.Default.ArrowBack, contentDescription = "back", onClick = onClick)
}

fun <T : Any> NavController.navigateFromHome(route: T, inclusive: Boolean = false) {
    navigate(route, NavOptions.Builder().setPopUpTo<NewsHome>(inclusive).build())
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

@Composable
fun ContentCard(
    modifier: Modifier = Modifier,
    cardContainerColor: Color = MaterialTheme.colorScheme.tertiary,
    cardContentColor: Color = MaterialTheme.colorScheme.onTertiary,
    inclinedTo: Char? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = cardContainerColor,
            contentColor = cardContentColor
        ),
        modifier = modifier,
        shape = if (inclinedTo == null)
            RoundedCornerShape(20.dp)
        else
            RoundedCornerShape(
                topStart = if (inclinedTo == 'l') 0.dp else 20.dp,
                topEnd = if (inclinedTo == 'r') 0.dp else 20.dp,
                bottomStart = 20.dp, bottomEnd = 20.dp
            ),
        content = content
    )
}

@Composable
fun keyboardVisibility(): State<Boolean> {
    val isImeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    return rememberUpdatedState(isImeVisible)
}

@Composable
fun LoadingScreen(text: String) {
    CenteredColumn {
        ContentCard(Modifier.fillMaxWidth(0.7f)) {
            Text(text,
                Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .padding(top = 8.dp)
                    .align(Alignment.CenterHorizontally))
            CircularProgressIndicator(
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .padding(bottom = 8.dp))
        }

        BannerAd("ca-app-pub-1812668276280069/6486071237")
    }
}

@Composable
fun MessageScreen(text: String, modifier: Modifier = Modifier, contentColor: Color = MaterialTheme.colorScheme.onTertiary, actionButton: @Composable (() -> Unit)? = null) {
    CenteredColumn {
        ContentCard(modifier = modifier.fillMaxWidth(0.9f), cardContentColor = contentColor) {
            Text(text = text, modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally))

            actionButton?.invoke()
        }
        BannerAd("ca-app-pub-1812668276280069/7677237129")
    }
}

operator fun PaddingValues.plus(other: PaddingValues): PaddingValues = PaddingValues(
    start = this.calculateStartPadding(LayoutDirection.Ltr) + other.calculateStartPadding(LayoutDirection.Ltr),
    top = this.calculateTopPadding() + other.calculateTopPadding(),
    end = this.calculateEndPadding(LayoutDirection.Ltr) + other.calculateEndPadding(LayoutDirection.Ltr),
    bottom = this.calculateBottomPadding() + other.calculateBottomPadding(),
)

fun String.capitalize() =
    replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }

fun String.formatToHomeDateDisplay(): String {
    return LocalDate.parse(this, DateTimeFormatter.ISO_LOCAL_DATE).format(DateTimeFormatter.ofPattern("EEE, d MMM yyyy"))
}

// Animations
fun AnimatedContentTransitionScope<NavBackStackEntry>.enterSlideTransition(towards: AnimatedContentTransitionScope.SlideDirection, durationMillis: Int): EnterTransition {
    return fadeIn(
        animationSpec = tween(durationMillis, easing = FastOutSlowInEasing)
    ) + slideIntoContainer(
        animationSpec = tween(durationMillis, easing = FastOutSlowInEasing),
        towards = towards
    )
}

fun AnimatedContentTransitionScope<NavBackStackEntry>.exitSlideTransition(towards: AnimatedContentTransitionScope.SlideDirection, durationMillis: Int): ExitTransition {
    return fadeOut(
        animationSpec = tween(durationMillis, easing = FastOutSlowInEasing)
    ) + slideOutOfContainer(
        animationSpec = tween(durationMillis, easing = FastOutSlowInEasing),
        towards = towards
    )
}

fun Result<*>.getErrorMsgOrNull(isDebug: Boolean): Pair<String, Boolean>? {
    if (isSuccess) {
        return null
    }
    val exception = exceptionOrNull()!!
    exception.printStackTrace()
    Firebase.analytics.logEvent("ChatError", Bundle().apply {
        putString("Message", exception.message)
        putString("StackTrace", exception.stackTraceToString())
    })
    return when (exception) {
        is NoInternetException -> {
            "Error connecting to servers! Please check the internet connection and try again." to false
        }
        is AiFeaturesDisabled -> {
            "AI Features are disabled temporarily. Contact support to know more." to true
        }
        else -> {
            (if (isDebug && exception.message != null) {
                exception.message!!
            } else if (isAiServerException()) {
                "Some Error occured with the AI agent. Kindly contact support to report the issue!"
            } else {
                "Some unknown exception occured. Kindly contact support to report the issue!"
            }) to true
        }
    }
}