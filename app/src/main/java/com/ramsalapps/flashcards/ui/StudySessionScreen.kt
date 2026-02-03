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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.TextUnit
import com.ramsalapps.flashcards.DataManager
import com.ramsalapps.flashcards.Deck
import com.ramsalapps.flashcards.ui.theme.*
import com.ramsalapps.flashcards.ui.theme.Spacing
import com.ramsalapps.flashcards.ui.theme.BorderRadius
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun StudySessionScreen(
    onClose: () -> Unit,
    deck: Deck? = null,
    totalCards: Int = 1,
    onDeckUpdate: (Deck) -> Unit = {}
) {
    val context = LocalContext.current
    val dataManager = DataManager(context)

    val originalFlashcards = deck?.flashcards ?: emptyList()
    val hasFlashcards = originalFlashcards.isNotEmpty()

    var currentIndex by remember { mutableStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editedDeckName by remember { mutableStateOf(deck?.name ?: "") }
    var flashcards by remember { mutableStateOf(originalFlashcards) }
    var bionicReadingEnabled by remember { mutableStateOf(dataManager.isBionicReadingEnabled()) }

    val currentCard = if (hasFlashcards && currentIndex < flashcards.size) flashcards[currentIndex] else null
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

    fun shuffleCards() {
        flashcards = flashcards.shuffled()
        currentIndex = 0
        isFlipped = false
    }

    fun resetToOriginal() {
        flashcards = originalFlashcards
        currentIndex = 0
        isFlipped = false
    }

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
            .systemBarsPadding()
            .padding(Spacing.xl)
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
                Spacer(modifier = Modifier.width(Spacing.sm))
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit name",
                    tint = TextDark,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { showEditDialog = true }
                )
            }
            Icon(Icons.Default.Info, contentDescription = "Stats", tint = TextDark)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Session Progress", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text("${currentIndex + 1} / $displayTotal cards", color = TextGray, fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.height(Spacing.sm))
        LinearProgressIndicator(
            progress = { if (displayTotal > 0) (currentIndex + 1).toFloat() / displayTotal else 0f },
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
                        shape = RoundedCornerShape(BorderRadius.lg),
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
                                    .padding(Spacing.xxl),
                                contentAlignment = Alignment.Center
                            ) {
                                if (flipRotation <= 90f) {
                                    if (bionicReadingEnabled) {
                                        BionicText(
                                            text = card.question,
                                            fontSize = calculateFontSizeForQuestion(card.question),
                                            color = TextDark,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    } else {
                                        Text(
                                            text = card.question,
                                            fontSize = calculateFontSizeForQuestion(card.question),
                                            color = TextDark,
                                            textAlign = TextAlign.Center,
                                            lineHeight = 32.sp
                                        )
                                    }
                                } else {
                                    if (bionicReadingEnabled) {
                                        BionicText(
                                            text = card.answer,
                                            fontSize = calculateFontSizeForAnswer(card.answer),
                                            color = TextDark,
                                            modifier = Modifier.fillMaxWidth(),
                                            textAlign = if (card.answer.split(" ").size < 3) TextAlign.Center else TextAlign.Center
                                        )
                                    } else {
                                        Text(
                                            text = card.answer,
                                            fontSize = calculateFontSizeForAnswer(card.answer),
                                            color = TextDark,
                                            textAlign = if (card.answer.split(" ").size < 3) TextAlign.Center else TextAlign.Center,
                                            lineHeight = 32.sp
                                        )
                                    }
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(Spacing.xxl),
                                contentAlignment = Alignment.Center
                            ) {
                                if (bionicReadingEnabled) {
                                    BionicText(
                                        text = card.question,
                                        fontSize = (calculateFontSizeForQuestion(card.question).value * 0.75).sp,
                                        color = TextDark.copy(alpha = 0.7f),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                } else {
                                    Text(
                                        text = card.question,
                                        fontSize = (calculateFontSizeForQuestion(card.question).value * 0.75).sp,
                                        color = TextDark.copy(alpha = 0.7f),
                                        textAlign = TextAlign.Center,
                                        lineHeight = 28.sp
                                    )
                                }
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
                    shape = RoundedCornerShape(BorderRadius.md)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous")
                    Spacer(modifier = Modifier.width(Spacing.sm))
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
                    shape = RoundedCornerShape(BorderRadius.md)
                ) {
                    Text("Next")
                    Spacer(modifier = Modifier.width(Spacing.sm))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { shuffleCards() },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF6B9D)  // Rosa vibrante
                    ),
                    shape = RoundedCornerShape(BorderRadius.lg)
                ) {
                    Icon(Icons.Default.Casino, contentDescription = "Shuffle", tint = Color.White)
                    Spacer(modifier = Modifier.width(Spacing.sm))
                    Text("Shuffle", fontWeight = FontWeight.Bold, color = Color.White)
                }

                Button(
                    onClick = { resetToOriginal() },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFA500)  // Naranja vibrante
                    ),
                    shape = RoundedCornerShape(BorderRadius.lg)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = Color.White)
                    Spacer(modifier = Modifier.width(Spacing.sm))
                    Text("Reset", fontWeight = FontWeight.Bold, color = Color.White)
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
                    shape = RoundedCornerShape(BorderRadius.md),
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

// Función auxiliar para calcular tamaño dinámico de fuente
fun calculateFontSizeForQuestion(text: String): TextUnit {
    return when {
        text.length > 200 -> 16.sp   // Texto muy largo
        text.length > 150 -> 18.sp   // Texto largo
        text.length > 100 -> 22.sp   // Texto medio-largo
        text.length > 60 -> 26.sp    // Texto medio
        else -> 28.sp                 // Texto corto
    }
}

// Función auxiliar para calcular tamaño dinámico de fuente para respuestas
fun calculateFontSizeForAnswer(text: String): TextUnit {
    return when {
        text.length > 200 -> 14.sp   // Texto muy largo
        text.length > 150 -> 16.sp   // Texto largo
        text.length > 100 -> 18.sp   // Texto medio-largo
        text.length > 60 -> 20.sp    // Texto medio
        else -> 22.sp                 // Texto corto
    }
}

@Preview(showBackground = true)
@Composable
fun StudySessionPreview() {
    FlashCardsTheme {
        StudySessionScreen(
            onClose = {},
            totalCards = 50
        )
    }
}

