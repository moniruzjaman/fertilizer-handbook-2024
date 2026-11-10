package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = AgrigreenAccent,
    onPrimary = AgrigreenDark,
    primaryContainer = AgrigreenDark,
    onPrimaryContainer = AgrigreenContainer,
    secondary = HarvestGold,
    onSecondary = Color(0xFF281800),
    secondaryContainer = Color(0xFF4A3414),
    onSecondaryContainer = Color(0xFFFFDDB3),
    tertiary = AgrigreenLight,
    onTertiary = Color(0xFF003822),
    background = SurfaceDark,
    surface = SurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onBackground = Color(0xFFE2E3E1),
    onSurface = Color(0xFFE2E3E1),
    onSurfaceVariant = Color(0xFFC3C7C0)
)

private val LightColorScheme = lightColorScheme(
    primary = AgrigreenPrimary,
    onPrimary = Color.White,
    primaryContainer = AgrigreenContainer,
    onPrimaryContainer = AgrigreenOnContainer,
    secondary = EarthSoil,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0E8D0),
    onSecondaryContainer = Color(0xFF1B2308),
    tertiary = HarvestGold,
    onTertiary = Color.White,
    background = SurfaceLight,
    surface = SurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onBackground = Color(0xFF191C1A),
    onSurface = Color(0xFF191C1A),
    onSurfaceVariant = Color(0xFF414942)
)

@Composable
fun FrgTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
