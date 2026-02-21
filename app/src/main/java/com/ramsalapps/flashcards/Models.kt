package com.ramsalapps.flashcards

data class DeckSessionState(
    val lastCardIndex: Int = 0,
    val cardIdsOrder: List<String> = emptyList(),
    val isFlipped: Boolean = false
)

data class Deck(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val cardCount: Int,
    val progress: Int = 0,
    val icon: String = "📚",
    val flashcards: List<Flashcard> = emptyList(),
    val createdDate: Long = System.currentTimeMillis(),
    val sessionState: DeckSessionState? = null,
    val lastModified: Long = System.currentTimeMillis()
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
    val category: String,
    val isMastered: Boolean = false,
    val lastReviewed: Long = 0
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
    val createdDate: Long = System.currentTimeMillis(),
    val lastModified: Long = System.currentTimeMillis()
)

data class TestResult(
    val testDeckId: String,
    val deckName: String,
    val date: String,
    val score: Int,
    val totalQuestions: Int,
    val icon: String = "📝"
)

data class UserStats(
    val streakCount: Int = 0,
    val lastActivityDate: Long = 0,
    val masteredCardsTotal: Int = 0,
    val dailyGoalProgress: Float = 0f,
    val dailyGoalTarget: Int = 10,
    val cardsReviewedToday: Int = 0,
    val totalCardsReviewed: Int = 0,
    val totalStudyTimeMillis: Long = 0,
    val weeklyReviews: Map<Int, Int> = emptyMap(), // DayOfWeek (1-7) -> count
    val reviewsYesterday: Int = 0
)
