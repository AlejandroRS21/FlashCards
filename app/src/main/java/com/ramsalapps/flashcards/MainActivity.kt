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
import com.ramsalapps.flashcards.ui.TestSessionScreen
import com.ramsalapps.flashcards.ui.theme.FlashCardsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlashCardsTheme {
                var currentScreen by remember { mutableStateOf(Screen.Dashboard) }
                var decks by remember { mutableStateOf(emptyList<Deck>()) }
                var testDecks by remember { mutableStateOf(emptyList<TestDeck>()) }
                var selectedDeck by remember { mutableStateOf<Deck?>(null) }
                var selectedTestDeck by remember { mutableStateOf<TestDeck?>(null) }
                var isReinforceMode by remember { mutableStateOf(false) }

                val dataManager = remember { DataManager(this@MainActivity) }

                // Cargar datos al iniciar
                LaunchedEffect(Unit) {
                    decks = dataManager.getAllDecks()
                    testDecks = dataManager.getAllTestDecks()
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
                        onTestDeckClick = { test ->
                            selectedTestDeck = test
                            isReinforceMode = false
                            currentScreen = Screen.TestSession
                        },
                        onReinforceClick = { test ->
                            selectedTestDeck = test
                            isReinforceMode = true
                            currentScreen = Screen.TestSession
                        },
                        onDeckEdit = { deck ->
                            selectedDeck = deck
                            currentScreen = Screen.DeckEdit
                        },
                        onDeckDelete = { deckName ->
                            dataManager.deleteDeck(deckName)
                            decks = dataManager.getAllDecks()
                        },
                        decks = decks,
                        testDecks = testDecks
                    )
                    Screen.Study -> StudySessionScreen(
                        onClose = {
                            currentScreen = Screen.Dashboard
                            decks = dataManager.getAllDecks()
                        },
                        deck = selectedDeck,
                        onDeckUpdate = { updatedDeck ->
                            dataManager.updateDeck(updatedDeck)
                            decks = dataManager.getAllDecks()
                            selectedDeck = updatedDeck
                        }
                    )
                    Screen.TestSession -> {
                        selectedTestDeck?.let { testDeck ->
                            TestSessionScreen(
                                onClose = { 
                                    currentScreen = Screen.Dashboard 
                                    testDecks = dataManager.getAllTestDecks()
                                },
                                testDeck = testDeck,
                                reinforceMode = isReinforceMode,
                                onTestComplete = { score, failedIds ->
                                    dataManager.saveTestResult(testDeck.id, score, failedIds)
                                    testDecks = dataManager.getAllTestDecks()
                                }
                            )
                        }
                    }
                    Screen.Import -> ImportScreen(
                        onBack = { currentScreen = Screen.Dashboard },
                        onDeckCreated = {
                            decks = dataManager.getAllDecks()
                            testDecks = dataManager.getAllTestDecks()
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
    Dashboard, Study, Import, Settings, DeckEdit, TestSession
}
