package com.ramsalapps.flashcards.designsystem.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.ramsalapps.flashcards.ui.theme.AccentBlue
import com.ramsalapps.flashcards.ui.theme.BorderRadius
import com.ramsalapps.flashcards.ui.theme.Spacing
import com.ramsalapps.flashcards.ui.theme.TextGray

/**
 * Design System TextField Component
 * 
 * Wrapper alrededor de Material3 TextField con altura estándar (48dp).
 * Mantiene consistencia en styling de campos de entrada.
 * 
 * @param value Valor actual del campo
 * @param onValueChange Callback cuando el valor cambia
 * @param label Etiqueta del campo
 * @param modifier Modifier adicional
 * @param placeholder Placeholder del campo
 * @param visualTransformation Transformación visual (ej: password)
 * @param singleLine Si es una sola línea
 */
@Composable
fun DesignSystemTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "",
    modifier: Modifier = Modifier,
    placeholder: String = "",
    visualTransformation: VisualTransformation = VisualTransformation.None,
    singleLine: Boolean = true
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = if (label.isNotEmpty()) ({ Text(label) }) else null,
        placeholder = if (placeholder.isNotEmpty()) ({ Text(placeholder, color = TextGray) }) else null,
        visualTransformation = visualTransformation,
        singleLine = singleLine,
        shape = RoundedCornerShape(BorderRadius.sm),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = AccentBlue,
            unfocusedIndicatorColor = Color.LightGray,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color(0xFFF5F5F5)
        )
    )
}
