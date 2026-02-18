package com.ramsalapps.flashcards.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PowderBlue,
    onPrimary = TextDark,
    secondary = PeachPuff,
    onSecondary = TextDark,
    tertiary = LemonChiffon,
    background = Cream,
    surface = Color.White,
    onBackground = TextDark,
    onSurface = TextDark,
    surfaceVariant = Cream,
    onSurfaceVariant = TextGray
)

@Composable
fun FlashCardsTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
