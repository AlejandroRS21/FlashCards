package com.ramsalapps.flashcards.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit

@Composable
fun BionicText(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = TextUnit.Unspecified,
    color: Color = Color.Unspecified
) {
    val annotatedString = buildBionicString(text)
    Text(
        text = annotatedString,
        modifier = modifier,
        fontSize = fontSize,
        color = color
    )
}

fun buildBionicString(text: String): AnnotatedString {
    return buildAnnotatedString {
        val words = text.split(" ")
        words.forEachIndexed { index, word ->
            if (word.isNotEmpty()) {
                val mid = (word.length + 1) / 2
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(word.substring(0, mid))
                }
                append(word.substring(mid))
            }
            if (index < words.size - 1) {
                append(" ")
            }
        }
    }
}
