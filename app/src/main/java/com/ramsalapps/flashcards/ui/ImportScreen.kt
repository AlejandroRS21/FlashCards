package com.ramsalapps.flashcards.ui

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.ramsalapps.flashcards.CSVParser
import com.ramsalapps.flashcards.DataManager
import com.ramsalapps.flashcards.DeckWithFlashcards
import com.ramsalapps.flashcards.Flashcard
import com.ramsalapps.flashcards.Deck
import com.ramsalapps.flashcards.ui.theme.*

enum class ImportStep {
    SELECT_FILE,
    REVIEW_CARDS
}

@Composable
fun ImportScreen(
    onBack: () -> Unit,
    recentImports: List<Pair<String, String>> = emptyList(),
    onImportFinalized: (String, Uri) -> Unit = { _, _ -> },
    onDeckCreated: () -> Unit = {}
) {
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var deckName by remember { mutableStateOf("") }
    var importStep by remember { mutableStateOf(ImportStep.SELECT_FILE) }
    var parsedFlashcards by remember { mutableStateOf<List<Flashcard>>(emptyList()) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val csvParser = CSVParser(context)
    val dataManager = DataManager(context)

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedFileUri = it
            // Validar que sea CSV
            val fileName = it.lastPathSegment ?: ""
            if (fileName.endsWith(".csv", ignoreCase = true)) {
                if (deckName.isEmpty()) {
                    deckName = fileName.removeSuffix(".csv")
                }
                errorMessage = ""
            } else {
                errorMessage = "Por favor selecciona un archivo CSV válido"
                selectedFileUri = null
            }
        }
    }

    when (importStep) {
        ImportStep.SELECT_FILE -> {
            SelectFileStep(
                selectedFileUri = selectedFileUri,
                deckName = deckName,
                errorMessage = errorMessage,
                onDeckNameChange = { deckName = it },
                onBrowseClick = { launcher.launch("text/*") },
                onNextClick = {
                    isLoading = true
                    // Parsear CSV
                    selectedFileUri?.let { uri ->
                        csvParser.parseCSV(uri).onSuccess { flashcards ->
                            parsedFlashcards = flashcards
                            importStep = ImportStep.REVIEW_CARDS
                            isLoading = false
                        }.onFailure { error ->
                            errorMessage = "Error al parsear el archivo: ${error.message}"
                            isLoading = false
                        }
                    }
                },
                onBack = onBack,
                recentImports = recentImports,
                isLoading = isLoading
            )
        }
        ImportStep.REVIEW_CARDS -> {
            ReviewCardsStep(
                deckName = deckName,
                flashcards = parsedFlashcards,
                onDeckNameChange = { deckName = it },
                onCreateDeck = {
                    isLoading = true
                    // Guardar deck
                    val deck = Deck(
                        name = deckName,
                        cardCount = parsedFlashcards.size,
                        progress = 0,
                        icon = "📚"
                    )
                    val deckWithFlashcards = DeckWithFlashcards(deck, parsedFlashcards)
                    dataManager.saveDeck(deckWithFlashcards)
                    onImportFinalized(deckName, selectedFileUri ?: Uri.EMPTY)
                    onDeckCreated()
                    importStep = ImportStep.SELECT_FILE
                    selectedFileUri = null
                    deckName = ""
                    parsedFlashcards = emptyList()
                    isLoading = false
                },
                onBack = {
                    importStep = ImportStep.SELECT_FILE
                    errorMessage = ""
                },
                isLoading = isLoading
            )
        }
    }
}

@Composable
fun SelectFileStep(
    selectedFileUri: Uri?,
    deckName: String,
    errorMessage: String,
    onDeckNameChange: (String) -> Unit,
    onBrowseClick: () -> Unit,
    onNextClick: () -> Unit,
    onBack: () -> Unit,
    recentImports: List<Pair<String, String>> = emptyList(),
    isLoading: Boolean = false
) {
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
                    selectedFileName = selectedFileUri?.lastPathSegment,
                    onBrowseClick = onBrowseClick
                )
            }

            if (selectedFileUri != null) {
                item {
                    Column {
                        Text("Deck Name", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = deckName,
                            onValueChange = onDeckNameChange,
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Example: Biology Exam") },
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedIndicatorColor = AccentBlue,
                                unfocusedIndicatorColor = Color.LightGray
                            )
                        )
                    }
                }
            }

            item { BionicReadingToggle() }
            item { FormattingGuide() }
            item { RecentImportsSection(recentImports) }

            if (errorMessage.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFFEBEE)),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Error",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFC62828)
                                )
                                Text(
                                    errorMessage,
                                    fontSize = 12.sp,
                                    color = Color(0xFF5E35B1)
                                )
                            }
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = onNextClick,
                    enabled = selectedFileUri != null && deckName.isNotBlank() && !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentPink,
                        disabledContainerColor = Color.LightGray
                    ),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = TextDark,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Processing...", color = TextDark, fontWeight = FontWeight.Bold)
                    } else {
                        Text(
                            "Process CSV",
                            color = TextDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewCardsStep(
    deckName: String,
    flashcards: List<Flashcard>,
    onDeckNameChange: (String) -> Unit,
    onCreateDeck: () -> Unit,
    onBack: () -> Unit,
    isLoading: Boolean = false
) {
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
                    "Review & Create",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.width(24.dp))
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column {
                    Text("Deck Name", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = deckName,
                        onValueChange = onDeckNameChange,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = AccentBlue,
                            unfocusedIndicatorColor = Color.LightGray
                        )
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = PastelBlue),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Cards to Import",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextDark
                            )
                            Text(
                                "${flashcards.size} flashcards",
                                fontSize = 12.sp,
                                color = TextGray
                            )
                        }
                        Text(
                            flashcards.size.toString(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,
                            color = AccentBlue
                        )
                    }
                }
            }

            item {
                Text("Preview", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            items(flashcards.take(5).size) { index ->
                val card = flashcards[index]
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            "Q: ${card.question}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = TextDark
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "A: ${card.answer}",
                            fontSize = 11.sp,
                            color = TextGray
                        )
                    }
                }
            }

            if (flashcards.size > 5) {
                item {
                    Text(
                        "+ ${flashcards.size - 5} more cards",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                        color = AccentBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            item {
                Button(
                    onClick = onCreateDeck,
                    enabled = deckName.isNotBlank() && !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentPink,
                        disabledContainerColor = Color.LightGray
                    ),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = TextDark,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Creating...", color = TextDark, fontWeight = FontWeight.Bold)
                    } else {
                        Text(
                            "Create Deck",
                            color = TextDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UploadZone(selectedFileName: String?, onBrowseClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .border(2.dp, AccentBlue.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(PastelBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (selectedFileName != null) Icons.Default.Check else Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = if (selectedFileName != null) Color(0xFF4CAF50) else AccentBlue
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                if (selectedFileName != null) "File Selected!" else "Upload CSV File",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Text(
                selectedFileName ?: "Tap to browse your flashcards file",
                textAlign = TextAlign.Center,
                color = TextGray,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onBrowseClick,
                colors = ButtonDefaults.buttonColors(containerColor = AccentPink),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(horizontal = 24.dp)
            ) {
                Text(
                    if (selectedFileName != null) "Change File" else "Browse Files",
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
                        "Formato: Pregunta, Respuesta",
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
fun RecentImportsSection(recentFiles: List<Pair<String, String>>) {
    if (recentFiles.isEmpty()) return

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

@Preview(showBackground = true)
@Composable
fun ImportPreview() {
    FlashCardsTheme {
        ImportScreen(
            onBack = {},
            recentImports = listOf(
                "Informatica_Basica.csv" to "Oct 24 • 124 cards",
                "Spanish_Vocab_V2.csv" to "Oct 21 • 50 cards"
            )
        )
    }
}
