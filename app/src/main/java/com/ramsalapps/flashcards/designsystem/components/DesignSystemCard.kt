package com.ramsalapps.flashcards.designsystem.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.ramsalapps.flashcards.ui.theme.BorderRadius
import com.ramsalapps.flashcards.ui.theme.Spacing

/**
 * Design System Card Component
 * 
 * Wrapper alrededor de Material3 Card para mantener consistencia.
 * Proporciona padding estándar (Spacing.lg = 16dp) y border radius (BorderRadius.md = 12dp)
 * 
 * @param modifier Modifier adicional
 * @param containerColor Color de fondo (default: blanco)
 * @param content Contenido del card
 */
@Composable
fun DesignSystemCard(
    modifier: Modifier = Modifier,
    containerColor: Color = Color.White,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = Spacing.xs),
        shape = RoundedCornerShape(BorderRadius.md),
        content = content
    )
}
