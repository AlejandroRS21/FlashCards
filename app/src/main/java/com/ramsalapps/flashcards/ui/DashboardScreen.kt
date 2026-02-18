package com.ramsalapps.flashcards.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramsalapps.flashcards.Deck
import com.ramsalapps.flashcards.R
import com.ramsalapps.flashcards.Session
import com.ramsalapps.flashcards.TestDeck
import com.ramsalapps.flashcards.ui.components.AppNavigationBar
import com.ramsalapps.flashcards.ui.theme.*
import com.ramsalapps.flashcards.ui.theme.Spacing
import com.ramsalapps.flashcards.ui.theme.BorderRadius
import androidx.compose.foundation.layout.BoxWithConstraints
import com.ramsalapps.flashcards.ui.theme.getResponsivePadding

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
        containerColor = Cream,
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
            val responsivePadding = getResponsivePadding(maxWidth)
            
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = responsivePadding, vertical = Spacing.sm),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                item { Header(userName) }
                item { DailyGoalCard(streakDays, masteredCards, dailyGoalProgress) }
                item { StartReviewButton(onStartReview) }
                
                // --- Flashcard Decks Section (List View) ---
                item { SectionHeader(title = stringResource(R.string.your_decks), action = stringResource(R.string.view_all)) }
                items(decks) { deck ->
                    DeckListItem(
                        deck = deck,
                        onClick = { onDeckClick(deck) },
                        onEdit = { onDeckEdit(deck) },
                        onDelete = { onDeckDelete(it) }
                    )
                }

                // --- Tests Section ---
                item { Spacer(modifier = Modifier.height(Spacing.xs)) }
                item { SectionHeader(title = stringResource(R.string.practice_tests), action = null) }
                items(testDecks) { testDeck ->
                    TestDeckItem(
                        testDeck = testDeck,
                        onClick = { onTestDeckClick(testDeck) },
                        onReinforceClick = { onReinforceClick(testDeck) }
                    )
                }

                // --- Recent Activity ---
                item { Spacer(modifier = Modifier.height(Spacing.xs)) }
                item { SectionHeader(title = stringResource(R.string.recent_activity), action = null) }
                items(recentSessions) { session ->
                    SessionItem(session)
                }
                
                item { Spacer(modifier = Modifier.height(Spacing.xl)) }
            }
        }
    }
}

@Composable
fun DeckListItem(
    deck: Deck,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: (String) -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(BorderRadius.md)
    ) {
        Row(
            modifier = Modifier.padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(BorderRadius.sm))
                    .background(PeachPuff),
                contentAlignment = Alignment.Center
            ) {
                Text(deck.icon, fontSize = 24.sp)
            }
            
            Spacer(modifier = Modifier.width(Spacing.md))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    deck.name,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 16.sp
                )
                Text(
                    stringResource(R.string.cards_count_label, deck.cardCount),
                    fontSize = 13.sp,
                    color = TextGray
                )
            }
            
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.edit), tint = PowderBlue, modifier = Modifier.size(20.dp))
                }
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete), tint = Color(0xFFFF6B6B), modifier = Modifier.size(20.dp))
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.delete_deck_q)) },
            text = { Text(stringResource(R.string.delete_confirm, deck.name)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete(deck.name)
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
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
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(BorderRadius.md)
    ) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(BorderRadius.sm))
                        .background(PowderBlue.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(testDeck.icon, fontSize = 24.sp)
                }
                Spacer(modifier = Modifier.width(Spacing.md))
                Column(modifier = Modifier.weight(1f)) {
                    Text(testDeck.name, fontWeight = FontWeight.Bold, color = TextDark, fontSize = 16.sp)
                    Text(stringResource(R.string.questions_count_label, testDeck.questionCount), fontSize = 13.sp, color = TextGray)
                }
                if (testDeck.lastScore != null) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            "${testDeck.lastScore}%",
                            fontWeight = FontWeight.Bold,
                            color = if (testDeck.lastScore >= 70) Color(0xFF4CAF50) else Color(0xFFFF6B6B),
                            fontSize = 18.sp
                        )
                        Text(stringResource(R.string.score_label), fontSize = 10.sp, color = TextGray)
                    }
                }
            }
            
            if (testDeck.failedQuestionIds.isNotEmpty()) {
                Spacer(modifier = Modifier.height(Spacing.sm))
                HorizontalDivider(color = Cream, thickness = 1.dp)
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
                            stringResource(R.string.mistakes, testDeck.failedQuestionIds.size),
                            fontSize = 12.sp,
                            color = Color(0xFFFF6B6B),
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Text(
                        stringResource(R.string.reinforce),
                        modifier = Modifier
                            .clickable { onReinforceClick() }
                            .background(MistyRose, RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        color = Color(0xFFC62828),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
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
            .padding(vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(PeachPuff),
                contentAlignment = Alignment.Center
            ) {
                Text("👤", fontSize = 24.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = if (userName.isNotEmpty()) stringResource(R.string.hi_user, userName) else stringResource(R.string.welcome_back),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
        }
        Icon(
            Icons.Default.Notifications,
            contentDescription = null,
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
                StatItem("🔥", stringResource(R.string.streak), stringResource(R.string.days, streak))
                VerticalDivider(modifier = Modifier.height(40.dp), color = Cream)
                StatItem("🏆", stringResource(R.string.mastered), stringResource(R.string.cards, mastered))
            }
            Spacer(modifier = Modifier.height(Spacing.lg))
            Text(stringResource(R.string.daily_goal), fontWeight = FontWeight.Bold, color = TextDark)
            Spacer(modifier = Modifier.height(Spacing.sm))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                color = PowderBlue,
                trackColor = Cream
            )
            Spacer(modifier = Modifier.height(Spacing.sm))
            Text(stringResource(R.string.keep_going), color = TextGray, fontSize = 14.sp)
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
            .padding(vertical = Spacing.md)
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(containerColor = PowderBlue),
        shape = RoundedCornerShape(BorderRadius.md)
    ) {
        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = TextDark)
        Spacer(modifier = Modifier.width(Spacing.sm))
        Text(stringResource(R.string.start_daily_review), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
    }
}

@Composable
fun SectionHeader(title: String, action: String?) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.xs),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        if (action != null) {
            Text(action, color = PowderBlue, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SessionItem(session: Session) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(BorderRadius.md)
    ) {
        Row(
            modifier = Modifier.padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(BorderRadius.sm))
                    .background(LemonChiffon),
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
