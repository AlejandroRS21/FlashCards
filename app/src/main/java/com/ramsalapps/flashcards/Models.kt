package com.ramsalapps.flashcards

import android.net.Uri

data class Deck(
    val id: String = "",
    val name: String,
    val cardCount: Int,
    val progress: Int,
    val icon: String = "📚", // Simplified for this example
    val flashcards: List<Flashcard> = emptyList()
)

data class Session(
    val deckName: String,
    val date: String,
    val duration: String,
    val improvement: Int,
    val icon: String
)

data class Flashcard(
    val question: String,
    val answer: String,
    val category: String
)

// Clase para representar el estado de importación temporal
data class ImportState(
    val fileUri: Uri? = null,
    val fileName: String = "",
    val deckName: String = "",
    val flashcards: List<Flashcard> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

