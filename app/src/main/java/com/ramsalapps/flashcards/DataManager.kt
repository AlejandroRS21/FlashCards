package com.ramsalapps.flashcards

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class DeckWithFlashcards(
    val deck: Deck,
    val flashcards: List<Flashcard>
)

class DataManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("flashcards_db", Context.MODE_PRIVATE)
    private val gson = Gson()

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

        // Para cada deck, cargar sus flashcards desde el almacenamiento
        return decksList.map { deck ->
            val deckWithFlashcards = getDeck(deck.name)
            if (deckWithFlashcards != null) {
                deck.copy(flashcards = deckWithFlashcards.flashcards)
            } else {
                deck
            }
        }
    }

    fun getAllDecksWithFlashcards(): List<DeckWithFlashcards> {
        val decksList = getAllDecks()
        return decksList.mapNotNull { getDeck(it.name) }
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
}
