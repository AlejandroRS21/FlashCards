package com.ramsalapps.flashcards

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.ramsalapps.flashcards.ui.DashboardScreen
import com.ramsalapps.flashcards.ui.ImportScreen
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
                        onDeckClick = { deck ->
                            selectedDeck = deck
                            currentScreen = Screen.Study
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
                }
            }
        }
    }
}

enum class Screen {
    Dashboard, Study, Import
}
