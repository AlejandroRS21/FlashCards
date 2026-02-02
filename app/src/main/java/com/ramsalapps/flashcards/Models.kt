package com.ramsalapps.flashcards

data class Deck(
    val name: String,
    val cardCount: Int,
    val progress: Int,
    val icon: String // Simplified for this example
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
