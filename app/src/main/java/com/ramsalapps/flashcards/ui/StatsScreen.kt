package com.ramsalapps.flashcards.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramsalapps.flashcards.Deck
import com.ramsalapps.flashcards.R
import com.ramsalapps.flashcards.UserStats
import com.ramsalapps.flashcards.ui.components.AppNavigationBar
import com.ramsalapps.flashcards.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    userStats: UserStats,
    decks: List<Deck>,
    onBack: () -> Unit,
    onHomeClick: () -> Unit,
    onImportClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        stringResource(R.string.stats_title), 
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp,
                        color = TextDark
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextDark)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Share functionality */ }) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = TextDark)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Cream)
            )
        },
        bottomBar = {
            AppNavigationBar(
                currentScreen = "stats",
                onHomeClick = onHomeClick,
                onImportClick = onImportClick,
                onStatsClick = {},
                onSettingsClick = onSettingsClick
            )
        },
        containerColor = Cream
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item { Spacer(modifier = Modifier.height(10.dp)) }

            // Resumen de tarjetas y tiempo
            item {
                val totalTime = userStats.totalStudyTimeMillis
                val hours = totalTime / 3600000
                val minutes = (totalTime % 3600000) / 60000
                val timeString = if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
                
                val diff = userStats.totalCardsReviewed - userStats.reviewsYesterday
                val diffPercent = if (userStats.reviewsYesterday > 0) (diff * 100) / userStats.reviewsYesterday else 0
                val diffString = if (diff >= 0) "+$diffPercent%" else "$diffPercent%"

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatSummaryCard(
                        title = stringResource(R.string.stats_cards),
                        value = String.format("%, d", userStats.totalCardsReviewed), 
                        subValue = stringResource(R.string.stats_vs_yesterday, diffString),
                        iconEmoji = "🎴",
                        modifier = Modifier.weight(1f)
                    )
                    StatSummaryCard(
                        title = stringResource(R.string.stats_time),
                        value = timeString,
                        subValue = stringResource(R.string.stats_total_study),
                        iconEmoji = "⏱️",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Actividad Semanal
            item {
                val totalWeekly = userStats.weeklyReviews.values.sum()
                // Simple calculation for mock comparison if real weekly data isn't complete
                WeeklyActivityCard(
                    totalCards = totalWeekly,
                    increasePercent = 12, // Placeholder or calculate
                    weeklyData = userStats.weeklyReviews
                )
            }

            // Rendimiento por Mazo
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            stringResource(R.string.stats_deck_performance),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = TextDark
                        )
                        TextButton(onClick = { /* View all */ }) {
                            Text(stringResource(R.string.view_all), color = TextGray, fontSize = 14.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    decks.sortedByDescending { it.progress }.take(5).forEach { deck ->
                        DeckPerformanceItem(deck)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
            
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
fun StatSummaryCard(
    title: String,
    value: String,
    subValue: String,
    iconEmoji: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(160.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(PeachPuff.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Text(iconEmoji, fontSize = 20.sp)
            }
            Column {
                Text(title, color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(value, color = TextDark, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                Text(subValue, color = if (subValue.contains("+")) Color(0xFF4CAF50) else if (subValue.contains("-")) Color(0xFFFF6B6B) else TextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun WeeklyActivityCard(totalCards: Int, increasePercent: Int, weeklyData: Map<Int, Int>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(stringResource(R.string.stats_weekly_activity), color = TextDark, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(totalCards.toString(), fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = TextDark)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.cards).lowercase(), fontSize = 14.sp, color = TextGray, modifier = Modifier.padding(bottom = 6.dp))
                    }
                }
                Surface(
                    color = Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.BarChart, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(14.dp))
                        Text(" $increasePercent%", color = Color(0xFF4CAF50), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Simple Bar Chart
            Row(
                modifier = Modifier.fillMaxWidth().height(100.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                val maxVal = (weeklyData.values.maxOrNull() ?: 1).coerceAtLeast(1)
                // Calendar.SUNDAY=1 ... SATURDAY=7. Reorder to L, M, X... (Mon=2, Tue=3, Wed=4, Thu=5, Fri=6, Sat=7, Sun=1)
                listOf(2, 3, 4, 5, 6, 7, 1).forEach { day ->
                    val value = weeklyData[day] ?: 0
                    val barHeight = (value.toFloat() / maxVal) * 80
                    Box(
                        modifier = Modifier
                            .width(12.dp)
                            .height(barHeight.dp.coerceAtLeast(4.dp))
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (value > 0) PowderBlue else Color(0xFFF5F5F5))
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("L", "M", "X", "J", "V", "S", "D").forEach { day ->
                    Text(day, color = TextGray, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun DeckPerformanceItem(deck: Deck) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(PeachPuff.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Text(deck.icon, fontSize = 24.sp)
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(deck.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
                    Text("${deck.progress}%", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = TextDark)
                }
                Text(
                    stringResource(R.string.stats_cards_mastered, (deck.cardCount * deck.progress) / 100),
                    fontSize = 12.sp,
                    color = TextGray
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { deck.progress.toFloat() / 100f },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                    color = PowderBlue,
                    trackColor = Color(0xFFF5F5F5)
                )
            }
        }
    }
}
