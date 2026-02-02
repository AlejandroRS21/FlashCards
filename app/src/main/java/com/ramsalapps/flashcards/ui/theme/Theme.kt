package com.ramsalapps.flashcards.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = AccentBlue,
    onPrimary = Color.White,
    secondary = AccentPink,
    onSecondary = TextDark,
    tertiary = AccentGreen,
    background = Color.White,
    surface = Color.White,
    onBackground = TextDark,
    onSurface = TextDark
)

@Composable
fun FlashCardsTheme(
    content: @Composable () -> Unit
) {
    // Forzamos siempre el LightColorScheme para ignorar el tema oscuro del sistema
    // y mantener la estética pastel.
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
