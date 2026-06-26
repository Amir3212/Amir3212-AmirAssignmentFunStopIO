package com.example.amirassignment.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = MarketplaceAccentStrong,
    onPrimary = MarketplaceNavy,
    primaryContainer = MarketplaceNavy,
    onPrimaryContainer = Color.White,
    secondary = MarketplaceAccent,
    background = Color(0xFF121820),
    onBackground = Color(0xFFE8EAED),
    surface = Color(0xFF1E2836),
    onSurface = Color(0xFFE8EAED),
    surfaceVariant = Color(0xFF2A3544),
    onSurfaceVariant = Color(0xFFB0B8C4),
    tertiaryContainer = MarketplaceNavy,
)

private val LightColorScheme = lightColorScheme(
    primary = MarketplaceNavy,
    onPrimary = Color.White,
    primaryContainer = MarketplaceAccent,
    onPrimaryContainer = MarketplaceNavy,
    secondary = MarketplaceAccentStrong,
    onSecondary = MarketplaceNavy,
    background = MarketplaceBackground,
    onBackground = MarketplaceNavy,
    surface = Color.White,
    onSurface = MarketplaceNavy,
    surfaceVariant = MarketplaceChipInactive,
    onSurfaceVariant = Color(0xFF5C6570),
    tertiary = StockGreen,
    tertiaryContainer = MarketplaceNavy,
    onTertiaryContainer = Color.White,
    outline = Color(0xFFD0D4DA),
)

@Composable
fun AmirAssignmentTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
