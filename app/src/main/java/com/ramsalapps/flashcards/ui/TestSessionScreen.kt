package com.ramsalapps.flashcards.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
    val dataManager = DataManager(context)
    
    // Shuffle questions once at start
    val questions = remember(testDeck.id) {
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
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("No questions found.")
                Button(onClick = onClose) { Text("Go Back") }
            }
        }
        return
    }

    if (showResults) {
        TestResultsView(
            score = score,
            total = questions.size,
            onFinish = {
                onTestComplete((score * 100) / questions.size, failedIds.toList())
                onClose()
            }
        )
        return
    }

    val currentQuestion = questions[currentIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .systemBarsPadding()
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close")
            }
            Text(
                "Question ${currentIndex + 1}/${questions.size}",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.width(48.dp))
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Question Card
        Card(
            modifier = Modifier.fillMaxWidth().weight(0.4f),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                Text(
                    text = currentQuestion.question,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = TextDark
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Options
        Column(modifier = Modifier.weight(0.6f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            currentQuestion.options.forEachIndexed { index, option ->
                val isSelected = selectedOptionIndex == index
                val isCorrect = index == currentQuestion.correctAnswerIndex
                
                val backgroundColor = when {
                    !isAnswered && isSelected -> Color(0xFFE3F2FD)
                    isAnswered && isCorrect -> Color(0xFFE8F5E9)
                    isAnswered && isSelected && !isCorrect -> Color(0xFFFFEBEE)
                    else -> Color.White
                }
                
                val borderColor = when {
                    !isAnswered && isSelected -> AccentBlue
                    isAnswered && isCorrect -> Color(0xFF4CAF50)
                    isAnswered && isSelected && !isCorrect -> Color(0xFFFF6B6B)
                    else -> Color.Transparent
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, borderColor, RoundedCornerShape(16.dp))
                        .clickable(enabled = !isAnswered) { selectedOptionIndex = index },
                    colors = CardDefaults.cardColors(containerColor = backgroundColor),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${(65 + index).toChar()}",
                            modifier = Modifier
                                .size(32.dp)
                                .background(if (isSelected || (isAnswered && isCorrect)) borderColor else Color(0xFFF0F0F0), RoundedCornerShape(8.dp))
                                .wrapContentSize(Alignment.Center),
                            color = if (isSelected || (isAnswered && isCorrect)) Color.White else TextGray,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = option, fontSize = 16.sp, color = TextDark)
                    }
                }
            }
        }

        if (isAnswered && currentQuestion.explanation.isNotEmpty()) {
            Text(
                text = currentQuestion.explanation,
                color = TextGray,
                fontSize = 12.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (!isAnswered) {
                    isAnswered = true
                    if (selectedOptionIndex == currentQuestion.correctAnswerIndex) {
                        score++
                    } else {
                        failedIds.add(currentQuestion.id)
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
            colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
        ) {
            Text(if (!isAnswered) "Check" else if (currentIndex < questions.size - 1) "Next" else "Finish")
        }
    }
}

@Composable
fun TestResultsView(score: Int, total: Int, onFinish: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Test Completed!", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Box(modifier = Modifier.size(150.dp).background(PastelBlue, CircleShape), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("${(score * 100) / total}%", fontSize = 36.sp, fontWeight = FontWeight.ExtraBold, color = AccentBlue)
                Text("$score / $total", color = TextGray)
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onFinish, modifier = Modifier.fillMaxWidth().height(56.dp)) {
            Text("Back to Dashboard")
        }
    }
}
