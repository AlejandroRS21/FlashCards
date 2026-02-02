# Documentación Técnica - FlashCards Import Feature

## Arquitectura

```
MainActivity
    ├── DashboardScreen (lista de decks)
    ├── ImportScreen (flujo de importación)
    └── StudySessionScreen (estudio)

DataManager (Persistencia)
    └── SharedPreferences (almacenamiento)

CSVParser (Parsing)
    └── InputStream (lectura de archivos)
```

## Componentes

### 1. CSVParser.kt
**Responsabilidad:** Parsear archivos CSV a objetos Flashcard

**Métodos:**
```kotlin
fun parseCSV(context: Context, uri: Uri): List<Flashcard>
```

**Formato esperado:**
```
pregunta,respuesta
pregunta,respuesta
```

**Características:**
- Ignora espacios en blanco
- Maneja excepciones gracefully
- Valida que haya al menos pregunta y respuesta

### 2. DataManager.kt
**Responsabilidad:** Gestionar persistencia de decks

**Métodos públicos:**
```kotlin
fun saveDeck(deck: Deck): Boolean
fun getAllDecks(): List<Deck>
fun getDeckById(id: String): Deck?
fun updateDeck(deck: Deck): Boolean
fun deleteDeck(id: String): Boolean
```

**Almacenamiento:**
- **Medio:** SharedPreferences
- **Clave:** "decks"
- **Formato:** JSON Array
- **Scope:** MODE_PRIVATE

**Estructura JSON:**
```json
[
  {
    "id": "uuid-string",
    "name": "Deck Name",
    "cardCount": 10,
    "progress": 0,
    "icon": "📚",
    "flashcards": [
      {
        "question": "Q?",
        "answer": "A",
        "category": "Deck Name"
      }
    ]
  }
]
```

### 3. Models.kt
**Clases de datos:**

```kotlin
data class Deck(
    val id: String = "",
    val name: String,
    val cardCount: Int,
    val progress: Int,
    val icon: String = "📚",
    val flashcards: List<Flashcard> = emptyList()
)

data class Flashcard(
    val question: String,
    val answer: String,
    val category: String
)

enum class ImportStep {
    SELECT_FILE,
    REVIEW_AND_CREATE
}
```

### 4. ImportScreen.kt
**Responsabilidad:** Orquestar flujo de importación

**Composables principales:**
- `ImportScreen()` - Orquestador principal
- `ImportFileSelectionStep()` - Paso 1: Seleccionar archivo
- `ImportReviewStep()` - Paso 2: Revisar y crear
- `UploadZone()` - Widget de carga
- `FormattingGuide()` - Guía de formato
- `BionicReadingToggle()` - Toggle de lectura biónica

**Estado manejado:**
```kotlin
currentStep: ImportStep
selectedFileUri: Uri?
fileName: String
deckName: String
importedFlashcards: List<Flashcard>
isLoading: Boolean
errorMessage: String?
```

**Flujo:**
1. Usuario selecciona archivo
2. Sistema parsea CSV
3. Muestra preview
4. Usuario confirma nombre
5. Sistema guarda con DataManager
6. Vuelve a Dashboard

### 5. MainActivity.kt
**Responsabilidad:** Orquestar navegación y estado global

**Navegación:**
```kotlin
enum class Screen {
    Dashboard,  // Pantalla principal
    Study,      // Sesión de estudio
    Import      // Importación
}
```

**Gestión de estado:**
```kotlin
var currentScreen: Screen
var decks: List<Deck>
```

**Ciclo de carga:**
1. Al iniciar: CargarDecks() con LaunchedEffect
2. Al importar: onImportSuccess recarma decks
3. Se propagan a DashboardScreen

## Flujo de Datos

```
Usuario selecciona archivo (Uri)
        ↓
CSVParser.parseCSV(context, uri)
        ↓
List<Flashcard>
        ↓
Deck creado con flashcards
        ↓
DataManager.saveDeck(deck)
        ↓
JSON guardado en SharedPreferences
        ↓
onImportSuccess callback
        ↓
MainActivity recarga decks
        ↓
decks se actualizan
        ↓
DashboardScreen re-composea con nuevos decks
```

## Manejo de Errores

### Errores de Parsing CSV
- Archivo vacío → "No se encontraron flashcards"
- Formato inválido → "Error al procesar el archivo"
- Excepción en lectura → "Error al procesar: [mensaje]"

### Errores de Persistencia
- Fallo al guardar → "Error al guardar el deck"
- JSON inválido → Manejo graceful en lectura

### Validaciones UI
- Archivo no seleccionado → Botón deshabilitado
- Nombre vacío → Botón deshabilitado
- Cargando → Indicador de progreso

## Consideraciones de Rendimiento

### Optimizaciones
- **LazyColumn** para listas de decks
- **Serialización incremental** de JSON
- **Caché de DataManager** con remember

### Limitaciones conocidas
- SharedPreferences tiene límite de tamaño (~1MB)
- No hay paginación para muchos decks
- Preview limitado a 5 tarjetas

## Seguridad

### Consideraciones
- No se valida origen del archivo CSV
- No hay encriptación de datos
- SharedPreferences almacena en texto plano
- UUID generado con java.util.UUID (seguro)

## Extensibilidad

### Puntos de extensión
1. **CSVParser**: Agregar soporte para más formatos
2. **DataManager**: Cambiar a SQLite o Room
3. **ImportScreen**: Agregar validación adicional
4. **Deck**: Agregar campos como imagen, tags, etc.

### Mejoras futuras
- Exportar decks a CSV
- Importar desde URLs
- Sincronización en la nube
- Búsqueda de decks
- Colecciones de decks

## Testing

### Casos de prueba sugeridos

```kotlin
// Test CSV Parser
fun testParseValidCSV() {
    val uri = "file://valid.csv"
    val result = CSVParser.parseCSV(context, uri)
    assert(result.size == 2)
}

fun testParseEmptyCSV() {
    val uri = "file://empty.csv"
    val result = CSVParser.parseCSV(context, uri)
    assert(result.isEmpty())
}

// Test DataManager
fun testSaveDeck() {
    val deck = Deck(name = "Test", cardCount = 5, progress = 0)
    val result = dataManager.saveDeck(deck)
    assert(result)
}

fun testPersistence() {
    dataManager.saveDeck(deck1)
    val loaded = dataManager.getAllDecks()
    assert(loaded.any { it.id == deck1.id })
}
```

## Dependencias

```gradle
// En build.gradle.kts
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.material3:material3")
implementation("org.json:json:20230227")  // Para serialización JSON
```

## Referencias

- [Material Design 3 - Compose](https://developer.android.com/develop/ui/compose/designsystems/material3)
- [SharedPreferences](https://developer.android.com/training/data-storage/shared-preferences)
- [File Access - Android](https://developer.android.com/training/data-storage/shared)
