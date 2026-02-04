package com.ramsalapps.flashcards

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.ramsalapps.flashcards.ui.DashboardScreen
import com.ramsalapps.flashcards.ui.DeckEditScreen
import com.ramsalapps.flashcards.ui.ImportScreen
import com.ramsalapps.flashcards.ui.SettingsScreen
import com.ramsalapps.flashcards.ui.StudySessionScreen
import com.ramsalapps.flashcards.ui.theme.FlashCardsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlashCardsTheme {
                var currentScreen by remember { mutableStateOf(Screen.Dashboard) }
                var decks by remember { mutableStateOf(emptyList<Deck>()) }
                var selectedDeck by remember { mutableStateOf<Deck?>(null) }

                // Cargar decks al iniciar
                LaunchedEffect(Unit) {
                    val dataManager = DataManager(this@MainActivity)
                    decks = dataManager.getAllDecks()
                }

                when (currentScreen) {
                    Screen.Dashboard -> DashboardScreen(
                        onStartReview = { currentScreen = Screen.Study },
                        onImportClick = { currentScreen = Screen.Import },
                        onSettingsClick = { currentScreen = Screen.Settings },
                        onDeckClick = { deck ->
                            selectedDeck = deck
                            currentScreen = Screen.Study
                        },
                        onDeckEdit = { deck ->
                            selectedDeck = deck
                            currentScreen = Screen.DeckEdit
                        },
                        onDeckDelete = { deckName ->
                            val dataManager = DataManager(this@MainActivity)
                            dataManager.deleteDeck(deckName)
                            decks = dataManager.getAllDecks()
                        },
                        decks = decks
                    )
                    Screen.Study -> StudySessionScreen(
                        onClose = {
                            currentScreen = Screen.Dashboard
                            // Recargar decks en caso de que se hayan actualizado
                            val dataManager = DataManager(this@MainActivity)
                            decks = dataManager.getAllDecks()
                        },
                        deck = selectedDeck,
                        onDeckUpdate = { updatedDeck ->
                            // Actualizar el deck en la base de datos
                            val dataManager = DataManager(this@MainActivity)
                            dataManager.updateDeck(updatedDeck)

                            // Actualizar la lista de decks
                            decks = dataManager.getAllDecks()
                            selectedDeck = updatedDeck
                        }
                    )
                    Screen.Import -> ImportScreen(
                        onBack = { currentScreen = Screen.Dashboard },
                        onDeckCreated = {
                            // Recargar decks después de crear
                            val dataManager = DataManager(this@MainActivity)
                            decks = dataManager.getAllDecks()
                        }
                    )
                    Screen.Settings -> SettingsScreen(
                        onBack = { currentScreen = Screen.Dashboard },
                        onImportClick = { currentScreen = Screen.Import }
                    )
                    Screen.DeckEdit -> DeckEditScreen(
                        deck = selectedDeck ?: Deck(id = "", name = "Untitled", cardCount = 0, progress = 0),
                        onBack = { currentScreen = Screen.Dashboard },
                        onDeckUpdate = { updatedDeck ->
                            val dataManager = DataManager(this@MainActivity)
                            dataManager.updateDeck(updatedDeck)
                            decks = dataManager.getAllDecks()
                            selectedDeck = updatedDeck
                        }
                    )
                }
            }
        }
    }
}

enum class Screen {
    Dashboard, Study, Import, Settings, DeckEdit
}
