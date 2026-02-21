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
import com.ramsalapps.flashcards.ui.StatsScreen
import com.ramsalapps.flashcards.ui.theme.FlashCardsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlashCardsTheme {
                var currentScreen by remember { mutableStateOf<Screen>(Screen.Dashboard) }
                var decks by remember { mutableStateOf(emptyList<Deck>()) }
                var testDecks by remember { mutableStateOf(emptyList<TestDeck>()) }
                var selectedDeck by remember { mutableStateOf<Deck?>(null) }
                var selectedTestDeck by remember { mutableStateOf<TestDeck?>(null) }
                var isReinforceMode by remember { mutableStateOf(false) }
                var userStats by remember { mutableStateOf(UserStats()) }

                val dataManager = remember { DataManager(this@MainActivity) }

                fun refreshData() {
                    decks = dataManager.getAllDecks().sortedByDescending { it.lastModified }
                    testDecks = dataManager.getAllTestDecks().sortedByDescending { it.lastModified }
                    userStats = dataManager.getUserStats()
                }

                LaunchedEffect(Unit) { refreshData() }

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
                            refreshData()
                        },
                        onStatsClick = { currentScreen = Screen.Stats },
                        decks = decks,
                        testDecks = testDecks,
                        userStats = userStats
                    )
                    Screen.Stats -> StatsScreen(
                        userStats = userStats,
                        decks = decks,
                        onBack = { currentScreen = Screen.Dashboard },
                        onHomeClick = { currentScreen = Screen.Dashboard },
                        onImportClick = { currentScreen = Screen.Import },
                        onSettingsClick = { currentScreen = Screen.Settings }
                    )
                    Screen.Study -> StudySessionScreen(
                        onClose = {
                            currentScreen = Screen.Dashboard
                            refreshData()
                        },
                        deck = selectedDeck,
                        onDeckUpdate = { updatedDeck ->
                            dataManager.updateDeck(updatedDeck)
                            refreshData()
                            selectedDeck = updatedDeck
                        },
                        onCardMastered = { cardId ->
                            selectedDeck?.let { deck ->
                                dataManager.markCardAsMastered(deck.name, cardId)
                                dataManager.updateDailyProgress()
                                refreshData()
                            }
                        },
                        onSaveSessionState = { state ->
                            selectedDeck?.let { deck ->
                                dataManager.updateDeck(deck.copy(sessionState = state))
                            }
                        }
                    )
                    Screen.TestSession -> {
                        selectedTestDeck?.let { testDeck ->
                            TestSessionScreen(
                                onClose = { 
                                    currentScreen = Screen.Dashboard 
                                    refreshData()
                                },
                                testDeck = testDeck,
                                reinforceMode = isReinforceMode,
                                onTestComplete = { score, failedIds ->
                                    dataManager.saveTestResult(testDeck.id, score, failedIds)
                                    dataManager.updateDailyProgress()
                                    refreshData()
                                }
                            )
                        }
                    }
                    Screen.Import -> ImportScreen(
                        onBack = { currentScreen = Screen.Dashboard },
                        onDeckCreated = { refreshData() },
                        onHomeClick = { currentScreen = Screen.Dashboard },
                        onSettingsClick = { currentScreen = Screen.Settings }
                    )
                    Screen.Settings -> SettingsScreen(
                        onBack = { currentScreen = Screen.Dashboard },
                        onImportClick = { currentScreen = Screen.Import },
                        onHomeClick = { currentScreen = Screen.Dashboard },
                        onStatsClick = { currentScreen = Screen.Stats }
                    )
                    Screen.DeckEdit -> DeckEditScreen(
                        deck = selectedDeck ?: Deck(name = "Untitled", cardCount = 0),
                        onBack = { currentScreen = Screen.Dashboard },
                        onDeckUpdate = { updatedDeck ->
                            dataManager.updateDeck(updatedDeck)
                            refreshData()
                            selectedDeck = updatedDeck
                        }
                    )
                }
            }
        }
    }
}

enum class Screen {
    Dashboard, Study, Import, Settings, DeckEdit, TestSession, Stats
}
