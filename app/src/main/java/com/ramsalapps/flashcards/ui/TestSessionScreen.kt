package com.ramsalapps.flashcards.ui

import android.media.AudioManager
import android.media.ToneGenerator
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramsalapps.flashcards.DataManager
import com.ramsalapps.flashcards.TestDeck
import com.ramsalapps.flashcards.TestQuestion
import com.ramsalapps.flashcards.ui.theme.*

@Composable
fun TestSessionScreen(
    onClose: () -> Unit,
    testDeck: TestDeck,
    reinforceMode: Boolean = false,
    onTestComplete: (Int, List<String>) -> Unit
) {
    val context = LocalContext.current
    val dataManager = remember { DataManager(context) }
    val toneGenerator = remember { ToneGenerator(AudioManager.STREAM_MUSIC, 100) }
    val soundEnabled = remember { dataManager.isSoundEffectsEnabled() }
    
    val questions = remember(testDeck.id, reinforceMode) {
        val baseQuestions = dataManager.getTestDeck(testDeck.id)?.questions ?: emptyList()
        if (reinforceMode) {
            baseQuestions.filter { it.id in testDeck.failedQuestionIds }.shuffled()
        } else {
            baseQuestions.shuffled()
        }
    }

    var currentIndex by remember { mutableStateOf(0) }
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var isAnswered by remember { mutableStateOf(false) }
    var score by remember { mutableStateOf(0) }
    val failedIds = remember { mutableStateListOf<String>() }
    var showResults by remember { mutableStateOf(false) }

    if (questions.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize().background(PastelBlue), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("No questions to review.", color = TextDark, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onClose) { Text("Back to Dashboard") }
            }
        }
        return
    }

    if (showResults) {
        val totalQuestions = questions.size
        TestResultsView(
            score = score,
            total = totalQuestions,
            onFinish = {
                if (totalQuestions > 0) onTestComplete((score * 100) / totalQuestions, failedIds.toList())
                onClose()
            }
        )
        return
    }

    val currentQuestion = questions[currentIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PastelBlue)
            .systemBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp) 
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = TextDark)
            }
            Text(
                "Question ${currentIndex + 1}/${questions.size}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextDark
            )
            Spacer(modifier = Modifier.width(48.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Question Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = currentQuestion.question,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Select the most accurate answer from the list below.",
                    fontSize = 14.sp,
                    color = TextGray,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Options scrollable to avoid deformation
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            currentQuestion.options.forEachIndexed { index, option ->
                if (option.isNotBlank()) {
                    val isSelected = selectedOptionIndex == index
                    val isCorrect = index == currentQuestion.correctAnswerIndex
                    
                    val backgroundColor = when {
                        isAnswered && isCorrect -> Color(0xFFE8F5E9)
                        isAnswered && isSelected && !isCorrect -> Color(0xFFFFEBEE)
                        else -> Color.White
                    }
                    
                    val borderColor = when {
                        isAnswered && isCorrect -> Color(0xFF4CAF50)
                        isAnswered && isSelected && !isCorrect -> Color(0xFFFF6B6B)
                        else -> Color.Transparent
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, if (isSelected && !isAnswered) AccentBlue else borderColor, RoundedCornerShape(16.dp))
                            .clickable(enabled = !isAnswered) { selectedOptionIndex = index },
                        colors = CardDefaults.cardColors(containerColor = backgroundColor),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xFFE3F2FD), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${(65 + index).toChar()}",
                                    color = AccentBlue,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = option,
                                fontSize = 16.sp,
                                color = TextDark,
                                modifier = Modifier.weight(1f)
                            )
                            if (isAnswered) {
                                if (isCorrect) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF4CAF50))
                                } else if (isSelected) {
                                    Icon(Icons.Default.Cancel, contentDescription = null, tint = Color(0xFFFF6B6B))
                                }
                            }
                        }
                    }
                }
            }
            
            if (isAnswered && currentQuestion.explanation.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = if (selectedOptionIndex == currentQuestion.correctAnswerIndex) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            if (selectedOptionIndex == currentQuestion.correctAnswerIndex) Icons.Default.CheckCircle else Icons.Default.Cancel, 
                            contentDescription=null,
                            tint = if (selectedOptionIndex == currentQuestion.correctAnswerIndex) Color(0xFF2E7D32) else Color(0xFFC62828)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = currentQuestion.explanation, color = TextDark, fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (!isAnswered) {
                    isAnswered = true
                    if (selectedOptionIndex == currentQuestion.correctAnswerIndex) {
                        score++
                        if (soundEnabled) toneGenerator.startTone(ToneGenerator.TONE_PROP_ACK, 200)
                    } else {
                        failedIds.add(currentQuestion.id)
                        if (soundEnabled) toneGenerator.startTone(ToneGenerator.TONE_PROP_NACK, 200)
                    }
                } else {
                    if (currentIndex < questions.size - 1) {
                        currentIndex++
                        isAnswered = false
                        selectedOptionIndex = null
                    } else {
                        showResults = true
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = selectedOptionIndex != null,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (!isAnswered) AccentBlue else Color(0xFF4CAF50)
            )
        ) {
            Text(
                if (!isAnswered) "Check Answer" 
                else if (currentIndex < questions.size - 1) "Next Question" 
                else "View Results",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun TestResultsView(score: Int, total: Int, onFinish: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PastelPurple)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(32.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Test Completed!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Spacer(modifier = Modifier.height(32.dp))
                
                Box(contentAlignment = Alignment.Center) {
                    val progress = if (total > 0) score.toFloat() / total else 0f
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.size(160.dp),
                        color = if (progress >= 0.7) Color(0xFF4CAF50) else if (progress >= 0.4) Color(0xFFFB8C00) else Color(0xFFFF6B6B),
                        strokeWidth = 12.dp,
                        trackColor = Color(0xFFF0F0F0)
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            if (total > 0) "${(score * 100) / total}%" else "0%",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextDark
                        )
                        Text("$score / $total", color = TextGray, fontSize = 16.sp)
                    }
                }
                
                Spacer(modifier = Modifier.height(40.dp))
                
                val feedback = when {
                    total == 0 -> "No questions were in this test."
                    score.toFloat() / total >= 0.9 -> "Excellent! You're a master!"
                    score.toFloat() / total >= 0.7 -> "Great job! Keep it up!"
                    score.toFloat() / total >= 0.5 -> "Good effort! A bit more study will help."
                    else -> "Don't give up! Review your mistakes and try again."
                }
                
                Text(
                    text = feedback,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                    color = TextGray,
                    fontSize = 16.sp
                )
                
                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = onFinish,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Back to Dashboard", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}
