package com.ramsalapps.flashcards.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
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
import com.ramsalapps.flashcards.ui.theme.Spacing
import com.ramsalapps.flashcards.ui.theme.BorderRadius
import androidx.compose.foundation.layout.BoxWithConstraints
import com.ramsalapps.flashcards.ui.theme.getDeviceType
import com.ramsalapps.flashcards.ui.theme.getResponsivePadding
import com.ramsalapps.flashcards.ui.theme.DeviceType

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
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val responsivePadding = getResponsivePadding(maxWidth)
            val deviceType = getDeviceType(maxWidth)
            
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = responsivePadding, vertical = Spacing.sm)
            ) {
                item { Header(userName) }
                item { DailyGoalCard(streakDays, masteredCards, dailyGoalProgress) }
                item { StartReviewButton(onStartReview) }
                item { SectionHeader(title = "Recent Decks", action = null) }
                items(recentSessions) { session ->
                    SessionItem(session)
                }
                item { SectionHeader(title = "All Decks", action = "View All") }
                // Usar 'items' directamente en LazyColumn para decks
                if (deviceType == DeviceType.MOBILE) {
                    item {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(Spacing.lg),
                            contentPadding = PaddingValues(vertical = Spacing.sm),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(decks) { deck ->
                                DeckCard(
                                    deck,
                                    onClick = { onDeckClick(deck) },
                                    onDelete = { onDeckDelete(it) },
                                    modifier = Modifier.width(160.dp)
                                )
                            }
                        }
                    }
                } else {
                    item {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(if (deviceType == DeviceType.TABLET) 2 else 3),
                            horizontalArrangement = Arrangement.spacedBy(Spacing.lg),
                            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
                            modifier = Modifier.fillMaxWidth(),
                            userScrollEnabled = false
                        ) {
                            items(decks.size) { index ->
                                DeckCard(
                                    decks[index],
                                    onClick = { onDeckClick(decks[index]) },
                                    onDelete = { onDeckDelete(it) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
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
            .padding(vertical = Spacing.xl),
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
        shape = RoundedCornerShape(BorderRadius.lg)
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
            Spacer(modifier = Modifier.height(Spacing.lg))
            Text("Daily Goal", fontWeight = FontWeight.Bold, color = TextDark)
            Spacer(modifier = Modifier.height(Spacing.sm))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                color = AccentBlue,
                trackColor = Color(0xFFF0F0F0)
            )
            Spacer(modifier = Modifier.height(Spacing.sm))
            BionicText("Keep going! You're doing great.", color = TextGray, fontSize = 14.sp)
        }
    }
}

@Composable
fun StatItem(emoji: String, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(emoji, fontSize = 24.sp)
        Spacer(modifier = Modifier.width(Spacing.sm))
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
        shape = RoundedCornerShape(BorderRadius.md)
    ) {
        Icon(Icons.Default.PlayArrow, contentDescription = null)
        Spacer(modifier = Modifier.width(Spacing.sm))
        Text("Start Daily Review", fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun SectionHeader(title: String, action: String?) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.md),
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
fun DeckCard(
    deck: Deck,
    onClick: () -> Unit = {},
    onDelete: (String) -> Unit = {},
    modifier: Modifier = Modifier.width(160.dp)
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(BorderRadius.lg)
    ) {
        Column(modifier = Modifier.padding(Spacing.lg)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Spacing.sm),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(BorderRadius.sm))
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
                    .clip(RoundedCornerShape(BorderRadius.sm))
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
        shape = RoundedCornerShape(BorderRadius.md)
    ) {
        Row(
            modifier = Modifier.padding(Spacing.lg),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(BorderRadius.sm))
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

// Preview responsivos para diferentes tamaños de pantalla
@Preview(name = "Mobile", widthDp = 360, heightDp = 800)
@Composable
fun DashboardPreviewMobile() {
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
                Deck(name = "Spanish 101", cardCount = 45, progress = 30, icon = "💃"),
                Deck(name = "History", cardCount = 75, progress = 50, icon = "📜")
            ),
            recentSessions = listOf(
                Session("Medical Terms", "Yesterday", "15 mins", 12, "🩺"),
                Session("World History", "2 days ago", "10 mins", 5, "📜")
            )
        )
    }
}

@Preview(name = "Tablet", widthDp = 600, heightDp = 1000)
@Composable
fun DashboardPreviewTablet() {
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
                Deck(name = "Spanish 101", cardCount = 45, progress = 30, icon = "💃"),
                Deck(name = "History", cardCount = 75, progress = 50, icon = "📜"),
                Deck(name = "Biology", cardCount = 60, progress = 45, icon = "🧬")
            ),
            recentSessions = listOf(
                Session("Medical Terms", "Yesterday", "15 mins", 12, "🩺"),
                Session("World History", "2 days ago", "10 mins", 5, "📜")
            )
        )
    }
}

@Preview(name = "Desktop", widthDp = 1200, heightDp = 800)
@Composable
fun DashboardPreviewDesktop() {
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
                Deck(name = "Spanish 101", cardCount = 45, progress = 30, icon = "💃"),
                Deck(name = "History", cardCount = 75, progress = 50, icon = "📜"),
                Deck(name = "Biology", cardCount = 60, progress = 45, icon = "🧬"),
                Deck(name = "Chemistry", cardCount = 85, progress = 70, icon = "⚗️"),
                Deck(name = "Physics", cardCount = 95, progress = 55, icon = "⚛️")
            ),
            recentSessions = listOf(
                Session("Medical Terms", "Yesterday", "15 mins", 12, "🩺"),
                Session("World History", "2 days ago", "10 mins", 5, "📜")
            )
        )
    }
}

