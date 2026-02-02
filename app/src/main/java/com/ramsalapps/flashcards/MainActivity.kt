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

                when (currentScreen) {
                    Screen.Dashboard -> DashboardScreen(
                        onStartReview = { currentScreen = Screen.Study },
                        onImportClick = { currentScreen = Screen.Import }
                    )
                    Screen.Study -> StudySessionScreen(
                        onClose = { currentScreen = Screen.Dashboard }
                    )
                    Screen.Import -> ImportScreen(
                        onBack = { currentScreen = Screen.Dashboard }
                    )
                }
            }
        }
    }
}

enum class Screen {
    Dashboard, Study, Import
}
