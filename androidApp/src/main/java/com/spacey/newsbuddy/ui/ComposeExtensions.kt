package com.spacey.newsbuddy.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
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
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.spacey.newsbuddy.NewsHome
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
    trailingIcon: @Composable () -> Unit = {},
    content: @Composable () -> Unit
) {
    Scaffold(containerColor = Color.Transparent, modifier = Modifier.imePadding(), topBar = {
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
            navigationIcon = navigationIcon,
            actions = { trailingIcon() }
        )
    }, content = { padding ->
        Box(Modifier.padding(padding)) {
            content()
        }
    })
}

@Composable
fun RoundIconButton(icon: ImageVector, contentDescription: String, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = MaterialTheme.colorScheme.tertiary,
            contentColor = MaterialTheme.colorScheme.onTertiary
        ),
        modifier = Modifier
            .padding(vertical = 16.dp, horizontal = 8.dp)
            .size(40.dp)
    ) {
        Icon(icon, contentDescription = contentDescription)
    }
}

@Composable
fun BackIconButton(onClick: () -> Unit) {
    RoundIconButton(icon = Icons.Default.ArrowBack, contentDescription = "back", onClick)
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
fun LoadingScreen(text: String = "Reading today's news 🗞️\nPlease give me a minute...") {
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
    }
}

@Composable
fun MessageScreen(text: String, modifier: Modifier = Modifier, contentColor: Color = MaterialTheme.colorScheme.onTertiary) {
    CenteredColumn {
        ContentCard(modifier = modifier.fillMaxWidth(0.9f), cardContentColor = contentColor) {
            Text(text = text, modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally))
        }
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
