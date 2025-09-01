package com.project.cureconnect.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = BackgroundWhite,
    primaryContainer = PrimaryBlueLight,
    onPrimaryContainer = TextPrimary,

    secondary = SecondaryTeal,
    onSecondary = BackgroundWhite,
    secondaryContainer = SecondaryTealLight,
    onSecondaryContainer = TextPrimary,

    tertiary = MedicalBlue,
    onTertiary = BackgroundWhite,

    error = ErrorRed,
    onError = BackgroundWhite,

    background = BackgroundLight,
    onBackground = TextPrimary,

    surface = CardBackground,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceLight,
    onSurfaceVariant = TextSecondary,

    outline = DividerColor,
    outlineVariant = Color(0xFFE0E0E0)
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlueLight,
    onPrimary = Color(0xFF1A1A1A),
    primaryContainer = PrimaryBlueDark,
    onPrimaryContainer = BackgroundWhite,

    secondary = SecondaryTealLight,
    onSecondary = Color(0xFF1A1A1A),
    secondaryContainer = SecondaryTealDark,
    onSecondaryContainer = BackgroundWhite,

    tertiary = WellnessBlue,
    onTertiary = Color(0xFF1A1A1A),

    error = ErrorRed,
    onError = Color(0xFF1A1A1A),

    background = Color(0xFF121212),
    onBackground = Color(0xFFE0E0E0),

    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE0E0E0),
    surfaceVariant = Color(0xFF2A2A2A),
    onSurfaceVariant = Color(0xFFB0B0B0),

    outline = Color(0xFF404040),
    outlineVariant = Color(0xFF2A2A2A)
)

@Composable
fun CureConnectTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = CureConnectTypography,
        shapes = CureConnectShapes,
        content = content
    )
}