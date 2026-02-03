package com.ramsalapps.flashcards.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Check
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
import androidx.activity.compose.BackHandler
import com.ramsalapps.flashcards.CSVParser
import com.ramsalapps.flashcards.DataManager
import com.ramsalapps.flashcards.DeckWithFlashcards
import com.ramsalapps.flashcards.Flashcard
import com.ramsalapps.flashcards.Deck
import com.ramsalapps.flashcards.ui.theme.*
import com.ramsalapps.flashcards.ui.theme.Spacing
import com.ramsalapps.flashcards.ui.theme.BorderRadius
import com.ramsalapps.flashcards.designsystem.components.DesignSystemButton
import com.ramsalapps.flashcards.designsystem.components.DesignSystemCard
import com.ramsalapps.flashcards.designsystem.components.ButtonSize

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
    // Manejar el botón atrás del móvil
    BackHandler {
        onBack()
    }

    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var deckName by remember { mutableStateOf("") }
    var importStep by remember { mutableStateOf(ImportStep.SELECT_FILE) }
    var parsedFlashcards by remember { mutableStateOf<List<Flashcard>>(emptyList()) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var importedCount by remember { mutableStateOf(0) }

    val context = LocalContext.current
    val csvParser = CSVParser(context)
    val dataManager = DataManager(context)

    // Constantes de validación
    val MAX_FILE_SIZE_BYTES = 5 * 1024 * 1024 // 5 MB
    val MAX_IMPORTS = 10

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedFileUri = it
            val fileName = it.lastPathSegment ?: ""

            // Validar que sea CSV
            if (!fileName.endsWith(".csv", ignoreCase = true)) {
                errorMessage = "❌ Solo se permiten archivos CSV"
                selectedFileUri = null
                return@rememberLauncherForActivityResult
            }

            // Validar tamaño del archivo
            try {
                val fileSize = context.contentResolver.query(it, null, null, null, null)?.use { cursor ->
                    cursor.moveToFirst()
                    val sizeIndex = cursor.getColumnIndex(android.provider.OpenableColumns.SIZE)
                    if (sizeIndex != -1) cursor.getLong(sizeIndex) else 0L
                } ?: 0L

                if (fileSize > MAX_FILE_SIZE_BYTES) {
                    errorMessage = "❌ El archivo es demasiado grande (máximo 5 MB)"
                    selectedFileUri = null
                    return@rememberLauncherForActivityResult
                }

                // Validar límite de importaciones
                if (importedCount >= MAX_IMPORTS) {
                    errorMessage = "⚠️ Límite de 10 importaciones alcanzado"
                    selectedFileUri = null
                    return@rememberLauncherForActivityResult
                }

                if (deckName.isEmpty()) {
                    deckName = fileName.removeSuffix(".csv")
                }
                errorMessage = ""
            } catch (e: Exception) {
                errorMessage = "❌ Error al validar el archivo"
                selectedFileUri = null
            }
        }
    }

    Scaffold(
        bottomBar = {
            if (importStep == ImportStep.SELECT_FILE) {
                BottomNavigationBar(onLibraryClick = onBack, currentScreen = "import")
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
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
                            selectedFileUri?.let { uri ->
                                csvParser.parseCSV(uri).onSuccess { flashcards: List<Flashcard> ->
                                    parsedFlashcards = flashcards
                                    importStep = ImportStep.REVIEW_CARDS
                                    isLoading = false
                                }.onFailure { error: Throwable ->
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

                            // Incrementar contador y limpiar
                            importedCount++

                            // Resetear estado
                            importStep = ImportStep.SELECT_FILE
                            selectedFileUri = null
                            deckName = ""
                            parsedFlashcards = emptyList()
                            isLoading = false
                            errorMessage = ""
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
                    .padding(Spacing.xs),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { onBack() },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Back",
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    "Import Flashcards",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(40.dp))
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .systemBarsPadding()
                .padding(horizontal = Spacing.xl, vertical = Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                    shape = RoundedCornerShape(BorderRadius.md)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.md),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "📋 Solo archivos CSV | Máximo 5 MB | Límite: 10 importaciones",
                            fontSize = 12.sp,
                            color = Color(0xFF1976D2),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

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
                        Spacer(modifier = Modifier.height(Spacing.sm))
                        OutlinedTextField(
                            value = deckName,
                            onValueChange = onDeckNameChange,
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Example: Biology Exam") },
                            shape = RoundedCornerShape(BorderRadius.md),
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

            item { FormattingGuide() }
            item { RecentImportsSection(recentImports) }

            if (errorMessage.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFFEBEE)),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                        shape = RoundedCornerShape(BorderRadius.md)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Spacing.lg),
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
                DesignSystemButton(
                    onClick = onNextClick,
                    text = if (isLoading) "Processing..." else "Process CSV",
                    size = ButtonSize.Large,
                    enabled = selectedFileUri != null && deckName.isNotBlank() && !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = Spacing.xl)
                )
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
                    .padding(Spacing.xs),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { onBack() },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Back",
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    "Review & Create",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(40.dp))
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .systemBarsPadding()
                .padding(horizontal = Spacing.xl, vertical = Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Column {
                    Text("Deck Name", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    OutlinedTextField(
                        value = deckName,
                        onValueChange = onDeckNameChange,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(BorderRadius.md),
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
                    shape = RoundedCornerShape(BorderRadius.md)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.lg),
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
                    shape = RoundedCornerShape(BorderRadius.md)
                ) {
                    Column(modifier = Modifier.padding(Spacing.md)) {
                        Text(
                            "Q: ${card.question}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = TextDark
                        )
                        Spacer(modifier = Modifier.height(Spacing.sm))
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
                DesignSystemButton(
                    onClick = onCreateDeck,
                    text = if (isLoading) "Creating..." else "Create Deck",
                    size = ButtonSize.Large,
                    enabled = deckName.isNotBlank() && !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = Spacing.xl)
                )
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
            .border(2.dp, AccentBlue.copy(alpha = 0.5f), RoundedCornerShape(BorderRadius.lg))
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
                    if (selectedFileName != null) Icons.Default.Check else Icons.AutoMirrored.Filled.ArrowForward,
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
                modifier = Modifier.padding(horizontal = Spacing.lg)
            )
            Spacer(modifier = Modifier.height(16.dp))
            DesignSystemButton(
                onClick = onBrowseClick,
                text = if (selectedFileName != null) "Change File" else "Browse Files",
                size = ButtonSize.Medium,
                modifier = Modifier
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
            shape = RoundedCornerShape(BorderRadius.md)
        ) {
            Row(modifier = Modifier.padding(Spacing.lg)) {
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
                        shape = RoundedCornerShape(BorderRadius.sm),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Text("View Example", fontSize = 12.sp, color = TextDark)
                    }
                }
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(BorderRadius.sm))
                        .background(Color.White)
                        .border(1.dp, Color.LightGray, RoundedCornerShape(BorderRadius.sm))
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
            Spacer(modifier = Modifier.height(Spacing.sm))
        }
    }
}

@Composable
fun RecentFileItem(name: String, info: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
        shape = RoundedCornerShape(BorderRadius.md)
    ) {
        Row(
            modifier = Modifier.padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(BorderRadius.sm))
                    .background(if (name.contains("Informatica")) PastelPink else if (name.contains("Spanish")) PastelYellow else PastelBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.AutoMirrored.Filled.List, contentDescription = null, tint = TextDark, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(info, fontSize = 12.sp, color = TextGray)
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = TextGray)
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

