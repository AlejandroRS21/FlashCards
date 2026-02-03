package com.ramsalapps.flashcards.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramsalapps.flashcards.Deck
import com.ramsalapps.flashcards.Session
import com.ramsalapps.flashcards.ui.theme.*

@Composable
fun DashboardScreen(
    onStartReview: () -> Unit,
    onImportClick: () -> Unit,
    onSettingsClick: () -> Unit = {},
    onDeckClick: (Deck) -> Unit = {},
    onDeckDelete: (String) -> Unit = {},
    decks: List<Deck> = emptyList(),
    recentSessions: List<Session> = emptyList(),
    streakDays: String = "0",
    masteredCards: String = "0",
    dailyGoalProgress: Float = 0f,
    userName: String = ""
) {
    Scaffold(
        bottomBar = { BottomNavigationBar(onLibraryClick = onImportClick, onSettingsClick = onSettingsClick, currentScreen = "home") }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            item { Header(userName) }
            item { DailyGoalCard(streakDays, masteredCards, dailyGoalProgress) }
            item { StartReviewButton(onStartReview) }
            item { SectionHeader(title = "Recent Decks", action = null) }
            items(recentSessions) { session ->
                SessionItem(session)
            }
            item { SectionHeader(title = "All Decks", action = "View All") }
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(decks) { deck ->
                        DeckCard(deck, onClick = { onDeckClick(deck) }, onDelete = { onDeckDelete(it) })
                    }
                }
            }
        }
    }
}

@Composable
fun Header(userName: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(AccentPink),
                contentAlignment = Alignment.Center
            ) {
                Text("👤", fontSize = 24.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = if (userName.isNotEmpty()) "Good morning, $userName!" else "Good morning!",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
        }
        Icon(
            Icons.Default.Notifications,
            contentDescription = "Notifications",
            tint = TextDark,
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
fun DailyGoalCard(streak: String, mastered: String, progress: Float) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem("🔥", "STREAK", "$streak Days")
                VerticalDivider(modifier = Modifier.height(40.dp), color = Color.LightGray)
                StatItem("🏆", "MASTERED", "$mastered Cards")
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text("Daily Goal", fontWeight = FontWeight.Bold, color = TextDark)
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                color = AccentBlue,
                trackColor = Color(0xFFF0F0F0)
            )
            Spacer(modifier = Modifier.height(8.dp))
            BionicText("Keep going! You're doing great.", color = TextGray, fontSize = 14.sp)
        }
    }
}

@Composable
fun StatItem(emoji: String, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(emoji, fontSize = 24.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = TextGray)
            Text(value, fontWeight = FontWeight.Bold, color = TextDark)
        }
    }
}

@Composable
fun StartReviewButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp)
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
        shape = RoundedCornerShape(12.dp)
    ) {
        Icon(Icons.Default.PlayArrow, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Start Daily Review", fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun SectionHeader(title: String, action: String?) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        if (action != null) {
            Text(action, color = AccentBlue, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun DeckCard(deck: Deck, onClick: () -> Unit = {}, onDelete: (String) -> Unit = {}) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (deck.name.contains("Medical")) PastelGreen else PastelPurple),
                    contentAlignment = Alignment.Center
                ) {
                    Text(deck.icon, fontSize = 16.sp)
                }
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color(0xFFFF6B6B),
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { showDeleteDialog = true }
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (deck.name.contains("Medical")) PastelGreen else PastelPurple),
                contentAlignment = Alignment.Center
            ) {
                Text(deck.icon, fontSize = 28.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                deck.name,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontSize = 14.sp
            )
            Text("${deck.cardCount} cards • ${deck.progress}%", fontSize = 12.sp, color = TextGray, maxLines = 1)
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Deck?") },
            text = { Text("Are you sure you want to delete \"${deck.name}\"? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete(deck.name)
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B6B))
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                Button(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SessionItem(session: Session) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(PastelGreen),
                contentAlignment = Alignment.Center
            ) {
                Text(session.icon)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(session.deckName, fontWeight = FontWeight.Bold, color = TextDark)
                Text("${session.date} • ${session.duration}", fontSize = 12.sp, color = TextGray)
            }
            Text("+${session.improvement}%", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun BottomNavigationBar(
    onLibraryClick: () -> Unit,
    onSettingsClick: () -> Unit = {},
    currentScreen: String = "home"
) {
    NavigationBar(containerColor = Color.White) {
        // Home
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Home,
                    null,
                    tint = if (currentScreen == "home") AccentBlue else TextGray
                )
            },
            label = { Text("Home") },
            selected = currentScreen == "home",
            onClick = {}
        )
        // Import
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.CloudUpload,
                    null,
                    tint = if (currentScreen == "import") AccentBlue else TextGray
                )
            },
            label = { Text("Import") },
            selected = currentScreen == "import",
            onClick = onLibraryClick
        )
        // Stats
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.BarChart,
                    null,
                    tint = if (currentScreen == "stats") AccentBlue else TextGray
                )
            },
            label = { Text("Stats") },
            selected = currentScreen == "stats",
            onClick = {}
        )
        // Settings
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Settings,
                    null,
                    tint = if (currentScreen == "settings") AccentBlue else TextGray
                )
            },
            label = { Text("Settings") },
            selected = currentScreen == "settings",
            onClick = { onSettingsClick() }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    FlashCardsTheme {
        DashboardScreen(
            onStartReview = {},
            onImportClick = {},
            userName = "Alex",
            streakDays = "7",
            masteredCards = "240",
            dailyGoalProgress = 0.75f,
            decks = listOf(
                Deck(name = "Medical Terms", cardCount = 120, progress = 80, icon = "🩺"),
                Deck(name = "Spanish 101", cardCount = 45, progress = 30, icon = "💃")
            ),
            recentSessions = listOf(
                Session("Medical Terms", "Yesterday", "15 mins", 12, "🩺"),
                Session("World History", "2 days ago", "10 mins", 5, "📜")
            )
        )
    }
}
