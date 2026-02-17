package com.ramsalapps.flashcards.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.height
import com.ramsalapps.flashcards.ui.theme.AccentBlue
import com.ramsalapps.flashcards.ui.theme.TextGray
import androidx.compose.ui.tooling.preview.Preview
import com.ramsalapps.flashcards.ui.theme.FlashCardsTheme

/**
 * Componente reutilizable de barra de navegación inferior
 * 
 * @param currentScreen Pantalla actualmente seleccionada ("home", "import", "stats", "settings")
 * @param onHomeClick Callback cuando se toca el botón Home
 * @param onImportClick Callback cuando se toca el botón Import
 * @param onStatsClick Callback cuando se toca el botón Stats
 * @param onSettingsClick Callback cuando se toca el botón Settings
 */
@Composable
fun AppNavigationBar(
    currentScreen: String = "home",
    onHomeClick: () -> Unit = {},
    onImportClick: () -> Unit = {},
    onStatsClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    NavigationBar(
        containerColor = Color.White
    ) {
        // Home
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Home,
                    contentDescription = "Home",
                    tint = if (currentScreen == "home") AccentBlue else TextGray
                )
            },
            label = { Text("Home") },
            selected = currentScreen == "home",
            onClick = onHomeClick
        )
        
        // Import
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.CloudUpload,
                    contentDescription = "Import",
                    tint = if (currentScreen == "import") AccentBlue else TextGray
                )
            },
            label = { Text("Import") },
            selected = currentScreen == "import",
            onClick = onImportClick
        )
        
        // Stats
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.BarChart,
                    contentDescription = "Stats",
                    tint = if (currentScreen == "stats") AccentBlue else TextGray
                )
            },
            label = { Text("Stats") },
            selected = currentScreen == "stats",
            onClick = onStatsClick
        )
        
        // Settings
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = if (currentScreen == "settings") AccentBlue else TextGray
                )
            },
            label = { Text("Settings") },
            selected = currentScreen == "settings",
            onClick = onSettingsClick
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AppNavigationBarPreview() {
    FlashCardsTheme {
        AppNavigationBar(currentScreen = "home")
    }
}
