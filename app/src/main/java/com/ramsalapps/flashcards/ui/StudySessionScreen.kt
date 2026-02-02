package com.ramsalapps.flashcards.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramsalapps.flashcards.Deck
import com.ramsalapps.flashcards.Flashcard
import com.ramsalapps.flashcards.ui.theme.*
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun StudySessionScreen(
    onClose: () -> Unit,
    deck: Deck? = null,
    question: String = "",
    answer: String = "",
    currentCardIndex: Int = 0,
    totalCards: Int = 1,
    onDeckUpdate: (Deck) -> Unit = {}
) {
    val flashcards = deck?.flashcards ?: emptyList()
    val hasFlashcards = flashcards.isNotEmpty()

    var currentIndex by remember { mutableStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editedDeckName by remember { mutableStateOf(deck?.name ?: "") }

    val currentCard = if (hasFlashcards && currentIndex < flashcards.size) flashcards[currentIndex] else null
    val displayQuestion = currentCard?.question ?: question
    val displayAnswer = currentCard?.answer ?: answer
    val displayTotal = if (hasFlashcards) flashcards.size else totalCards

    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val flipRotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "cardFlip"
    )

    val cardBackgroundColors = listOf(
        Color(0xFFFFE4E1), // Misty Rose
        Color(0xFFE8D4F1), // Lavender Blush
        Color(0xFFD4E8F7), // Alice Blue
        Color(0xFFD4F1E8), // Mint Cream
        Color(0xFFFFFACD), // Lemon Chiffon
        Color(0xFFFFE4B5)  // Moccasin
    )

    val currentCardColor = cardBackgroundColors[(currentIndex / 3) % cardBackgroundColors.size]

    suspend fun animateSwipeNext() {
        // Swipe para siguiente: se desliza a la izquierda (offset negativo)
        offsetX.animateTo(-1200f, animationSpec = tween(durationMillis = 400))
        offsetX.snapTo(0f)
    }

    suspend fun animateSwipePrevious() {
        // Swipe para anterior: se desliza a la derecha (offset positivo)
        offsetX.animateTo(1200f, animationSpec = tween(durationMillis = 400))
        offsetX.snapTo(0f)
    }

    fun goToNextWithAnimation() {
        if (currentIndex < displayTotal - 1) {
            scope.launch {
                animateSwipeNext()
                currentIndex++
                isFlipped = false
            }
        }
    }

    fun goToPreviousWithAnimation() {
        if (currentIndex > 0) {
            scope.launch {
                animateSwipePrevious()
                currentIndex--
                isFlipped = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PastelPurple)
            .padding(24.dp)
    ) {
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    deck?.name ?: "Bionic Study Session",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit name",
                    tint = TextDark,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { showEditDialog = true }
                )
            }
            Icon(Icons.Default.List, contentDescription = "Stats", tint = TextDark)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Session Progress", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text("${currentIndex + 1} / $displayTotal cards", color = TextGray, fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = if (displayTotal > 0) (currentIndex + 1).toFloat() / displayTotal else 0f,
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
            color = AccentBlue,
            trackColor = Color.White.copy(alpha = 0.5f)
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Stack de cartas (baraja)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            // Mostrar las próximas 3 cartas apiladas (efecto baraja)
            // Dibujar primero las cartas de atrás, luego la frontal encima
            for (stackIndex in 2 downTo 0) {
                val cardIndex = currentIndex + stackIndex
                if (cardIndex < flashcards.size) {
                    val card = flashcards[cardIndex]
                    val isFrontCard = stackIndex == 0

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .graphicsLayer {
                                // Posicionamiento escalonado para efecto baraja
                                translationY = stackIndex * 12f
                                translationX = if (isFrontCard) offsetX.value else 0f
                                scaleX = 1f - (stackIndex * 0.05f) - (if (isFrontCard) abs(offsetX.value) / 1200f * 0.1f else 0f)
                                scaleY = 1f - (stackIndex * 0.05f) - (if (isFrontCard) abs(offsetX.value) / 1200f * 0.1f else 0f)
                                rotationZ = if (isFrontCard) (offsetX.value / 1200f) * 15f else 0f
                                alpha = if (isFrontCard) (1f - (abs(offsetX.value) / 1200f) * 0.3f) else (1f - stackIndex * 0.1f)
                                // Flip animation solo para la tarjeta frontal
                                if (isFrontCard) {
                                    rotationY = flipRotation
                                    cameraDistance = 12f * density
                                }
                            }
                            .clickable(enabled = isFrontCard) { if (isFrontCard) isFlipped = !isFlipped }
                            .pointerInput(Unit) {
                                if (isFrontCard) {
                                    detectHorizontalDragGestures(
                                        onDragStart = {},
                                        onDragEnd = {
                                            if (abs(offsetX.value) > 100) {
                                                scope.launch {
                                                    if (offsetX.value > 0) {
                                                        goToPreviousWithAnimation()
                                                    } else {
                                                        goToNextWithAnimation()
                                                    }
                                                }
                                            } else {
                                                scope.launch {
                                                    offsetX.animateTo(0f, animationSpec = tween(durationMillis = 200))
                                                }
                                            }
                                        },
                                        onDragCancel = {
                                            scope.launch {
                                                offsetX.animateTo(0f, animationSpec = tween(durationMillis = 200))
                                            }
                                        }
                                    ) { change, dragAmount ->
                                        change.consume()
                                        scope.launch {
                                            offsetX.snapTo(offsetX.value + dragAmount)
                                        }
                                    }
                                }
                            },
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = cardBackgroundColors[cardIndex % cardBackgroundColors.size]
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isFrontCard) 4.dp else 2.dp)
                    ) {
                        if (isFrontCard) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .graphicsLayer {
                                        if (flipRotation > 90f) {
                                            rotationY = 180f
                                        }
                                    }
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (flipRotation <= 90f) {
                                    BionicText(
                                        text = card.question,
                                        fontSize = 28.sp,
                                        color = TextDark,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                } else {
                                    Text(
                                        text = card.answer,
                                        fontSize = 22.sp,
                                        color = TextDark,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 32.sp
                                    )
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                BionicText(
                                    text = card.question,
                                    fontSize = 22.sp,
                                    color = TextDark.copy(alpha = 0.7f),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
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
        Text(
            "Swipe left for next • Swipe right for previous",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = TextGray,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(40.dp))

        if (hasFlashcards && displayTotal > 1) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { goToPreviousWithAnimation() },
                    enabled = currentIndex > 0,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentBlue,
                        disabledContainerColor = Color.LightGray
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Previous")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Previous")
                }

                Button(
                    onClick = { goToNextWithAnimation() },
                    enabled = currentIndex < displayTotal - 1,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentBlue,
                        disabledContainerColor = Color.LightGray
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Next")
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = "Next")
                }
            }
        }
    }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Deck Name") },
            text = {
                OutlinedTextField(
                    value = editedDeckName,
                    onValueChange = { editedDeckName = it },
                    label = { Text("Deck Name") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editedDeckName.isNotBlank() && deck != null) {
                            val updatedDeck = deck.copy(name = editedDeckName)
                            onDeckUpdate(updatedDeck)
                            showEditDialog = false
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                Button(onClick = { showEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
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
