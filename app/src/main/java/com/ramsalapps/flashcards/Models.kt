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

data class TestQuestion(
    val id: String = java.util.UUID.randomUUID().toString(),
    val question: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val explanation: String = ""
)

data class TestDeck(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val questionCount: Int,
    val lastScore: Int? = null,
    val icon: String = "📝",
    val questions: List<TestQuestion> = emptyList(),
    val failedQuestionIds: List<String> = emptyList(),
    val createdDate: Long = System.currentTimeMillis()
)

data class TestResult(
    val testDeckId: String,
    val deckName: String,
    val date: String,
    val score: Int,
    val totalQuestions: Int,
    val icon: String = "📝"
)
