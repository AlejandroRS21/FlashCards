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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
    decks: List<Deck> = emptyList(),
    recentSessions: List<Session> = emptyList(),
    streakDays: String = "0",
    masteredCards: String = "0",
    dailyGoalProgress: Float = 0f,
    userName: String = ""
) {
    Scaffold(
        bottomBar = { BottomNavigationBar(onLibraryClick = onImportClick) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            item { Header(userName) }
            item { DailyGoalCard(streakDays, masteredCards, dailyGoalProgress) }
            item { StartReviewButton(onStartReview) }
            item { SectionHeader(title = "My Decks", action = "View All") }
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(decks) { deck ->
                        DeckCard(deck)
                    }
                }
            }
            item { SectionHeader(title = "Recent Sessions", action = null) }
            items(recentSessions) { session ->
                SessionItem(session)
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
                Divider(modifier = Modifier.height(40.dp).width(1.dp), color = Color.LightGray)
                StatItem("🏆", "MASTERED", "$mastered Cards")
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text("Daily Goal", fontWeight = FontWeight.Bold, color = TextDark)
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = progress,
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
fun DeckCard(deck: Deck) {
    Card(
        modifier = Modifier.width(160.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (deck.name.contains("Medical")) PastelGreen else PastelPurple),
                contentAlignment = Alignment.Center
            ) {
                Text(deck.icon, fontSize = 40.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(deck.name, fontWeight = FontWeight.Bold, color = TextDark)
            Text("${deck.cardCount} cards • ${deck.progress}%", fontSize = 12.sp, color = TextGray)
        }
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
fun BottomNavigationBar(onLibraryClick: () -> Unit) {
    NavigationBar(containerColor = Color.White) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, null) },
            label = { Text("Home") },
            selected = true,
            onClick = {}
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.List, null) },
            label = { Text("Library") },
            selected = false,
            onClick = onLibraryClick
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Info, null) },
            label = { Text("Stats") },
            selected = false,
            onClick = {}
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Settings, null) },
            label = { Text("Settings") },
            selected = false,
            onClick = {}
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
                Deck("Medical Terms", 120, 80, "🩺"),
                Deck("Spanish 101", 45, 30, "💃")
            ),
            recentSessions = listOf(
                Session("Medical Terms", "Yesterday", "15 mins", 12, "🩺"),
                Session("World History", "2 days ago", "10 mins", 5, "📜")
            )
        )
    }
}
