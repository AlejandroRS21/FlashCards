package com.ramsalapps.flashcards.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramsalapps.flashcards.ui.theme.*

@Composable
fun StudySessionScreen(
    onClose: () -> Unit,
    question: String = "",
    answer: String = "",
    currentCardIndex: Int = 0,
    totalCards: Int = 1
) {
    var isFlipped by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "cardFlip"
    )

    val cardBackgroundColors = listOf(
        Color.White,
        PastelPink,
        PastelBlue,
        PastelGreen,
        PastelYellow,
        PastelPurple
    )
    
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
            Text("${currentCardIndex + 1} / $totalCards cards", color = TextGray, fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = if (totalCards > 0) (currentCardIndex + 1).toFloat() / totalCards else 0f,
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
            color = AccentBlue,
            trackColor = Color.White.copy(alpha = 0.5f)
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Flashcard with Flip Animation
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .graphicsLayer {
                    rotationY = rotation
                    cameraDistance = 12f * density
                }
                .clickable { isFlipped = !isFlipped },
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = currentCardColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        // Evita que el texto se vea al revés cuando la tarjeta está girada
                        if (rotation > 90f) {
                            rotationY = 180f
                        }
                    }
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                if (rotation <= 90f) {
                    // Front: Question
                    BionicText(
                        text = question,
                        fontSize = 28.sp,
                        color = TextDark,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    // Back: Answer
                    Text(
                        text = answer,
                        fontSize = 22.sp,
                        color = TextDark,
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
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun StudySessionPreview() {
    FlashCardsTheme {
        StudySessionScreen(
            onClose = {},
            question = "¿Qué ciencia estudia el tratamiento automático de la información?",
            answer = "La informática, que procede de la fusión de las palabras información y automática.",
            currentCardIndex = 0,
            totalCards = 50
        )
    }
}
