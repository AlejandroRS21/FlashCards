package com.ramsalapps.flashcards.data

import com.ramsalapps.flashcards.Deck
import com.ramsalapps.flashcards.Flashcard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object FlashcardRepository {
    private val _decks = MutableStateFlow<List<Deck>>(
        listOf(
            Deck("Medical Terms", 120, 80, "🩺"),
            Deck("Spanish 101", 45, 30, "💃")
        )
    )
    val decks: StateFlow<List<Deck>> = _decks.asStateFlow()
    
    private val _flashcards = MutableStateFlow<Map<String, List<Flashcard>>>(emptyMap())
    val flashcards: StateFlow<Map<String, List<Flashcard>>> = _flashcards.asStateFlow()
    
    fun addDeck(name: String, flashcards: List<Flashcard>, icon: String = "📚"): Boolean {
        // Check if deck with this name already exists
        if (_decks.value.any { it.name.equals(name, ignoreCase = true) }) {
            return false // Deck name already exists
        }
        
        val newDeck = Deck(
            name = name,
            cardCount = flashcards.size,
            progress = 0,
            icon = icon
        )
        _decks.value = _decks.value + newDeck
        _flashcards.value = _flashcards.value + (name to flashcards)
        return true
    }
    
    fun getFlashcardsForDeck(deckName: String): List<Flashcard> {
        return _flashcards.value[deckName] ?: emptyList()
    }
}
