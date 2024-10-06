package com.spacey.newsbuddy

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    // TODO: Adopt multiple themes
    val darkTheme = false
    val colors = if (darkTheme) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            dynamicDarkColorScheme(context)
//        } else {
            darkColorScheme(
                primary = Color(0xFFFFFFFF),
                secondary = Color(0xFF1c2756),
                tertiary = Color(0xFF292a33),
                onTertiary = Color.White,
                tertiaryContainer = Color(0xFFf4723a)
            )
//        }
    } else {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            dynamicLightColorScheme(context)
//        } else {
            lightColorScheme(
                primary = Color(0xFF03142a),
                onPrimary = Color.White,
                onTertiary = Color.Black,
                primaryContainer = Color(0xFF4a7feb),
                onPrimaryContainer = Color.White,
                secondaryContainer = Color(0xFFedf2f8),
                secondary = Color(0xFFfdfefe),
                tertiary = Color.White,
                onSecondaryContainer = Color.Black
            )
//        }
    }
    val typography = Typography(
        bodyMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        )
    )
    val shapes = Shapes(
        small = RoundedCornerShape(4.dp),
        medium = RoundedCornerShape(4.dp),
        large = RoundedCornerShape(0.dp)
    )

    MaterialTheme(
        colorScheme = colors,
        typography = typography,
        shapes = shapes,
        content = content
    )
}

@Composable
fun gradientBackground(): Brush {
    return Brush.radialGradient(
        colors = listOf(
            Color(0xFF322054),
            Color(0xFF070707),
        ), center = Offset(900f, 100f), radius = 800f
    )
}