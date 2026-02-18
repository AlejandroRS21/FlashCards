package com.ramsalapps.flashcards

import android.content.Context
import android.content.SharedPreferences
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

    // --- User Stats ---

    fun getUserStats(): UserStats {
        val json = sharedPreferences.getString("user_stats", null)
        return if (json != null) {
            gson.fromJson(json, UserStats::class.java)
        } else UserStats()
    }

    private fun saveUserStats(stats: UserStats) {
        val json = gson.toJson(stats)
        sharedPreferences.edit().putString("user_stats", json).apply()
    }

    fun updateDailyProgress() {
        val stats = getUserStats()
        val now = System.currentTimeMillis()
        val calendar = Calendar.getInstance()
        
        val lastActivity = Calendar.getInstance().apply { timeInMillis = stats.lastActivityDate }
        val today = Calendar.getInstance()

        val isSameDay = today.get(Calendar.YEAR) == lastActivity.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == lastActivity.get(Calendar.DAY_OF_YEAR)

        val newStats = if (isSameDay) {
            stats.copy(
                cardsReviewedToday = stats.cardsReviewedToday + 1,
                lastActivityDate = now,
                dailyGoalProgress = (stats.cardsReviewedToday + 1).toFloat() / stats.dailyGoalTarget
            )
        } else {
            // Comprobar racha
            val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
            val wasYesterday = yesterday.get(Calendar.YEAR) == lastActivity.get(Calendar.YEAR) &&
                    yesterday.get(Calendar.DAY_OF_YEAR) == lastActivity.get(Calendar.DAY_OF_YEAR)

            stats.copy(
                streakCount = if (wasYesterday) stats.streakCount + 1 else 1,
                cardsReviewedToday = 1,
                lastActivityDate = now,
                dailyGoalProgress = 1f / stats.dailyGoalTarget
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
            val totalCount = updatedFlashcards.size
            val newProgress = if (totalCount > 0) (masteredCount * 100) / totalCount else 0
            
            val updatedDeck = deckWithFlashcards.deck.copy(
                flashcards = updatedFlashcards,
                progress = newProgress
            )
            
            saveDeck(DeckWithFlashcards(updatedDeck, updatedFlashcards))
            
            // Actualizar total de dominadas en stats si es nueva
            val oldMastered = deckWithFlashcards.flashcards.find { it.id == cardId }?.isMastered ?: false
            if (!oldMastered) {
                val stats = getUserStats()
                saveUserStats(stats.copy(masteredCardsTotal = stats.masteredCardsTotal + 1))
            }
        }
    }

    // --- Métodos para preferencias de usuario ---
    fun setBionicReadingEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean("bionic_reading_enabled", enabled).apply()
    }

    fun isBionicReadingEnabled(): Boolean {
        return sharedPreferences.getBoolean("bionic_reading_enabled", true)
    }

    fun setSoundEffectsEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean("sound_effects_enabled", enabled).apply()
    }

    fun isSoundEffectsEnabled(): Boolean {
        return sharedPreferences.getBoolean("sound_effects_enabled", true)
    }

    // --- Flashcard Decks ---

    fun saveDeck(deck: Deck) {
        val deckWithFlashcards = DeckWithFlashcards(deck, deck.flashcards)
        val json = gson.toJson(deckWithFlashcards)
        sharedPreferences.edit().putString("deck_${deck.name}", json).apply()
        updateDecksList(deck)
    }

    fun saveDeck(deckWithFlashcards: DeckWithFlashcards) {
        val json = gson.toJson(deckWithFlashcards)
        sharedPreferences.edit().putString("deck_${deckWithFlashcards.deck.name}", json).apply()
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
            } else {
                deck
            }
        }
    }

    private fun updateDecksList(deck: Deck) {
        val currentDecks = getAllDecks().toMutableList()
        val existingIndex = currentDecks.indexOfFirst { it.name == deck.name }

        if (existingIndex >= 0) {
            currentDecks[existingIndex] = deck
        } else {
            currentDecks.add(deck)
        }

        val json = gson.toJson(currentDecks)
        sharedPreferences.edit().putString("all_decks", json).apply()
    }

    fun deleteDeck(deckName: String) {
        sharedPreferences.edit().remove("deck_$deckName").apply()
        val currentDecks = getAllDecks().toMutableList()
        currentDecks.removeAll { it.name == deckName }
        val json = gson.toJson(currentDecks)
        sharedPreferences.edit().putString("all_decks", json).apply()
    }

    fun updateDeck(deck: Deck) {
        saveDeck(deck)
    }

    // --- Test Decks ---

    fun saveTestDeck(testDeckWithQuestions: TestDeckWithQuestions) {
        val json = gson.toJson(testDeckWithQuestions)
        sharedPreferences.edit().putString("test_deck_${testDeckWithQuestions.deck.id}", json).apply()
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

        if (existingIndex >= 0) {
            currentTests[existingIndex] = testDeck
        } else {
            currentTests.add(testDeck)
        }

        val json = gson.toJson(currentTests)
        sharedPreferences.edit().putString("all_test_decks", json).apply()
    }

    fun saveTestResult(testDeckId: String, score: Int, failedIds: List<String>) {
        val testWithQuestions = getTestDeck(testDeckId)
        if (testWithQuestions != null) {
            val updatedDeck = testWithQuestions.deck.copy(
                lastScore = score,
                failedQuestionIds = failedIds
            )
            saveTestDeck(TestDeckWithQuestions(updatedDeck, testWithQuestions.questions))
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
}
