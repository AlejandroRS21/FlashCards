package com.ramsalapps.flashcards

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Calendar

data class DeckWithFlashcards(
    val deck: Deck,
    val flashcards: List<Flashcard>
)

data class TestDeckWithQuestions(
    val deck: TestDeck,
    val questions: List<TestQuestion>
)

class DataManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("flashcards_db", Context.MODE_PRIVATE)
    private val gson = Gson()

    init {
        createWelcomeDeckIfNeeded()
    }

    private fun createWelcomeDeckIfNeeded() {
        if (getAllDecks().isEmpty()) {
            val welcomeCards = listOf(
                Flashcard(
                    question = "¡Bienvenido a FlashCards! 🚀\n¿Cómo veo la respuesta?",
                    answer = "Toca la tarjeta para voltearla y ver la respuesta.",
                    category = "Tutorial"
                ),
                Flashcard(
                    question = "¿Cómo paso a la siguiente tarjeta?",
                    answer = "Desliza la tarjeta hacia la izquierda para la siguiente, o a la derecha para la anterior.",
                    category = "Tutorial"
                ),
                Flashcard(
                    question = "¿Qué es el botón de check (dominado)?",
                    answer = "Úsalo cuando ya sepas la respuesta. Esto actualizará tu progreso y estadísticas.",
                    category = "Tutorial"
                ),
                Flashcard(
                    question = "¿Cómo añado mis propios mazos?",
                    answer = "Ve a la sección 'Importar' y sube un archivo CSV con tus preguntas y respuestas.",
                    category = "Tutorial"
                )
            )
            val welcomeDeck = Deck(
                name = "Bienvenido",
                cardCount = welcomeCards.size,
                icon = "👋",
                flashcards = welcomeCards
            )
            saveDeck(DeckWithFlashcards(welcomeDeck, welcomeCards))
        }
    }

    // --- User Stats ---

    fun getUserStats(): UserStats {
        val json = sharedPreferences.getString("user_stats", null)
        val stats = if (json != null) {
            gson.fromJson(json, UserStats::class.java)
        } else UserStats()
        
        val now = System.currentTimeMillis()
        val lastActivity = Calendar.getInstance().apply { timeInMillis = stats.lastActivityDate }
        val today = Calendar.getInstance()

        val isSameDay = today.get(Calendar.YEAR) == lastActivity.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == lastActivity.get(Calendar.DAY_OF_YEAR)
        
        return if (!isSameDay && stats.lastActivityDate != 0L) {
            val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
            val wasYesterday = yesterday.get(Calendar.YEAR) == lastActivity.get(Calendar.YEAR) &&
                    yesterday.get(Calendar.DAY_OF_YEAR) == lastActivity.get(Calendar.DAY_OF_YEAR)
            
            val resetStats = stats.copy(
                cardsReviewedToday = 0,
                dailyGoalProgress = 0f,
                reviewsYesterday = if (wasYesterday) stats.cardsReviewedToday else 0,
                streakCount = if (wasYesterday) stats.streakCount else 0
            )
            saveUserStats(resetStats)
            resetStats
        } else stats
    }

    private fun saveUserStats(stats: UserStats) {
        val json = gson.toJson(stats)
        sharedPreferences.edit { putString("user_stats", json) }
    }

    fun updateDailyProgress(cardsCount: Int = 1, studyTimeMillis: Long = 0) {
        val stats = getUserStats()
        val now = System.currentTimeMillis()
        
        val lastActivity = Calendar.getInstance().apply { timeInMillis = stats.lastActivityDate }
        val today = Calendar.getInstance()
        val dayOfWeek = today.get(Calendar.DAY_OF_WEEK)

        val isSameDay = today.get(Calendar.YEAR) == lastActivity.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == lastActivity.get(Calendar.DAY_OF_YEAR)

        val newWeeklyReviews = stats.weeklyReviews.toMutableMap()
        val currentDayValue = newWeeklyReviews.getOrDefault(dayOfWeek, 0)
        newWeeklyReviews[dayOfWeek] = currentDayValue + cardsCount

        val newStats = if (isSameDay) {
            stats.copy(
                cardsReviewedToday = stats.cardsReviewedToday + cardsCount,
                totalCardsReviewed = stats.totalCardsReviewed + cardsCount,
                totalStudyTimeMillis = stats.totalStudyTimeMillis + studyTimeMillis,
                lastActivityDate = now,
                dailyGoalProgress = (stats.cardsReviewedToday + cardsCount).toFloat() / stats.dailyGoalTarget,
                weeklyReviews = newWeeklyReviews
            )
        } else {
            val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
            val wasYesterday = yesterday.get(Calendar.YEAR) == lastActivity.get(Calendar.YEAR) &&
                    yesterday.get(Calendar.DAY_OF_YEAR) == lastActivity.get(Calendar.DAY_OF_YEAR)

            stats.copy(
                streakCount = if (wasYesterday) stats.streakCount + 1 else 1,
                cardsReviewedToday = cardsCount,
                totalCardsReviewed = stats.totalCardsReviewed + cardsCount,
                totalStudyTimeMillis = stats.totalStudyTimeMillis + studyTimeMillis,
                lastActivityDate = now,
                dailyGoalProgress = cardsCount.toFloat() / stats.dailyGoalTarget,
                reviewsYesterday = if (wasYesterday) stats.cardsReviewedToday else 0,
                weeklyReviews = newWeeklyReviews
            )
        }
        saveUserStats(newStats)
    }

    fun markCardAsMastered(deckName: String, cardId: String) {
        val deckWithFlashcards = getDeck(deckName)
        if (deckWithFlashcards != null) {
            val updatedFlashcards = deckWithFlashcards.flashcards.map {
                if (it.id == cardId && !it.isMastered) {
                    it.copy(isMastered = true)
                } else it
            }
            val masteredCount = updatedFlashcards.count { it.isMastered }
            val newProgress = if (updatedFlashcards.isNotEmpty()) (masteredCount * 100) / updatedFlashcards.size else 0
            val updatedDeck = deckWithFlashcards.deck.copy(progress = newProgress, lastModified = System.currentTimeMillis())
            saveDeck(DeckWithFlashcards(updatedDeck, updatedFlashcards))
            
            val oldMastered = deckWithFlashcards.flashcards.find { it.id == cardId }?.isMastered ?: false
            if (!oldMastered) {
                val stats = getUserStats()
                saveUserStats(stats.copy(masteredCardsTotal = stats.masteredCardsTotal + 1))
            }
        }
    }

    // --- Flashcard Decks ---

    fun saveDeck(deckWithFlashcards: DeckWithFlashcards) {
        val json = gson.toJson(deckWithFlashcards)
        sharedPreferences.edit { putString("deck_${deckWithFlashcards.deck.name}", json) }
        updateDecksList(deckWithFlashcards.deck)
    }

    fun getDeck(deckName: String): DeckWithFlashcards? {
        val json = sharedPreferences.getString("deck_$deckName", null)
        return if (json != null) {
            gson.fromJson(json, DeckWithFlashcards::class.java)
        } else null
    }

    fun getAllDecks(): List<Deck> {
        val decksJson = sharedPreferences.getString("all_decks", "[]")
        val decksList: List<Deck> = gson.fromJson(decksJson, object : TypeToken<List<Deck>>() {}.type)
        return decksList.map { deck ->
            val deckWithFlashcards = getDeck(deck.name)
            if (deckWithFlashcards != null) {
                deck.copy(flashcards = deckWithFlashcards.flashcards, progress = deckWithFlashcards.deck.progress)
            } else deck
        }
    }

    private fun updateDecksList(deck: Deck) {
        val currentDecks = getAllDecks().toMutableList()
        val existingIndex = currentDecks.indexOfFirst { it.id == deck.id || it.name == deck.name }
        if (existingIndex >= 0) currentDecks[existingIndex] = deck else currentDecks.add(deck)
        sharedPreferences.edit { putString("all_decks", gson.toJson(currentDecks)) }
    }

    fun deleteDeck(deckName: String) {
        sharedPreferences.edit { remove("deck_$deckName") }
        val currentDecks = getAllDecks().toMutableList()
        currentDecks.removeAll { it.name == deckName }
        sharedPreferences.edit { putString("all_decks", gson.toJson(currentDecks)) }
    }

    fun updateDeck(deck: Deck) {
        val fullDeck = getDeck(deck.name)
        if (fullDeck != null) {
            saveDeck(fullDeck.copy(deck = deck))
        } else {
            saveDeck(DeckWithFlashcards(deck, emptyList()))
        }
    }

    fun deleteFlashcard(deckName: String, flashcardId: String) {
        val deckWithFlashcards = getDeck(deckName)
        if (deckWithFlashcards != null) {
            val updatedFlashcards = deckWithFlashcards.flashcards.filter { it.id != flashcardId }
            val updatedDeck = deckWithFlashcards.deck.copy(
                flashcards = updatedFlashcards,
                cardCount = updatedFlashcards.size
            )
            saveDeck(DeckWithFlashcards(updatedDeck, updatedFlashcards))
        }
    }

    fun addFlashcard(deckName: String, flashcard: Flashcard) {
        val deckWithFlashcards = getDeck(deckName)
        if (deckWithFlashcards != null) {
            val updatedFlashcards = deckWithFlashcards.flashcards + flashcard
            val updatedDeck = deckWithFlashcards.deck.copy(
                flashcards = updatedFlashcards,
                cardCount = updatedFlashcards.size
            )
            saveDeck(DeckWithFlashcards(updatedDeck, updatedFlashcards))
        }
    }

    fun updateFlashcard(deckName: String, flashcardId: String, updatedFlashcard: Flashcard) {
        val deckWithFlashcards = getDeck(deckName)
        if (deckWithFlashcards != null) {
            val updatedFlashcards = deckWithFlashcards.flashcards.map {
                if (it.id == flashcardId) updatedFlashcard else it
            }
            val updatedDeck = deckWithFlashcards.deck.copy(flashcards = updatedFlashcards)
            saveDeck(DeckWithFlashcards(updatedDeck, updatedFlashcards))
        }
    }

    // --- Test Decks ---

    fun saveTestDeck(testDeckWithQuestions: TestDeckWithQuestions) {
        val json = gson.toJson(testDeckWithQuestions)
        sharedPreferences.edit { putString("test_deck_${testDeckWithQuestions.deck.id}", json) }
        updateTestDecksList(testDeckWithQuestions.deck)
    }

    fun getTestDeck(testDeckId: String): TestDeckWithQuestions? {
        val json = sharedPreferences.getString("test_deck_$testDeckId", null)
        return if (json != null) {
            gson.fromJson(json, TestDeckWithQuestions::class.java)
        } else null
    }

    fun getAllTestDecks(): List<TestDeck> {
        val testsJson = sharedPreferences.getString("all_test_decks", "[]")
        return gson.fromJson(testsJson, object : TypeToken<List<TestDeck>>() {}.type)
    }

    private fun updateTestDecksList(testDeck: TestDeck) {
        val currentTests = getAllTestDecks().toMutableList()
        val existingIndex = currentTests.indexOfFirst { it.id == testDeck.id }
        if (existingIndex >= 0) currentTests[existingIndex] = testDeck else currentTests.add(testDeck)
        sharedPreferences.edit { putString("all_test_decks", gson.toJson(currentTests)) }
    }

    fun saveTestResult(testDeckId: String, score: Int, failedIds: List<String>) {
        val testWithQuestions = getTestDeck(testDeckId)
        if (testWithQuestions != null) {
            val updatedDeck = testWithQuestions.deck.copy(
                lastScore = score,
                failedQuestionIds = failedIds,
                lastModified = System.currentTimeMillis()
            )
            saveTestDeck(TestDeckWithQuestions(updatedDeck, testWithQuestions.questions))
        }
    }

    fun updateTestDeck(testDeck: TestDeck) {
        val fullTest = getTestDeck(testDeck.id)
        if (fullTest != null) {
            saveTestDeck(fullTest.copy(deck = testDeck))
        }
    }

    // --- Preferences ---
    fun setBionicReadingEnabled(enabled: Boolean) = sharedPreferences.edit { putBoolean("bionic_reading_enabled", enabled) }
    fun isBionicReadingEnabled() = sharedPreferences.getBoolean("bionic_reading_enabled", true)
    fun setSoundEffectsEnabled(enabled: Boolean) = sharedPreferences.edit { putBoolean("sound_effects_enabled", enabled) }
    fun isSoundEffectsEnabled() = sharedPreferences.getBoolean("sound_effects_enabled", true)
}
