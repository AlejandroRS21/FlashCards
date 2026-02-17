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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Error
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
import com.ramsalapps.flashcards.TestDeck
import com.ramsalapps.flashcards.ui.components.AppNavigationBar
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
    onTestDeckClick: (TestDeck) -> Unit = {},
    onDeckEdit: (Deck) -> Unit = {},
    onDeckDelete: (String) -> Unit = {},
    onReinforceClick: (TestDeck) -> Unit = {},
    decks: List<Deck> = emptyList(),
    testDecks: List<TestDeck> = emptyList(),
    recentSessions: List<Session> = emptyList(),
    streakDays: String = "0",
    masteredCards: String = "0",
    dailyGoalProgress: Float = 0f,
    userName: String = ""
) {
    Scaffold(
        contentWindowInsets = WindowInsets.systemBars,
        bottomBar = { 
            AppNavigationBar(
                currentScreen = "home",
                onImportClick = onImportClick,
                onSettingsClick = onSettingsClick
            )
        }
    ) { padding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .consumeWindowInsets(padding)
        ) {
            @Suppress("UNUSED_VARIABLE")
            val scope = this
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
                
                // --- Flashcard Decks Section ---
                item { SectionHeader(title = "Your Decks", action = "View All") }
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
                                    onEdit = { onDeckEdit(deck) },
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
                            modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp),
                            userScrollEnabled = false
                        ) {
                            items(decks.size) { index ->
                                DeckCard(
                                    decks[index],
                                    onClick = { onDeckClick(decks[index]) },
                                    onEdit = { onDeckEdit(decks[index]) },
                                    onDelete = { onDeckDelete(it) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }

                // --- Tests Section ---
                item { Spacer(modifier = Modifier.height(Spacing.lg)) }
                item { SectionHeader(title = "Practice Tests", action = null) }
                items(testDecks) { testDeck ->
                    TestDeckItem(
                        testDeck = testDeck,
                        onClick = { onTestDeckClick(testDeck) },
                        onReinforceClick = { onReinforceClick(testDeck) }
                    )
                }

                // --- Recent Activity ---
                item { Spacer(modifier = Modifier.height(Spacing.lg)) }
                item { SectionHeader(title = "Recent Activity", action = null) }
                items(recentSessions) { session ->
                    SessionItem(session)
                }
            }
        }
    }
}

@Composable
fun TestDeckItem(
    testDeck: TestDeck,
    onClick: () -> Unit,
    onReinforceClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(BorderRadius.md)
    ) {
        Column(modifier = Modifier.padding(Spacing.lg)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(BorderRadius.sm))
                        .background(PastelBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Text(testDeck.icon, fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(testDeck.name, fontWeight = FontWeight.Bold, color = TextDark)
                    Text("${testDeck.questionCount} Questions", fontSize = 12.sp, color = TextGray)
                }
                if (testDeck.lastScore != null) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            "${testDeck.lastScore}%",
                            fontWeight = FontWeight.Bold,
                            color = if (testDeck.lastScore >= 70) Color(0xFF4CAF50) else Color(0xFFFF6B6B),
                            fontSize = 18.sp
                        )
                        Text("Last Score", fontSize = 10.sp, color = TextGray)
                    }
                }
            }
            
            if (testDeck.failedQuestionIds.isNotEmpty()) {
                Spacer(modifier = Modifier.height(Spacing.md))
                Divider(color = Color(0xFFF0F0F0))
                Spacer(modifier = Modifier.height(Spacing.sm))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            tint = Color(0xFFFF6B6B),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "${testDeck.failedQuestionIds.size} mistakes to review",
                            fontSize = 12.sp,
                            color = Color(0xFFFF6B6B),
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Text(
                        "Reinforce",
                        modifier = Modifier
                            .clickable { onReinforceClick() }
                            .background(Color(0xFFFFEBEE), RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        color = Color(0xFFC62828),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
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
    onEdit: () -> Unit = {},
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
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = AccentBlue,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { onEdit() }
                    )
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color(0xFFFF6B6B),
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { showDeleteDialog = true }
                    )
                }
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

// Preview responsivos para diferentes tamaños de pantalla
@Preview(name = "Mobile", widthDp = 360, heightDp = 800)
@Composable
fun DashboardPreviewMobile() {
    FlashCardsTheme {
        DashboardScreen(
            onStartReview = {},
            onImportClick = {},
            onSettingsClick = {},
            userName = "Alex",
            streakDays = "7",
            masteredCards = "240",
            dailyGoalProgress = 0.75f,
            decks = listOf(
                Deck(name = "Medical Terms", cardCount = 120, progress = 80, icon = "🩺"),
                Deck(name = "Spanish 101", cardCount = 45, progress = 30, icon = "💃"),
                Deck(name = "History", cardCount = 75, progress = 50, icon = "📜")
            ),
            testDecks = listOf(
                TestDeck(name = "Anatomy Quiz", questionCount = 50, lastScore = 85, icon = "🦴"),
                TestDeck(name = "Driver's Test", questionCount = 30, lastScore = 60, icon = "🚗", failedQuestionIds = listOf("1", "2", "3"))
            ),
            recentSessions = listOf(
                Session("Medical Terms", "Yesterday", "15 mins", 12, "🩺"),
                Session("World History", "2 days ago", "10 mins", 5, "📜")
            )
        )
    }
}
