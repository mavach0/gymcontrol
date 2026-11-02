package com.gymtracker.app.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = GymPrimary,
    onPrimary = TextPrimary,
    primaryContainer = GymSurfaceVariant,
    onPrimaryContainer = GymPrimary,

    secondary = GymSecondary,
    onSecondary = GymBackground,
    secondaryContainer = GymSurfaceVariant,
    onSecondaryContainer = GymSecondary,

    tertiary = GymOrange,
    onTertiary = GymBackground,

    background = GymBackground,
    onBackground = TextPrimary,

    surface = GymSurface,
    onSurface = TextPrimary,
    surfaceVariant = GymSurfaceVariant,
    onSurfaceVariant = TextSecondary,

    outline = GymCardStroke,
    error = GymRed,
    onError = TextPrimary
)

@Composable
fun GymTrackerTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                try {
                    val insetsController = WindowCompat.getInsetsController(window, view)
                    insetsController.isAppearanceLightStatusBars = false
                    insetsController.isAppearanceLightNavigationBars = false
                } catch (_: Exception) {
                    // Safe guard for API differences
                }
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = GymTypography,
        shapes = GymShapes,
        content = content
    )
}
