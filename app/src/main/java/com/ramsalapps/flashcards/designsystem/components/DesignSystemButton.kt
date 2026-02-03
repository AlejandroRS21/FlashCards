package com.ramsalapps.flashcards.designsystem.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramsalapps.flashcards.ui.theme.AccentBlue
import com.ramsalapps.flashcards.ui.theme.BorderRadius
import com.ramsalapps.flashcards.ui.theme.Spacing

/**
 * Design System Button Component
 * 
 * Wrapper alrededor de Material3 Button para mantener consistencia de diseño.
 * Proporciona 3 tamaños: Small (36dp), Medium (44dp), Large (52dp)
 * 
 * @param text Texto del botón
 * @param onClick Callback al hacer click
 * @param size Tamaño del botón (Small, Medium, Large)
 * @param modifier Modifier adicional
 * @param enabled Si el botón está habilitado
 */
@Composable
fun DesignSystemButton(
    text: String,
    onClick: () -> Unit,
    size: ButtonSize = ButtonSize.Medium,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val (height, fontSize) = when (size) {
        ButtonSize.Small -> 36.dp to 12.sp
        ButtonSize.Medium -> 44.dp to 14.sp
        ButtonSize.Large -> 52.dp to 16.sp
    }
    
    Button(
        onClick = onClick,
        modifier = modifier.height(height),
        colors = ButtonDefaults.buttonColors(
            containerColor = AccentBlue,
            disabledContainerColor = AccentBlue.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(BorderRadius.md),
        enabled = enabled
    ) {
        Text(
            text = text,
            fontSize = fontSize,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Enum para representar los tamaños disponibles del botón
 */
enum class ButtonSize {
    /**
     * Botón pequeño: 36dp de alto, 12sp de texto
     * Uso: acciones secundarias, pequeños espacios
     */
    Small,
    
    /**
     * Botón mediano: 44dp de alto, 14sp de texto
     * Uso: botones estándar, acciones normales
     */
    Medium,
    
    /**
     * Botón grande: 52dp de alto, 16sp de texto
     * Uso: acciones principales, CTAs
     */
    Large
}
