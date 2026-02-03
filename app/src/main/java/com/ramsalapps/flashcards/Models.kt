package com.ramsalapps.flashcards

data class Deck(
    val id: String = "",
    val name: String,
    val cardCount: Int,
    val progress: Int,
    val icon: String = "📚",
    val flashcards: List<Flashcard> = emptyList(),
    val createdDate: Long = System.currentTimeMillis()
)

data class Session(
    val deckName: String,
    val date: String,
    val duration: String,
    val improvement: Int,
    val icon: String
)

data class Flashcard(
    val id: String = java.util.UUID.randomUUID().toString(),
    val question: String,
    val answer: String,
    val category: String
)


