package com.ucb.whosin.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Esquema de colores claro (basado en la imagen de referencia)
private val WhosInLightColorScheme = lightColorScheme(
    // Colores primarios
    primary = WhosInColors.DarkTeal,
    onPrimary = WhosInColors.LightGray,
    primaryContainer = WhosInColors.PetrolBlue,
    onPrimaryContainer = WhosInColors.LightGray,

    // Colores secundarios (Lime para acentos)
    secondary = WhosInColors.LimeGreen,
    onSecondary = WhosInColors.DarkTeal,
    secondaryContainer = WhosInColors.MintGreen,
    onSecondaryContainer = WhosInColors.DarkTeal,

    // Colores terciarios
    tertiary = WhosInColors.OliveGreen,
    onTertiary = WhosInColors.White,
    tertiaryContainer = WhosInColors.ForestGreen,
    onTertiaryContainer = WhosInColors.LightGray,

    // Fondos y superficies
    background = WhosInColors.LightGray,
    onBackground = WhosInColors.DarkTeal,
    surface = WhosInColors.White,
    onSurface = WhosInColors.DarkTeal,
    surfaceVariant = Color(0xFFF5F7F7),
    onSurfaceVariant = WhosInColors.GrayBlue,

    // Contornos
    outline = WhosInColors.GrayBlue,
    outlineVariant = Color(0xFFD0D5D5),

    // Estados de error
    error = WhosInColors.Error,
    onError = WhosInColors.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    // Inversos
    inverseSurface = WhosInColors.DarkTeal,
    inverseOnSurface = WhosInColors.LightGray,
    inversePrimary = WhosInColors.LimeGreen,

    // Scrim
    scrim = WhosInColors.Black,
    surfaceTint = WhosInColors.DarkTeal
)

// Esquema de colores oscuro (para futuro uso)
private val WhosInDarkColorScheme = darkColorScheme(
    primary = WhosInColors.LimeGreen,
    onPrimary = WhosInColors.DarkTeal,
    primaryContainer = WhosInColors.ForestGreen,
    onPrimaryContainer = WhosInColors.MintGreen,

    secondary = WhosInColors.MintGreen,
    onSecondary = WhosInColors.DarkTeal,
    secondaryContainer = WhosInColors.OliveGreen,
    onSecondaryContainer = WhosInColors.LightGray,

    tertiary = WhosInColors.LimeGreen,
    onTertiary = WhosInColors.DarkTeal,
    tertiaryContainer = WhosInColors.PetrolBlue,
    onTertiaryContainer = WhosInColors.LightGray,

    background = WhosInColors.DarkTeal,
    onBackground = WhosInColors.LightGray,
    surface = WhosInColors.DarkTealLight,
    onSurface = WhosInColors.LightGray,
    surfaceVariant = WhosInColors.PetrolBlue,
    onSurfaceVariant = WhosInColors.GrayBlue,

    outline = WhosInColors.GrayBlue,
    outlineVariant = WhosInColors.PetrolBlue,

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    inverseSurface = WhosInColors.LightGray,
    inverseOnSurface = WhosInColors.DarkTeal,
    inversePrimary = WhosInColors.PetrolBlue,

    scrim = WhosInColors.Black,
    surfaceTint = WhosInColors.LimeGreen
)

@Composable
fun WhosinTheme(
    darkTheme: Boolean = false, // Por defecto modo claro como la imagen
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) WhosInDarkColorScheme else WhosInLightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Status bar con el color del fondo
            window.statusBarColor = colorScheme.background.toArgb()
            // Iconos oscuros en modo claro, claros en modo oscuro
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = WhosInTypography,
        content = content
    )

}