package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AegisDarkColorScheme =
    darkColorScheme(
        primary = AegisPrimary,
        onPrimary = Color.Black,
        secondary = AegisSecondary,
        onSecondary = Color.White,
        tertiary = AegisSuccess,
        background = AegisDarkBackground,
        onBackground = AegisTextPrimary,
        surface = AegisSurface,
        onSurface = AegisTextPrimary,
        surfaceVariant = AegisSurfaceVariant,
        onSurfaceVariant = AegisTextSecondary,
        error = AegisCritical,
        onError = Color.White
    )

@Composable
fun AegisAndroidTheme(
    darkTheme: Boolean = true, // Force dark cyber aesthetic by default for security console
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AegisDarkColorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    AegisAndroidTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}
