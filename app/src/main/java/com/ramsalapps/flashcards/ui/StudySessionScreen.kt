package com.ramsalapps.flashcards.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramsalapps.flashcards.ui.theme.*

@Composable
fun StudySessionScreen(onClose: () -> Unit) {
    var isFlipped by remember { mutableStateOf(false) }
    var currentCardIndex by remember { mutableIntStateOf(0) }

    val cardBackgroundColors = listOf(
        Color.White,
        PastelPink,
        PastelBlue,
        PastelGreen,
        PastelYellow,
        PastelPurple
    )
    
    // Cambia de color cada 3 tarjetas
    val currentCardColor = cardBackgroundColors[(currentCardIndex / 3) % cardBackgroundColors.size]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PastelPurple)
            .padding(24.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Close",
                tint = TextDark,
                modifier = Modifier.clickable { onClose() }
            )
            Text("Bionic Study Session", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Icon(Icons.Default.List, contentDescription = "Stats", tint = TextDark)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Progress
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Session Progress", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text("${currentCardIndex + 1} / 50 cards", color = TextGray, fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = (currentCardIndex + 1) / 50f,
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
            color = AccentBlue,
            trackColor = Color.White.copy(alpha = 0.5f)
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Flashcard (Text-only with dynamic background)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clickable { isFlipped = !isFlipped },
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = currentCardColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                if (!isFlipped) {
                    // Front: Question
                    BionicText(
                        text = "¿Qué ciencia estudia el tratamiento automático de la información?",
                        fontSize = 28.sp,
                        color = TextDark,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    // Back: Answer
                    Text(
                        text = "La informática, que procede de la fusión de las palabras información y automática.",
                        fontSize = 22.sp,
                        color = TextDark, // Usamos TextDark para mejor legibilidad en fondos pastel
                        textAlign = TextAlign.Center,
                        lineHeight = 32.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            if (isFlipped) "Tap to see question" else "Tap to see answer",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = TextGray
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Feedback Buttons (Only visible on back side)
        if (isFlipped) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val onNext = {
                    currentCardIndex++
                    isFlipped = false
                }
                FeedbackButton("Again", "< 1m", PastelPink, modifier = Modifier.weight(1f).clickable { onNext() })
                FeedbackButton("Hard", "2d", PastelYellow, modifier = Modifier.weight(1f).clickable { onNext() })
                FeedbackButton("Good", "4d", PastelGreen, modifier = Modifier.weight(1f).clickable { onNext() })
                FeedbackButton("Easy", "7d", PastelBlue, modifier = Modifier.weight(1f).clickable { onNext() })
            }
        } else {
            // Placeholder to maintain layout
            Box(modifier = Modifier.height(80.dp).fillMaxWidth())
        }
    }
}

@Composable
fun FeedbackButton(label: String, time: String, bgColor: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, fontWeight = FontWeight.Bold, color = TextDark)
        Text(time, fontSize = 12.sp, color = TextGray)
    }
}

@Preview(showBackground = true)
@Composable
fun StudySessionPreview() {
    FlashCardsTheme {
        StudySessionScreen(onClose = {})
    }
}
