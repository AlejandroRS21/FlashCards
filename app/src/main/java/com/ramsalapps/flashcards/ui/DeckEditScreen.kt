package com.ramsalapps.flashcards.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramsalapps.flashcards.DataManager
import com.ramsalapps.flashcards.Deck
import com.ramsalapps.flashcards.Flashcard
import com.ramsalapps.flashcards.ui.theme.*

@Composable
fun DeckEditScreen(
    deck: Deck,
    onBack: () -> Unit,
    onDeckUpdate: (Deck) -> Unit
) {
    val context = LocalContext.current
    val dataManager = DataManager(context)

    var currentDeck by remember { mutableStateOf(deck) }
    var flashcards by remember { mutableStateOf(deck.flashcards) }
    var showAddDialog by remember { mutableStateOf(false) }
    var newQuestion by remember { mutableStateOf("") }
    var newAnswer by remember { mutableStateOf("") }
    var editingFlashcard by remember { mutableStateOf<Flashcard?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PastelPurple)
            .systemBarsPadding()
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
                modifier = Modifier.clickable { onBack() }
            )
            Text(
                "Edit Cards",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Button(
                onClick = {
                    val updatedDeck = currentDeck.copy(flashcards = flashcards)
                    dataManager.updateDeck(updatedDeck)
                    onDeckUpdate(updatedDeck)
                    onBack()
                },
                modifier = Modifier.height(40.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                Text("Done", color = Color.White, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Deck info
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(PastelGreen, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(currentDeck.icon, fontSize = 24.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(currentDeck.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("${flashcards.size} cards", color = TextGray, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Add new card button
        Button(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentPink),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add New Card", color = Color.White, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // List of flashcards
        Text("Cards (${flashcards.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(flashcards) { flashcard ->
                FlashcardEditItem(
                    flashcard = flashcard,
                    onEdit = {
                        editingFlashcard = flashcard
                        newQuestion = flashcard.question
                        newAnswer = flashcard.answer
                        showAddDialog = true
                    },
                    onDelete = {
                        flashcards = flashcards.filter { it.id != flashcard.id }
                        dataManager.deleteFlashcard(currentDeck.name, flashcard.id)
                    }
                )
            }
        }
    }

    // Add/Edit flashcard dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = {
                showAddDialog = false
                editingFlashcard = null
                newQuestion = ""
                newAnswer = ""
            },
            title = { Text(if (editingFlashcard == null) "Add Card" else "Edit Card") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newQuestion,
                        onValueChange = { newQuestion = it },
                        label = { Text("Question") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = newAnswer,
                        onValueChange = { newAnswer = it },
                        label = { Text("Answer") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newQuestion.isNotBlank() && newAnswer.isNotBlank()) {
                            if (editingFlashcard != null) {
                                // Update existing
                                val updatedFlashcard = editingFlashcard!!.copy(
                                    question = newQuestion,
                                    answer = newAnswer
                                )
                                flashcards = flashcards.map {
                                    if (it.id == editingFlashcard!!.id) updatedFlashcard else it
                                }
                                dataManager.updateFlashcard(currentDeck.name, editingFlashcard!!.id, updatedFlashcard)
                            } else {
                                // Add new
                                val newFlashcard = Flashcard(
                                    question = newQuestion,
                                    answer = newAnswer,
                                    category = ""
                                )
                                flashcards = flashcards + newFlashcard
                                dataManager.addFlashcard(currentDeck.name, newFlashcard)
                            }
                            showAddDialog = false
                            editingFlashcard = null
                            newQuestion = ""
                            newAnswer = ""
                        }
                    }
                ) {
                    Text(if (editingFlashcard == null) "Add" else "Update")
                }
            },
            dismissButton = {
                Button(onClick = {
                    showAddDialog = false
                    editingFlashcard = null
                    newQuestion = ""
                    newAnswer = ""
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun FlashcardEditItem(
    flashcard: Flashcard,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    flashcard.question,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    flashcard.answer,
                    fontSize = 12.sp,
                    color = TextGray,
                    maxLines = 1
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                Icons.Default.Edit,
                contentDescription = "Edit",
                tint = AccentBlue,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onEdit() }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                Icons.Default.Delete,
                contentDescription = "Delete",
                tint = Color(0xFFFF6B6B),
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onDelete() }
            )
        }
    }
}
