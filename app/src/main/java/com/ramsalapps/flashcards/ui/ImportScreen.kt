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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Style
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
import com.ramsalapps.flashcards.TestDeck
import com.ramsalapps.flashcards.TestQuestion
import com.ramsalapps.flashcards.TestDeckWithQuestions
import com.ramsalapps.flashcards.ui.theme.*
import com.ramsalapps.flashcards.ui.theme.Spacing
import com.ramsalapps.flashcards.ui.theme.BorderRadius
import com.ramsalapps.flashcards.designsystem.components.DesignSystemButton
import com.ramsalapps.flashcards.designsystem.components.DesignSystemCard
import com.ramsalapps.flashcards.designsystem.components.ButtonSize
import com.ramsalapps.flashcards.ui.components.AppNavigationBar
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

enum class ImportType {
    FLASHCARDS,
    TESTS
}

enum class ImportStep {
    SELECT_TYPE,
    SELECT_FILE,
    REVIEW_CARDS,
    REVIEW_TESTS,
    REVIEW_BATCH
}

@Composable
fun ImportScreen(
    onBack: () -> Unit,
    recentImports: List<Pair<String, String>> = emptyList(),
    onImportFinalized: (String, Uri) -> Unit = { _, _ -> },
    onDeckCreated: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    BackHandler { onBack() }

    var importType by remember { mutableStateOf(ImportType.FLASHCARDS) }
    var importStep by remember { mutableStateOf(ImportStep.SELECT_TYPE) }
    var selectedFileUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var deckName by remember { mutableStateOf("") }
    
    var parsedFlashcards by remember { mutableStateOf<List<Flashcard>>(emptyList()) }
    var parsedTestQuestions by remember { mutableStateOf<List<TestQuestion>>(emptyList()) }
    var batchParsedDecks by remember { mutableStateOf<List<Triple<Uri, String, List<Flashcard>>>>(emptyList()) }
    
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val csvParser = CSVParser(context)
    val dataManager = DataManager(context)

    fun getDisplayName(uri: Uri): String {
        return try {
            context.contentResolver.query(uri, arrayOf(android.provider.OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
                cursor.moveToFirst()
                val index = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (index != -1) cursor.getString(index) else uri.lastPathSegment ?: ""
            } ?: uri.lastPathSegment ?: ""
        } catch (e: Exception) { uri.lastPathSegment ?: "" }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isEmpty()) return@rememberLauncherForActivityResult
        val limitedUris = uris.take(10)
        val validUris = limitedUris.filter { getDisplayName(it).endsWith(".csv", ignoreCase = true) }
        
        if (validUris.size < uris.size) {
            errorMessage = "⚠️ Only CSV files allowed (max 10)"
        } else {
            errorMessage = ""
        }
        
        selectedFileUris = validUris
        if (validUris.size == 1) {
            deckName = getDisplayName(validUris.first()).removeSuffix(".csv")
        }
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(vertical = 8.dp, horizontal = Spacing.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { 
                    if (importStep == ImportStep.SELECT_TYPE) onBack() else importStep = ImportStep.SELECT_TYPE 
                }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Back")
                }
                Text(
                    if (importStep == ImportStep.SELECT_TYPE) "Import Content" else "Import ${importType.name.lowercase().capitalize()}",
                    fontWeight = FontWeight.Bold, fontSize = 18.sp, textAlign = TextAlign.Center, modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(48.dp))
            }
        },
        bottomBar = {
            if (importStep == ImportStep.SELECT_TYPE || importStep == ImportStep.SELECT_FILE) {
                AppNavigationBar(
                    currentScreen = "import",
                    onHomeClick = onHomeClick,
                    onImportClick = {}, // We're already here
                    onSettingsClick = onSettingsClick
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).consumeWindowInsets(padding)) {
            when (importStep) {
                ImportStep.SELECT_TYPE -> {
                    SelectTypeStep(
                        onSelectType = { type ->
                            importType = type
                            importStep = ImportStep.SELECT_FILE
                        }
                    )
                }
                ImportStep.SELECT_FILE -> {
                    SelectFileStep(
                        selectedFiles = selectedFileUris,
                        displayFileName = if (selectedFileUris.size == 1) getDisplayName(selectedFileUris.first()) else if (selectedFileUris.size > 1) "${selectedFileUris.size} files" else null,
                        deckName = deckName,
                        errorMessage = errorMessage,
                        importType = importType,
                        onDeckNameChange = { deckName = it },
                        onBrowseClick = { launcher.launch("text/*") },
                        onNextClick = {
                            isLoading = true
                            if (selectedFileUris.size == 1) {
                                val uri = selectedFileUris.first()
                                if (importType == ImportType.FLASHCARDS) {
                                    csvParser.parseCSV(uri).onSuccess {
                                        parsedFlashcards = it
                                        importStep = ImportStep.REVIEW_CARDS
                                        isLoading = false
                                    }.onFailure { errorMessage = it.message ?: "Error"; isLoading = false }
                                } else {
                                    csvParser.parseTestCSV(uri).onSuccess {
                                        parsedTestQuestions = it
                                        importStep = ImportStep.REVIEW_TESTS
                                        isLoading = false
                                    }.onFailure { errorMessage = it.message ?: "Error"; isLoading = false }
                                }
                            } else if (importType == ImportType.FLASHCARDS) {
                                scope.launch {
                                    val results = selectedFileUris.map { uri ->
                                        async {
                                            csvParser.parseCSV(uri).getOrNull()?.let { Triple(uri, getDisplayName(uri).removeSuffix(".csv"), it) }
                                        }
                                    }.awaitAll().filterNotNull()
                                    if (results.isEmpty()) errorMessage = "Error parsing" else { batchParsedDecks = results; importStep = ImportStep.REVIEW_BATCH }
                                    isLoading = false
                                }
                            }
                        },
                        isLoading = isLoading
                    )
                }
                ImportStep.REVIEW_CARDS -> {
                    ReviewCardsStep(
                        deckName = deckName,
                        flashcards = parsedFlashcards,
                        onDeckNameChange = { deckName = it },
                        onCreateDeck = {
                            dataManager.saveDeck(DeckWithFlashcards(Deck(name = deckName, cardCount = parsedFlashcards.size, progress = 0), parsedFlashcards))
                            onDeckCreated(); onBack()
                        }
                    )
                }
                ImportStep.REVIEW_TESTS -> {
                    ReviewTestsStep(
                        deckName = deckName,
                        questions = parsedTestQuestions,
                        onDeckNameChange = { deckName = it },
                        onCreateTest = {
                            val testDeck = TestDeck(name = deckName, questionCount = parsedTestQuestions.size)
                            dataManager.saveTestDeck(TestDeckWithQuestions(testDeck, parsedTestQuestions))
                            onDeckCreated(); onBack()
                        }
                    )
                }
                ImportStep.REVIEW_BATCH -> {
                    ReviewBatchStep(
                        batchDecks = batchParsedDecks,
                        onImportAll = {
                            batchParsedDecks.forEach { (_, name, cards) ->
                                dataManager.saveDeck(DeckWithFlashcards(Deck(name = name, cardCount = cards.size, progress = 0), cards))
                            }
                            onDeckCreated(); onBack()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SelectTypeStep(onSelectType: (ImportType) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(Spacing.xl),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("What would you like to import?", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(32.dp))
        
        TypeOptionCard(
            title = "Flashcards",
            description = "Import question-answer pairs for classic study sessions.",
            icon = Icons.Default.Style,
            color = PastelBlue,
            onClick = { onSelectType(ImportType.FLASHCARDS) }
        )
        
        Spacer(modifier = Modifier.height(Spacing.lg))
        
        TypeOptionCard(
            title = "Practice Tests",
            description = "Import multiple-choice questions with scores and explanations.",
            icon = Icons.Default.Assignment,
            color = PastelGreen,
            onClick = { onSelectType(ImportType.TESTS) }
        )
    }
}

@Composable
fun TypeOptionCard(title: String, description: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(BorderRadius.lg)
    ) {
        Row(modifier = Modifier.padding(Spacing.lg), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(56.dp).clip(RoundedCornerShape(BorderRadius.md)).background(color), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = TextDark, modifier = Modifier.size(32.dp))
            }
            Spacer(modifier = Modifier.width(Spacing.lg))
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextDark)
                Text(description, fontSize = 14.sp, color = TextGray)
            }
        }
    }
}

@Composable
fun SelectFileStep(
    selectedFiles: List<Uri>,
    displayFileName: String?,
    deckName: String,
    errorMessage: String,
    importType: ImportType,
    onDeckNameChange: (String) -> Unit,
    onBrowseClick: () -> Unit,
    onNextClick: () -> Unit,
    isLoading: Boolean
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = Spacing.xl, vertical = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = if (importType == ImportType.TESTS) Color(0xFFE8F5E9) else Color(0xFFE3F2FD))) {
                Text(
                    if (importType == ImportType.FLASHCARDS) "📋 Format: Question, Answer" else "📝 Format: Question, Opt A, Opt B, Opt C, Opt D, CorrectIdx, Explanation",
                    modifier = Modifier.padding(Spacing.md), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (importType == ImportType.TESTS) Color(0xFF2E7D32) else Color(0xFF1976D2)
                )
            }
        }
        item { UploadZone(selectedFileName = displayFileName, onBrowseClick = onBrowseClick) }
        if (selectedFiles.size == 1) {
            item {
                OutlinedTextField(
                    value = deckName, onValueChange = onDeckNameChange, modifier = Modifier.fillMaxWidth(),
                    label = { Text("Name your deck") }, shape = RoundedCornerShape(BorderRadius.md)
                )
            }
        }
        if (errorMessage.isNotEmpty()) item { Text(errorMessage, color = Color.Red, fontSize = 12.sp) }
        item {
            DesignSystemButton(
                onClick = onNextClick, text = if (isLoading) "Processing..." else "Process CSV",
                enabled = selectedFiles.isNotEmpty() && !isLoading, modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun ReviewCardsStep(deckName: String, flashcards: List<Flashcard>, onDeckNameChange: (String) -> Unit, onCreateDeck: () -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(Spacing.xl), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { OutlinedTextField(value = deckName, onValueChange = onDeckNameChange, modifier = Modifier.fillMaxWidth(), label = { Text("Deck Name") }) }
        items(flashcards.take(5)) { card ->
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(modifier = Modifier.padding(Spacing.md)) {
                    Text("Q: ${card.question}", fontWeight = FontWeight.Bold)
                    Text("A: ${card.answer}", color = TextGray)
                }
            }
        }
        item { DesignSystemButton(onClick = onCreateDeck, text = "Create Deck", modifier = Modifier.fillMaxWidth()) }
    }
}

@Composable
fun ReviewTestsStep(deckName: String, questions: List<TestQuestion>, onDeckNameChange: (String) -> Unit, onCreateTest: () -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(Spacing.xl), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { OutlinedTextField(value = deckName, onValueChange = onDeckNameChange, modifier = Modifier.fillMaxWidth(), label = { Text("Test Name") }) }
        item { Text("${questions.size} Questions detected", fontWeight = FontWeight.Bold) }
        items(questions.take(3)) { q ->
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(1.dp)) {
                Column(modifier = Modifier.padding(Spacing.md)) {
                    Text("Q: ${q.question}", fontWeight = FontWeight.Bold)
                    q.options.forEachIndexed { idx, opt ->
                        Text("${if (idx == q.correctAnswerIndex) "✓" else "○"} $opt", fontSize = 12.sp, color = if (idx == q.correctAnswerIndex) Color(0xFF4CAF50) else TextGray)
                    }
                }
            }
        }
        item { DesignSystemButton(onClick = onCreateTest, text = "Create Test Deck", modifier = Modifier.fillMaxWidth()) }
    }
}

@Composable
fun ReviewBatchStep(batchDecks: List<Triple<Uri, String, List<Flashcard>>>, onImportAll: () -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(Spacing.xl), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { Text("${batchDecks.size} Decks to import", fontWeight = FontWeight.Bold) }
        items(batchDecks) { (_, name, cards) ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Text("$name (${cards.size} cards)", modifier = Modifier.padding(Spacing.md))
            }
        }
        item { DesignSystemButton(onClick = onImportAll, text = "Import All", modifier = Modifier.fillMaxWidth()) }
    }
}

@Composable
fun UploadZone(selectedFileName: String?, onBrowseClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth().height(180.dp).border(2.dp, AccentBlue.copy(alpha = 0.3f), RoundedCornerShape(BorderRadius.lg)).background(Color.White).clickable { onBrowseClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(if (selectedFileName != null) Icons.Default.Check else Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = if (selectedFileName != null) Color(0xFF4CAF50) else AccentBlue, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(selectedFileName ?: "Tap to select CSV file", fontWeight = FontWeight.Medium)
        }
    }
}
