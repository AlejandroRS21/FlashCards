package com.ramsalapps.flashcards.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.ramsalapps.flashcards.ui.theme.Spacing
import com.ramsalapps.flashcards.ui.theme.TextDark
import com.ramsalapps.flashcards.ui.theme.TextGray

/**
 * Design System ListItem Component
 * 
 * Componente para representar elementos en listas con estructura consistente.
 * Incluye: icon (opcional), texto principal, subtexto (opcional), trailing (opcional)
 * 
 * @param title Texto principal
 * @param modifier Modifier adicional
 * @param icon Icono opcional
 * @param subtitle Subtexto opcional
 * @param trailing Contenido trailing (lado derecho) opcional
 */
@Composable
fun DesignSystemListItem(
    title: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    subtitle: String? = null,
    trailing: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = modifier.padding(horizontal = Spacing.lg, vertical = Spacing.md),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.padding(Spacing.sm)
                )
            }
            
            androidx.compose.foundation.layout.Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Medium,
                    color = TextDark,
                    fontSize = 14.sp
                )
                subtitle?.let {
                    Text(
                        text = it,
                        color = TextGray,
                        fontSize = 12.sp
                    )
                }
            }
        }
        
        trailing?.invoke()
    }
}
