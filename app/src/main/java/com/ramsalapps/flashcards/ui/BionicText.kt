package com.ramsalapps.flashcards.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit

@Composable
fun BionicText(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = TextUnit.Unspecified,
    color: Color = Color.Unspecified,
    textAlign: TextAlign = TextAlign.Start
) {
    val annotatedString = buildBionicString(text)
    Text(
        text = annotatedString,
        modifier = modifier,
        fontSize = fontSize,
        color = color,
        textAlign = textAlign
    )
}

fun buildBionicString(text: String): AnnotatedString {
    return buildAnnotatedString {
        val words = text.split(" ")
        words.forEachIndexed { index, word ->
            if (word.isNotEmpty()) {
                // No aplicar bionic reading a palabras que empiezan con símbolos, números solo, o paréntesis/guiones
                val isSymbolOrSpecial = word.firstOrNull()?.let {
                    !it.isLetterOrDigit() || (word.all { c -> c.isDigit() || c == ',' || c == '.' || c == '-' || c == '/' })
                } ?: true

                if (!isSymbolOrSpecial && word.any { it.isLetter() }) {
                    // Aplicar lectura biónica solo si hay letras
                    val mid = (word.length + 1) / 2
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(word.substring(0, mid))
                    }
                    append(word.substring(mid))
                } else {
                    // No aplicar formato a símbolos, números, fechas, etc.
                    append(word)
                }
            }
            if (index < words.size - 1) {
                append(" ")
            }
        }
    }
}
