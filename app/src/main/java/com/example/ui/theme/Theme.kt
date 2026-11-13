package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GamifiedColorScheme = lightColorScheme(
    primary = GamifyGreen,
    onPrimary = Color.White,
    primaryContainer = GamifyGreenLight,
    onPrimaryContainer = GamifyGreenDark,
    secondary = GamifyBlue,
    onSecondary = Color.White,
    secondaryContainer = GamifyBlueLight,
    onSecondaryContainer = GamifyBlueDark,
    error = GamifyRed,
    onError = Color.White,
    errorContainer = GamifyRedLight,
    onErrorContainer = GamifyRedDark,
    background = NeutralWhite,
    onBackground = NeutralText,
    surface = NeutralWhite,
    onSurface = NeutralText,
    surfaceVariant = NeutralCard,
    onSurfaceVariant = NeutralSubtext,
    outline = NeutralBorder,
    outlineVariant = NeutralBorder
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = GamifiedColorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
