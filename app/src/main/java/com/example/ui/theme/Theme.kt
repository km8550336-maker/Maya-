package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val MayaDarkColorScheme = darkColorScheme(
    primary = MayaPinkPrimary,
    onPrimary = MayaTextPrimary,
    primaryContainer = MayaDarkSurfaceVariant,
    onPrimaryContainer = MayaPinkSoft,
    secondary = MayaJarvisCyan,
    onSecondary = MayaDarkBackground,
    secondaryContainer = MayaDarkSurfaceElevated,
    onSecondaryContainer = MayaJarvisCyanGlow,
    tertiary = MayaLavender,
    onTertiary = MayaDarkBackground,
    background = MayaDarkBackground,
    onBackground = MayaTextPrimary,
    surface = MayaDarkSurface,
    onSurface = MayaTextPrimary,
    surfaceVariant = MayaDarkSurfaceVariant,
    onSurfaceVariant = MayaTextSecondary,
    outline = MayaBorderGlow
)

@Composable
fun MayaTheme(
    darkTheme: Boolean = true, // Maya shines brightest in dark cyber-anime mode
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = MayaDarkBackground.toArgb()
                window.navigationBarColor = MayaDarkBackground.toArgb()
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = false
                controller.isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = MayaDarkColorScheme,
        typography = Typography,
        content = content
    )
}
