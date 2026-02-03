# Integración de Android Mobile Design Skill - MCPMarket

## Descripción General

Se ha integrado la skill "Android Mobile Design" de MCPMarket en el proyecto FlashCards. Esta integración proporciona un sistema de diseño consistente, componentes reutilizables y patrones recomendados alineados con Material Design 3.

## Estructura de Módulos

### 1. `MCPClient.kt`
**Responsabilidad**: Cliente de protocolo MCP para conectarse al servidor de la skill

**Características principales**:
- Conexión JSON-RPC al endpoint de MCPMarket
- Métodos para obtener recomendaciones de:
  - Diseño de componentes
  - Temas y paletas de color
  - Layouts responsivos
- Fallback a patrones locales si no hay conexión
- Manejo de errores y logging

**Uso**:
```kotlin
val mcpClient = MCPClient.getInstance(context)
val connected = mcpClient.connect()

// Obtener recomendación de diseño
val recommendation = mcpClient.getDesignRecommendations(
    componentType = "button",
    screenSize = "small"
)

// Obtener recomendaciones de tema
val theme = mcpClient.getThemeRecommendations(isDarkMode = false)

// Obtener layout responsivo
val layout = mcpClient.getLayoutRecommendations(screenWidth = 400, screenHeight = 800)
```

### 2. `MCPViewModel.kt`
**Responsabilidad**: Gestionar el estado y las llamadas asincrónicas al cliente MCP

**Estado expuesto**:
- `isConnected`: StateFlow<Boolean>
- `designRecommendation`: StateFlow<DesignRecommendation?>
- `themeRecommendation`: StateFlow<ThemeRecommendation?>
- `layoutRecommendation`: StateFlow<LayoutRecommendation?>
- `isLoading`: StateFlow<Boolean>
- `errorMessage`: StateFlow<String?>

**Métodos principales**:
- `connectToMCP()`: Conecta con el servidor MCP
- `fetchDesignRecommendation()`: Obtiene recomendaciones de diseño
- `fetchThemeRecommendation()`: Obtiene recomendaciones de tema
- `fetchLayoutRecommendation()`: Obtiene recomendaciones de layout

**Uso**:
```kotlin
val viewModel = MCPViewModel(context)

LaunchedEffect(Unit) {
    viewModel.connectToMCP()
}

val isConnected by viewModel.isConnected.collectAsState()
val isLoading by viewModel.isLoading.collectAsState()
```

### 3. `DesignSystemPatterns.kt`
**Responsabilidad**: Centralizar todos los patrones y constantes de diseño

**Contenido**:
- **Spacing**: Espaciado estándar (xs, sm, md, lg, xl, xxl)
- **Typography**: Tamaños de fuente (10sp a 40sp)
- **CornerRadius**: Radios de esquinas (0dp a 999dp)
- **ComponentSize**: Dimensiones de botones, cards, iconos, etc.
- **PaddingPatterns**: Patrones de padding por tipo de componente
- **MarginPatterns**: Márgenes entre elementos
- **ResponsiveLayout**: Configuración para diferentes tamaños de pantalla
- **Animation**: Duraciones de animación estándar
- **Accessibility**: Guías de accesibilidad WCAG AA

**Patrones específicos**:
- `ButtonPatterns`: padding, altura mínima, border radius, etc.
- `CardPatterns`: padding, elevation, spacing
- `TypographyPatterns`: font sizes, weights, line heights
- `NavigationPatterns`: altura, iconos, spacing
- `DialogPatterns`: dimensiones y espaciado
- `ListPatterns`: altura de items, padding, dividers

**Funciones helper**:
```kotlin
// Obtener padding responsivo según ancho de pantalla
val padding = DesignSystemPatterns.getResponsivePadding(screenWidth)

// Obtener número de columnas para grid responsivo
val columns = DesignSystemPatterns.getColumnCount(screenWidth)

// Obtener font size escalada responsivamente
val fontSize = DesignSystemPatterns.getResponsiveFontSize(screenWidth, 14)
```

### 4. `DesignSystemComponents.kt`
**Responsabilidad**: Componentes Composable reutilizables basados en patrones

**Componentes disponibles**:

#### Botones
- `DesignSystemButton()`: Botón principal con 3 tamaños (Small, Medium, Large)

```kotlin
DesignSystemButton(
    text = "Continuar",
    onClick = { /* ... */ },
    size = ButtonSize.Large,
    backgroundColor = Color(0xFF7C63D8)
)
```

#### Typography
- `DesignSystemHeadline()`: Títulos grandes (24sp)
- `DesignSystemTitle()`: Títulos medianos (20sp)
- `DesignSystemSubtitle()`: Subtítulos (18sp)
- `DesignSystemBody()`: Texto normal (14sp)
- `DesignSystemCaption()`: Texto pequeño (12sp)

```kotlin
DesignSystemHeadline(text = "Mi Aplicación")
DesignSystemBody(text = "Contenido principal")
DesignSystemCaption(text = "Texto auxiliar")
```

#### Cards
- `DesignSystemCard()`: Card reutilizable con padding y elevation

```kotlin
DesignSystemCard(
    backgroundColor = Color.White,
    elevation = DesignSystemPatterns.Elevation.sm
) {
    DesignSystemBody(text = "Contenido de la card")
}
```

#### Listas
- `DesignSystemListItem()`: Item de lista con título, subtítulo, icon y trailing

```kotlin
DesignSystemListItem(
    title = "Mi Elemento",
    subtitle = "Descripción",
    icon = { Icon(...) },
    onClick = { /* ... */ }
)
```

#### Espaciadores
- `DesignSystemVerticalSpacer()`: Espaciador vertical
- `DesignSystemHorizontalSpacer()`: Espaciador horizontal

```kotlin
DesignSystemVerticalSpacer(DesignSystemPatterns.Spacing.lg)
```

#### Elementos especiales
- `DesignSystemBadge()`: Badge redondeado
- `DesignSystemSection()`: Sección con título
- `DesignSystemDivider()`: Línea divisoria
- `DesignSystemLoadingState()`: Estado de carga
- `DesignSystemErrorState()`: Estado de error
- `DesignSystemEmptyState()`: Estado vacío

## Integración en Pantallas Existentes

### DashboardScreen.kt
Para mejorar el dashboard usando componentes del design system:

```kotlin
import com.ramsalapps.flashcards.mcp.*

@Composable
fun DashboardScreen() {
    val mcpViewModel = remember { MCPViewModel(LocalContext.current) }
    val isLoading by mcpViewModel.isLoading.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(DesignSystemPatterns.Spacing.lg)
    ) {
        DesignSystemHeadline(text = "Mis Flashcards")
        DesignSystemVerticalSpacer(DesignSystemPatterns.Spacing.md)
        
        LazyColumn {
            items(decks) { deck ->
                DesignSystemCard {
                    DesignSystemTitle(text = deck.name)
                    DesignSystemBody(text = "${deck.flashcards.size} tarjetas")
                }
                DesignSystemVerticalSpacer(DesignSystemPatterns.Spacing.md)
            }
        }
    }
}
```

### StudySessionScreen.kt
Para mejorar la pantalla de estudio:

```kotlin
DesignSystemCard(
    backgroundColor = Color(0xFF7CEFFF),
    cornerRadius = DesignSystemPatterns.CornerRadius.lg
) {
    DesignSystemTitle(text = currentCard.question)
    DesignSystemVerticalSpacer(DesignSystemPatterns.Spacing.md)
    DesignSystemBody(text = currentCard.answer)
}

Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(
        DesignSystemPatterns.Spacing.md
    )
) {
    DesignSystemButton(
        text = "Anterior",
        onClick = { onPrevious() },
        size = ButtonSize.Medium,
        modifier = Modifier.weight(1f)
    )
    DesignSystemButton(
        text = "Siguiente",
        onClick = { onNext() },
        size = ButtonSize.Medium,
        modifier = Modifier.weight(1f)
    )
}
```

## Ventajas de la Integración

### Para Desarrolladores
✅ **Componentes reutilizables**: No duplicar código de UI
✅ **Consistencia visual**: Todos los elementos siguen el mismo patrón
✅ **Mantenimiento centralizado**: Cambios en un solo lugar
✅ **Patrones probados**: Basado en recomendaciones de Google Material Design
✅ **Escalabilidad**: Fácil agregar nuevas pantallas con el sistema de diseño

### Para Usuarios
✅ **Mejor experiencia visual**: Interfaz moderna y profesional
✅ **Accesibilidad mejorada**: Sigue guías WCAG AA
✅ **Responsividad**: Funciona bien en diferentes tamaños de pantalla
✅ **Consistencia**: Elementos visuales uniformes en toda la app

## Configuración Recomendada

### En MainActivity.kt
```kotlin
import com.ramsalapps.flashcards.mcp.MCPViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val mcpViewModel = MCPViewModel(this)
        
        setContent {
            FlashCardsTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    FlashCardsApp(mcpViewModel = mcpViewModel)
                }
            }
        }
    }
}
```

## Próximos Pasos Recomendados

1. **Aplicar a DashboardScreen**: Usar `DesignSystemCard` para decks
2. **Refactorizar StudySessionScreen**: Aplicar patrones de colores y espaciado
3. **Mejorar ImportScreen**: Usar componentes de input del design system
4. **Actualizar SettingsScreen**: Aplicar listas y componentes estándar
5. **Sincronizar con MCP**: Si el servidor está disponible, obtener patrones en tiempo real

## Endpoints MCP Disponibles

```
POST /v1/skills/android-mobile-design/rpc

Métodos disponibles:
- design.getRecommendation
  - Parámetros: componentType, screenSize, context
  - Retorna: DesignRecommendation

- theme.getRecommendation
  - Parámetros: isDarkMode, baseStyle
  - Retorna: ThemeRecommendation

- layout.getRecommendation
  - Parámetros: screenWidth, screenHeight
  - Retorna: LayoutRecommendation
```

## Troubleshooting

### Problema: No se conecta con MCP
**Solución**: El cliente automáticamente usa patrones locales como fallback

### Problema: Componentes se ven diferentes
**Solución**: Asegúrate de usar `DesignSystemPatterns.*` en lugar de valores hardcoded

### Problema: Responsive design no funciona
**Solución**: Usa `DesignSystemPatterns.getResponsivePadding()` y `getColumnCount()`

## Referencias

- Material Design 3: https://m3.material.io/
- MCPMarket: https://mcpmarket.com/
- Jetpack Compose: https://developer.android.com/jetpack/compose
