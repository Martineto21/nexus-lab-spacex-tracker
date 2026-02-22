package com.nexuslab.spacextracker.ui.theme

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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Tema SpaceX Tracker - Generado por Nexus Platform
 * 
 * Implementa Material 3 con la identidad visual de SpaceX:
 * - Tema oscuro por defecto (como el espacio)
 * - Colores oficiales de SpaceX
 * - Soporte para Material You (Android 12+)
 * - Tipografía moderna y legible
 */

private val DarkColorScheme = darkColorScheme(
    primary = SpaceXBlue,
    onPrimary = SpaceXWhite,
    primaryContainer = SpaceXBlueDark,
    onPrimaryContainer = SpaceXWhite,
    
    secondary = SpaceXGray,
    onSecondary = SpaceXTextPrimary,
    secondaryContainer = SpaceXLightGray,
    onSecondaryContainer = SpaceXTextPrimary,
    
    tertiary = SpaceXOrange,
    onTertiary = SpaceXWhite,
    
    background = SpaceXBlack,
    onBackground = SpaceXTextPrimary,
    
    surface = SpaceXDarkGray,
    onSurface = SpaceXTextPrimary,
    surfaceVariant = SpaceXGray,
    onSurfaceVariant = SpaceXTextSecondary,
    
    error = SpaceXRed,
    onError = SpaceXWhite,
    
    outline = SpaceXTextSecondary,
    outlineVariant = SpaceXLightGray
)

private val LightColorScheme = lightColorScheme(
    primary = SpaceXBlue,
    onPrimary = SpaceXWhite,
    primaryContainer = SpaceXBlueLight,
    onPrimaryContainer = SpaceXTextOnLight,
    
    secondary = SpaceXLightGray,
    onSecondary = SpaceXWhite,
    secondaryContainer = SpaceXOffWhite,
    onSecondaryContainer = SpaceXTextOnLight,
    
    tertiary = SpaceXOrange,
    onTertiary = SpaceXWhite,
    
    background = SpaceXWhite,
    onBackground = SpaceXTextOnLight,
    
    surface = SpaceXOffWhite,
    onSurface = SpaceXTextOnLight,
    surfaceVariant = SpaceXOffWhite,
    onSurfaceVariant = SpaceXTextSecondary,
    
    error = SpaceXRed,
    onError = SpaceXWhite,
    
    outline = SpaceXTextSecondary,
    outlineVariant = SpaceXLightGray
)

@Composable
fun SpaceXTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color está disponible en Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
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
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}