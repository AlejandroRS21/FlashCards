package com.ramsalapps.flashcards.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramsalapps.flashcards.Flashcard
import com.ramsalapps.flashcards.data.FlashcardRepository
import com.ramsalapps.flashcards.ui.theme.*
import java.io.BufferedReader
import java.io.InputStreamReader

@Composable
fun ImportScreen(onBack: () -> Unit) {
    var importedFlashcards by remember { mutableStateOf<List<Flashcard>>(emptyList()) }
    var selectedFileName by remember { mutableStateOf<String?>(null) }
    var showNameDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val context = LocalContext.current
    
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(it)
                val reader = BufferedReader(InputStreamReader(inputStream))
                val flashcards = mutableListOf<Flashcard>()
                
                reader.useLines { lines ->
                    lines.forEach { line ->
                        if (line.isNotBlank()) {
                            val parts = line.split(",", limit = 2)
                            if (parts.size == 2) {
                                val question = parts[0].trim()
                                val answer = parts[1].trim()
                                if (question.isNotEmpty() && answer.isNotEmpty()) {
                                    flashcards.add(Flashcard(question, answer, "Imported"))
                                }
                            }
                        }
                    }
                }
                
                if (flashcards.isEmpty()) {
                    errorMessage = "No valid flashcards found in the CSV file. Expected format: Question, Answer"
                    showErrorDialog = true
                } else {
                    importedFlashcards = flashcards
                    selectedFileName = it.lastPathSegment ?: "flashcards.csv"
                }
            } catch (e: Exception) {
                errorMessage = "Error reading file: ${e.message}"
                showErrorDialog = true
            }
        }
    }
    
    if (showNameDialog) {
        DeckNameDialog(
            onDismiss = { showNameDialog = false },
            onConfirm = { deckName ->
                FlashcardRepository.addDeck(deckName, importedFlashcards)
                showNameDialog = false
                onBack()
            },
            cardCount = importedFlashcards.size
        )
    }
    
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text("Import Error") },
            text = { Text(errorMessage) },
            confirmButton = {
                TextButton(onClick = { showErrorDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Back",
                    modifier = Modifier.clickable { onBack() }
                )
                Text(
                    "Import Flashcards",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.width(24.dp))
            }
        },
        bottomBar = {
            Button(
                onClick = { 
                    if (importedFlashcards.isNotEmpty()) {
                        showNameDialog = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentPink),
                shape = RoundedCornerShape(28.dp),
                enabled = importedFlashcards.isNotEmpty()
            ) {
                Text("Finalize Import", color = TextDark, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item { 
                UploadZone(
                    onBrowseClick = { filePickerLauncher.launch("text/*") },
                    selectedFileName = selectedFileName,
                    cardCount = importedFlashcards.size
                ) 
            }
            if (importedFlashcards.isNotEmpty()) {
                item { 
                    Text(
                        "Preview (${importedFlashcards.size} flashcards)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ) 
                }
                items(importedFlashcards.take(5)) { flashcard ->
                    FlashcardPreviewItem(flashcard)
                }
                if (importedFlashcards.size > 5) {
                    item {
                        Text(
                            "... and ${importedFlashcards.size - 5} more flashcards",
                            color = TextGray,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            item { BionicReadingToggle() }
            item { FormattingGuide() }
            item { RecentImportsSection() }
        }
    }
}

@Composable
fun UploadZone(
    onBrowseClick: () -> Unit,
    selectedFileName: String?,
    cardCount: Int
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .border(2.dp, AccentBlue.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .background(if (selectedFileName != null) PastelBlue.copy(alpha = 0.3f) else Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(if (selectedFileName != null) Color(0xFF4CAF50).copy(alpha = 0.2f) else PastelBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (selectedFileName != null) Icons.Default.CheckCircle else Icons.Default.ArrowForward, 
                    contentDescription = null, 
                    tint = if (selectedFileName != null) Color(0xFF4CAF50) else AccentBlue,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                if (selectedFileName != null) "File Selected" else "Upload CSV File", 
                fontWeight = FontWeight.Bold, 
                fontSize = 18.sp
            )
            Text(
                if (selectedFileName != null) {
                    "$selectedFileName\n$cardCount flashcards imported"
                } else {
                    "Tap to browse or drag and drop your\nflashcards file here"
                },
                textAlign = TextAlign.Center,
                color = TextGray,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onBrowseClick,
                colors = ButtonDefaults.buttonColors(containerColor = AccentPink),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(horizontal = 24.dp)
            ) {
                Text(
                    if (selectedFileName != null) "Choose Different File" else "Browse Files", 
                    color = TextDark, 
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun BionicReadingToggle() {
    var isEnabled by remember { mutableStateOf(true) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Bionic Reading", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = PastelBlue,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            "BETA",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentBlue
                        )
                    }
                }
                Text(
                    "Improve focus by highlighting the start of each word.",
                    fontSize = 12.sp,
                    color = TextGray
                )
            }
            Switch(
                checked = isEnabled,
                onCheckedChange = { isEnabled = it },
                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = AccentBlue)
            )
        }
    }
}

@Composable
fun FormattingGuide() {
    Column {
        Text("Formatting Guide", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBF0)),
            border = androidx.compose.foundation.BorderStroke(1.dp, AccentYellow),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(modifier = Modifier.padding(16.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("NotebookLM CSV Format", fontWeight = FontWeight.Bold)
                    Text(
                        "Formato: Pregunta, Respuesta\nEjemplo: ¿Qué ciencia estudia...?, La informática...",
                        fontSize = 12.sp,
                        color = TextGray
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = { },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Text("View Example", fontSize = 12.sp, color = TextDark)
                    }
                }
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White)
                        .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                )
            }
        }
    }
}

@Composable
fun RecentImportsSection() {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Recent Imports", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("Clear All", color = AccentBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(12.dp))
        val recentFiles = listOf(
            "Informatica_Basica.csv" to "Oct 24 • 124 cards",
            "Spanish_Vocab_V2.csv" to "Oct 21 • 50 cards",
            "Chemistry_Formulas.csv" to "Oct 15 • 88 cards"
        )
        recentFiles.forEach { (name, info) ->
            RecentFileItem(name, info)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun RecentFileItem(name: String, info: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (name.contains("Informatica")) PastelPink else if (name.contains("Spanish")) PastelYellow else PastelBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.List, contentDescription = null, tint = TextDark, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(info, fontSize = 12.sp, color = TextGray)
            }
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = TextGray)
        }
    }
}

@Composable
fun FlashcardPreviewItem(flashcard: Flashcard) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Q: ${flashcard.question}",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TextDark
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "A: ${flashcard.answer}",
                fontSize = 13.sp,
                color = TextGray
            )
        }
    }
}

@Composable
fun DeckNameDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    cardCount: Int
) {
    var deckName by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Name Your Flashcard Set") },
        text = {
            Column {
                Text("You're about to create a new deck with $cardCount flashcards.")
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = deckName,
                    onValueChange = { deckName = it },
                    label = { Text("Deck Name") },
                    placeholder = { Text("e.g., Spanish Vocabulary") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { 
                    if (deckName.isNotBlank()) {
                        onConfirm(deckName.trim())
                    }
                },
                enabled = deckName.isNotBlank()
            ) {
                Text("Create Deck")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun ImportPreview() {
    FlashCardsTheme {
        ImportScreen(onBack = {})
    }
}
