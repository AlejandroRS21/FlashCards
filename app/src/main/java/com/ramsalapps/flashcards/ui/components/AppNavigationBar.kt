package com.ramsalapps.flashcards.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ramsalapps.flashcards.R
import com.ramsalapps.flashcards.ui.theme.AccentBlue
import com.ramsalapps.flashcards.ui.theme.TextGray
import androidx.compose.ui.tooling.preview.Preview
import com.ramsalapps.flashcards.ui.theme.FlashCardsTheme

@Composable
fun AppNavigationBar(
    currentScreen: String = "home",
    onHomeClick: () -> Unit = {},
    onImportClick: () -> Unit = {},
    onStatsClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp
    ) {
        // Home
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_nav_home),
                    contentDescription = null
                )
            },
            label = { Text(stringResource(R.string.nav_home)) },
            selected = currentScreen == "home",
            onClick = onHomeClick,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AccentBlue,
                unselectedIconColor = TextGray,
                selectedTextColor = AccentBlue,
                unselectedTextColor = TextGray,
                indicatorColor = Color.Transparent
            )
        )
        
        // Mazos (Import functionality usually or list)
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_nav_decks),
                    contentDescription = null
                )
            },
            label = { Text(stringResource(R.string.nav_decks)) },
            selected = currentScreen == "import",
            onClick = onImportClick,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AccentBlue,
                unselectedIconColor = TextGray,
                selectedTextColor = AccentBlue,
                unselectedTextColor = TextGray,
                indicatorColor = Color.Transparent
            )
        )
        
        // Progreso (Stats)
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_nav_stats),
                    contentDescription = null
                )
            },
            label = { Text(stringResource(R.string.nav_progress)) },
            selected = currentScreen == "stats",
            onClick = onStatsClick,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AccentBlue,
                unselectedIconColor = TextGray,
                selectedTextColor = AccentBlue,
                unselectedTextColor = TextGray,
                indicatorColor = Color.Transparent
            )
        )
        
        // Perfil (Settings)
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null
                )
            },
            label = { Text(stringResource(R.string.nav_profile)) },
            selected = currentScreen == "settings",
            onClick = onSettingsClick,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AccentBlue,
                unselectedIconColor = TextGray,
                selectedTextColor = AccentBlue,
                unselectedTextColor = TextGray,
                indicatorColor = Color.Transparent
            )
        )
    }
}
