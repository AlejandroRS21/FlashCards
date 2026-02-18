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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.TextUnit
import com.ramsalapps.flashcards.DataManager
import com.ramsalapps.flashcards.Deck
import com.ramsalapps.flashcards.R
import com.ramsalapps.flashcards.ui.theme.*
import com.ramsalapps.flashcards.ui.theme.Spacing
import com.ramsalapps.flashcards.ui.theme.BorderRadius
import com.ramsalapps.flashcards.designsystem.components.DesignSystemButton
import com.ramsalapps.flashcards.designsystem.components.ButtonSize
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun StudySessionScreen(
    onClose: () -> Unit,
    deck: Deck? = null,
    totalCards: Int = 1,
    onDeckUpdate: (Deck) -> Unit = {},
    onCardMastered: (String) -> Unit = {}
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
        PeachPuff, MistyRose, LemonChiffon, Color(0xFFE0F7FA), PastelPurple, PastelGreen
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
        offsetX.animateTo(-1200f, animationSpec = tween(durationMillis = 400))
        offsetX.snapTo(0f)
    }

    suspend fun animateSwipePrevious() {
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
            .background(Cream)
            .systemBarsPadding()
            .padding(Spacing.xl)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = stringResource(R.string.close), tint = TextDark)
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    deck?.name ?: stringResource(R.string.study_session),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = TextDark
                )
                Spacer(modifier = Modifier.width(Spacing.sm))
                IconButton(onClick = { showEditDialog = true }, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.edit), tint = TextGray, modifier = Modifier.size(18.dp))
                }
            }
            IconButton(onClick = {
                currentCard?.let { onCardMastered(it.id) }
                goToNextWithAnimation()
            }) {
                Icon(Icons.Default.CheckCircle, contentDescription = "Mastered", tint = Color(0xFF4CAF50))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(stringResource(R.string.session_progress), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
            Text("${currentIndex + 1} / $displayTotal", color = TextGray, fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.height(Spacing.sm))
        LinearProgressIndicator(
            progress = { if (displayTotal > 0) (currentIndex + 1).toFloat() / displayTotal else 0f },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
            color = PowderBlue,
            trackColor = Color.White
        )

        Spacer(modifier = Modifier.height(40.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
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
                                translationY = stackIndex * 12f
                                translationX = if (isFrontCard) offsetX.value else 0f
                                scaleX = 1f - (stackIndex * 0.05f) - (if (isFrontCard) abs(offsetX.value) / 1200f * 0.1f else 0f)
                                scaleY = 1f - (stackIndex * 0.05f) - (if (isFrontCard) abs(offsetX.value) / 1200f * 0.1f else 0f)
                                rotationZ = if (isFrontCard) (offsetX.value / 1200f) * 15f else 0f
                                alpha = if (isFrontCard) (1f - (abs(offsetX.value) / 1200f) * 0.3f) else (1f - stackIndex * 0.1f)
                                if (isFrontCard) {
                                    rotationY = flipRotation
                                    cameraDistance = 12f * density
                                }
                            }
                            .clickable(enabled = isFrontCard) { if (isFrontCard) isFlipped = !isFlipped }
                            .pointerInput(isFlipped) {
                                if (isFrontCard) {
                                    detectHorizontalDragGestures(
                                        onDragEnd = {
                                            if (abs(offsetX.value) > 100) {
                                                scope.launch {
                                                    val direction = if (isFlipped) -offsetX.value else offsetX.value
                                                    if (direction > 0) goToPreviousWithAnimation() else goToNextWithAnimation()
                                                }
                                            } else {
                                                scope.launch { offsetX.animateTo(0f, animationSpec = tween(durationMillis = 200)) }
                                            }
                                        }
                                    ) { change, dragAmount ->
                                        change.consume()
                                        scope.launch { 
                                            val adjustedDrag = if (isFlipped) -dragAmount else dragAmount
                                            offsetX.snapTo(offsetX.value + adjustedDrag) 
                                        }
                                    }
                                }
                            },
                        shape = RoundedCornerShape(BorderRadius.lg),
                        colors = CardDefaults.cardColors(
                            containerColor = cardBackgroundColors[cardIndex % cardBackgroundColors.size]
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isFrontCard) 4.dp else 1.dp)
                    ) {
                        if (isFrontCard) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .graphicsLayer { if (flipRotation > 90f) rotationY = 180f }
                                    .padding(Spacing.xxl),
                                contentAlignment = Alignment.Center
                            ) {
                                val textToShow = if (flipRotation <= 90f) card.question else card.answer
                                val fontSize = if (flipRotation <= 90f) calculateFontSizeForQuestion(textToShow) else calculateFontSizeForAnswer(textToShow)
                                
                                if (bionicReadingEnabled) {
                                    BionicText(
                                        text = textToShow,
                                        fontSize = fontSize,
                                        color = TextDark,
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )
                                } else {
                                    Text(
                                        text = textToShow,
                                        fontSize = fontSize,
                                        color = TextDark,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 32.sp
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
            if (isFlipped) stringResource(R.string.tap_to_see_question) else stringResource(R.string.tap_to_see_answer),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = TextGray,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(32.dp))

        if (hasFlashcards && displayTotal > 0) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DesignSystemButton(
                    onClick = { goToPreviousWithAnimation() },
                    text = stringResource(R.string.previous),
                    size = ButtonSize.Medium,
                    enabled = currentIndex > 0,
                    modifier = Modifier.weight(1f)
                )

                DesignSystemButton(
                    onClick = { goToNextWithAnimation() },
                    text = stringResource(R.string.next),
                    size = ButtonSize.Medium,
                    enabled = currentIndex < displayTotal - 1,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { shuffleCards() },
                    modifier = Modifier.weight(1f).height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LemonChiffon),
                    shape = RoundedCornerShape(BorderRadius.md)
                ) {
                    Icon(Icons.Default.Casino, contentDescription = stringResource(R.string.shuffle), tint = TextDark)
                    Spacer(modifier = Modifier.width(Spacing.sm))
                    Text(stringResource(R.string.shuffle), fontWeight = FontWeight.Bold, color = TextDark)
                }

                Button(
                    onClick = { resetToOriginal() },
                    modifier = Modifier.weight(1f).height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MistyRose),
                    shape = RoundedCornerShape(BorderRadius.md)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = stringResource(R.string.reset), tint = TextDark)
                    Spacer(modifier = Modifier.width(Spacing.sm))
                    Text(stringResource(R.string.reset), fontWeight = FontWeight.Bold, color = TextDark)
                }
            }
        }
    }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text(stringResource(R.string.edit_deck_name)) },
            text = {
                OutlinedTextField(
                    value = editedDeckName,
                    onValueChange = { editedDeckName = it },
                    label = { Text(stringResource(R.string.deck_name)) },
                    shape = RoundedCornerShape(BorderRadius.md),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (editedDeckName.isNotBlank() && deck != null) {
                            onDeckUpdate(deck.copy(name = editedDeckName))
                            showEditDialog = false
                        }
                    }
                ) { Text(stringResource(R.string.save), color = PowderBlue, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) { Text(stringResource(R.string.cancel), color = TextGray) }
            }
        )
    }
}

fun calculateFontSizeForQuestion(text: String): TextUnit = when {
    text.length > 200 -> 16.sp
    text.length > 150 -> 18.sp
    text.length > 100 -> 22.sp
    text.length > 60 -> 26.sp
    else -> 28.sp
}

fun calculateFontSizeForAnswer(text: String): TextUnit = when {
    text.length > 200 -> 14.sp
    text.length > 150 -> 16.sp
    text.length > 100 -> 18.sp
    text.length > 60 -> 20.sp
    else -> 22.sp
}
