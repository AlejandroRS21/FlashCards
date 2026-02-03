# 🔌 SKILL INTEGRATION GUIDE - Android Mobile Design

**Versión**: 1.0.0  
**Proyecto**: FlashCards  
**Skill**: Android Mobile Design  
**Fecha**: 03 Feb 2026

---

## 📌 Guía de Integración de la Skill

Esta guía te ayudará a integrar la skill Android Mobile Design en el proyecto FlashCards.

---

## 🚀 Paso 1: Preparar la Estructura de Carpetas

Crea la siguiente estructura en `app/src/main/java/com/ramsalapps/flashcards/`:

```
designsystem/
├── DesignSystemPatterns.kt          # Constantes principales
├── components/
│   ├── buttons/
│   │   ├── DesignSystemButton.kt
│   │   └── ButtonDefaults.kt
│   ├── cards/
│   │   ├── DesignSystemCard.kt
│   │   └── CardDefaults.kt
│   ├── inputs/
│   │   ├── DesignSystemTextField.kt
│   │   └── InputDefaults.kt
│   ├── navigation/
│   │   ├── DesignSystemTopAppBar.kt
│   │   └── DesignSystemBottomNavigation.kt
│   └── common/
│       ├── DesignSystemDivider.kt
│       ├── DesignSystemListItem.kt
│       └── Spacers.kt
└── theme/
    ├── Color.kt
    ├── Type.kt
    └── Theme.kt
```

---

## 🎨 Paso 2: Crear el Archivo de Constantes

**Archivo**: `app/src/main/java/com/ramsalapps/flashcards/designsystem/DesignSystemPatterns.kt`

```kotlin
package com.ramsalapps.flashcards.designsystem

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color

object DesignSystemPatterns {
    
    // Espaciado
    object Spacing {
        val xs = 4.dp
        val sm = 8.dp
        val md = 12.dp
        val lg = 16.dp
        val xl = 24.dp
        val xxl = 32.dp
    }
    
    // Colores
    object Colors {
        val Primary = Color(0xFF7C63D8)      // Morado
        val Secondary = Color(0xFFFF8ECD)    // Rosa
        val Tertiary = Color(0xFF7CEFFF)     // Cyan
        val Background = Color(0xFFFFFFFF)   // Blanco
        val Surface = Color(0xFFF5F5F5)      // Gris claro
        val Error = Color(0xFFFF6B6B)        // Rojo
        
        val OnPrimary = Color(0xFFFFFFFF)
        val OnSecondary = Color(0xFFFFFFFF)
        val OnTertiary = Color(0xFF000000)
        val OnError = Color(0xFFFFFFFF)
    }
    
    // Border Radius
    object BorderRadius {
        val none = 0.dp
        val xs = 4.dp
        val sm = 8.dp
        val md = 12.dp
        val lg = 16.dp
        val full = 999.dp
    }
    
    // Tipografía
    object TextSize {
        val xs = 10.sp      // Caption
        val sm = 12.sp      // Pequeño
        val md = 14.sp      // Body
        val lg = 16.sp      // Body Large
        val xl = 18.sp      // Subtitle
        val xxl = 20.sp     // Title
        val xxxl = 24.sp    // Headline
        val display = 32.sp // Display
    }
    
    // Funciones Helper
    fun getColumnCount(screenWidthDp: Int): Int = when {
        screenWidthDp < 600 -> 1
        screenWidthDp < 840 -> 2
        else -> 3
    }
    
    fun getResponsivePadding(screenWidthDp: Int): Float = when {
        screenWidthDp < 600 -> 16f
        screenWidthDp < 840 -> 24f
        else -> 32f
    }
}

// Alias para facilitar uso
val LocalSpacing = Spacing
val LocalColors = Colors
```

---

## 🎨 Paso 3: Crear el Archivo de Colores

**Archivo**: `app/src/main/java/com/ramsalapps/flashcards/designsystem/theme/Color.kt`

```kotlin
package com.ramsalapps.flashcards.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Colores de la paleta
val Primary = Color(0xFF7C63D8)
val Secondary = Color(0xFFFF8ECD)
val Tertiary = Color(0xFF7CEFFF)
val Background = Color(0xFFFFFFFF)
val Surface = Color(0xFFF5F5F5)
val Error = Color(0xFFFF6B6B)

// Esquema light
val LightColors = lightColorScheme(
    primary = Primary,
    secondary = Secondary,
    tertiary = Tertiary,
    background = Background,
    surface = Surface,
    error = Error,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.Black,
    onError = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black
)

// Esquema dark (opcional)
val DarkColors = darkColorScheme(
    primary = Primary,
    secondary = Secondary,
    tertiary = Tertiary,
    background = Color(0xFF1A1A1A),
    surface = Color(0xFF2D2D2D),
    error = Error
)
```

---

## 🔤 Paso 4: Crear el Archivo de Tipografía

**Archivo**: `app/src/main/java/com/ramsalapps/flashcards/designsystem/theme/Type.kt`

```kotlin
package com.ramsalapps.flashcards.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val FlashCardsTypography = Typography(
    displayLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 40.sp,
    ),
    displayMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
    ),
    headlineSmall = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
    )
)
```

---

## 🎭 Paso 5: Crear el Tema General

**Archivo**: `app/src/main/java/com/ramsalapps/flashcards/designsystem/theme/Theme.kt`

```kotlin
package com.ramsalapps.flashcards.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable

@Composable
fun FlashCardsTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = FlashCardsTypography,
        content = content
    )
}
```

---

## 🧩 Paso 6: Crear Componentes Base

### DesignSystemButton

**Archivo**: `app/src/main/java/com/ramsalapps/flashcards/designsystem/components/buttons/DesignSystemButton.kt`

```kotlin
package com.ramsalapps.flashcards.designsystem.components

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ramsalapps.flashcards.designsystem.DesignSystemPatterns

@Composable
fun DesignSystemButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = DesignSystemPatterns.Colors.Primary,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            disabledContainerColor = DesignSystemPatterns.Colors.Surface
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(
            DesignSystemPatterns.BorderRadius.sm
        ),
        enabled = enabled
    ) {
        Text(text = text)
    }
}
```

---

## 📱 Paso 7: Aplicar en MainActivity

**Archivo**: `app/src/main/java/com/ramsalapps/flashcards/MainActivity.kt`

```kotlin
package com.ramsalapps.flashcards

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.ramsalapps.flashcards.designsystem.theme.FlashCardsTheme
import com.ramsalapps.flashcards.ui.screens.DashboardScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlashCardsTheme {
                // Tu contenido principal aquí
                DashboardScreen()
            }
        }
    }
}
```

---

## ✅ Checklist de Integración

- [ ] Crear carpeta `designsystem/`
- [ ] Crear `DesignSystemPatterns.kt` con constantes
- [ ] Crear tema en carpeta `theme/`
- [ ] Implementar `Color.kt`
- [ ] Implementar `Type.kt`
- [ ] Implementar `Theme.kt`
- [ ] Crear componentes base (Button, Card, etc.)
- [ ] Aplicar tema en MainActivity
- [ ] Probar en diferentes pantallas
- [ ] Actualizar todas las screens

---

## 🚀 Próximos Pasos

1. **Implementa los componentes base** (Button, Card, TextField, etc.)
2. **Crea los componentes específicos** (DeckCard, FlashcardView, etc.)
3. **Aplica en cada pantalla** (Dashboard, StudySession, etc.)
4. **Valida con checklist** (VALIDATION_CHECKLIST.md)
5. **Prueba en múltiples dispositivos**

---

## 📚 Documentación de Referencia

- **Especificaciones**: [ANDROID_MOBILE_DESIGN_SKILL.md](./docs/ANDROID_MOBILE_DESIGN_SKILL.md)
- **Ejemplos FlashCards**: [FLASHCARDS_IMPLEMENTATION.md](./docs/FLASHCARDS_IMPLEMENTATION.md)
- **Referencia Rápida**: [SKILL_QUICK_REFERENCE.md](./docs/SKILL_QUICK_REFERENCE.md)
- **Validación**: [VALIDATION_CHECKLIST.md](./docs/VALIDATION_CHECKLIST.md)

---

**Guía de Integración v1.0.0**  
**Última actualización**: 03 Feb 2026
