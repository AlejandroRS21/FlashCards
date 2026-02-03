# Android Mobile Design - MCP Skill

**Versión**: 1.0.0  
**Autor**: MCPMarket  
**Categoría**: Mobile Design & UI/UX  
**Plataforma**: Android, Jetpack Compose  
**Licencia**: MIT

---

## 📋 Descripción

La skill **Android Mobile Design** es un conjunto completo de patrones, componentes y guías de diseño para aplicaciones Android nativas usando Jetpack Compose. Integra las mejores prácticas de Google Material Design 3 con recomendaciones adaptadas específicamente para desarrollo móvil moderno.

Esta skill proporciona:
- **Patrones de diseño** probados y optimizados
- **Componentes reutilizables** del design system
- **Recomendaciones responsivas** basadas en tamaño de pantalla
- **Guías de accesibilidad** WCAG AA compliant
- **Ejemplos de código** listos para usar

---

## 🎯 Objetivos

✅ Garantizar **consistencia visual** en toda la aplicación  
✅ Acelerar el **desarrollo de UI** con componentes pre-diseñados  
✅ Mejorar la **experiencia del usuario** con patrones probados  
✅ Facilitar **responsive design** en diferentes dispositivos  
✅ Cumplir con estándares de **accesibilidad**  
✅ Proporcionar **mejor mantenibilidad** del código

---

## 📚 Contenido de la Skill

### 1. Sistema de Espaciado
```
xs:  4dp    (pequeños detalles)
sm:  8dp    (espacios mini)
md:  12dp   (espacios por defecto)
lg:  16dp   (espacios principales)
xl:  24dp   (espacios entre secciones)
xxl: 32dp   (espacios grandes)
```

### 2. Tipografía
```
10sp  - Extra pequeño (captions)
12sp  - Pequeño (subtítulos)
14sp  - Body (texto principal)
16sp  - Body Large (texto importante)
18sp  - Subtitle
20sp  - Title (títulos medianos)
24sp  - Headline (títulos grandes)
28sp  - Large Headline
32sp  - Display Small
36sp  - Display Medium
40sp  - Display Large
```

### 3. Radio de Esquinas (Border Radius)
```
0dp   - Sin redondeo
4dp   - Muy ligeramente redondeado
8dp   - Redondeado (botones, inputs)
12dp  - Más redondeado (cards)
16dp  - Bastante redondeado (bottom sheets)
999dp - Completamente redondeado (badges)
```

### 4. Componentes Base

#### DesignSystemButton
Botón principal con 3 tamaños: Small (36dp), Medium (44dp), Large (52dp)

**Propiedades recomendadas:**
```
- Altura mínima: 48dp (touch target)
- Padding horizontal: 16dp
- Border radius: 8dp
- Font size: 14sp
- Font weight: 500 (Medium)
- Elevation: 2dp
```

#### DesignSystemCard
Card reutilizable con elevación y esquinas redondeadas

**Propiedades recomendadas:**
```
- Padding interno: 16dp
- Border radius: 12dp
- Elevation: 4dp (normal), 8dp (hover)
- Spacing entre elementos: 8dp
```

#### DesignSystemListItem
Item de lista con soporte para icon, trailing element y acciones

**Propiedades recomendadas:**
```
- Altura mínima: 56dp
- Padding: 12dp
- Font size (título): 14sp
- Font size (subtítulo): 12sp
```

#### DesignSystemDivider
Línea divisoria para separar elementos

**Propiedades recomendadas:**
```
- Color: #EEEEEE
- Grosor: 0.5dp
```

### 5. Sistema de Colores

#### Paleta Primaria (Tema Pastel)
```
Primary:      #7C63D8 (Morado pastel)
Secondary:    #FF8ECD (Rosa pastel)
Tertiary:     #7CEFFF (Cyan pastel)
Background:   #FFFFFF (Blanco)
Surface:      #F5F5F5 (Gris claro)
Error:        #FF6B6B (Rojo)
```

**Variaciones por componente:**
```
On Primary:   #FFFFFF
On Secondary: #FFFFFF
On Tertiary:  #000000
On Error:     #FFFFFF
```

### 6. Patrones de Layout Responsivo

#### Teléfono (<600dp)
```
- Columnas: 1
- Padding: 16dp
- Max width: 480dp
- Tap target: 48dp
```

#### Tablet (600-840dp)
```
- Columnas: 2
- Padding: 24dp
- Max width: 840dp
- Tap target: 56dp
```

#### Desktop (>840dp)
```
- Columnas: 3
- Padding: 32dp
- Max width: 1200dp
- Tap target: 64dp
```

### 7. Componentes de Texto

| Nivel | Font Size | Weight | Uso |
|-------|-----------|--------|-----|
| Display Large | 40sp | 700 | Títulos principales |
| Display Medium | 36sp | 700 | Títulos secundarios |
| Headline | 24sp | 700 | Secciones |
| Title | 20sp | 600 | Subtítulos |
| Subtitle | 18sp | 500 | Etiquetas |
| Body Large | 16sp | 400 | Texto importante |
| Body | 14sp | 400 | Texto principal |
| Small | 12sp | 500 | Detalles |
| Caption | 10sp | 500 | Textos muy pequeños |

### 8. Componentes de Entrada

#### OutlinedTextField
```
- Altura: 48dp
- Padding: 12dp
- Border radius: 8dp
- Font size: 14sp
- Min lines: 1
- Max lines: 4
```

### 9. Componentes de Navegación

#### Bottom Navigation
```
- Altura: 56dp
- Elevation: 8dp
- Icon size: 24dp
- Padding: 8dp
- Spacing: 8dp
```

#### Navigation Rail (Tablet)
```
- Ancho: 80dp
- Icon size: 24dp
- Padding: 12dp
```

### 10. Diálogos y Bottom Sheets

#### Dialog
```
- Max width: 560dp
- Corner radius: 12dp
- Padding: 24dp
- Spacing: 16dp
- Button min width: 80dp
```

#### BottomSheet
```
- Corner radius: 16dp (solo top)
- Elevation: 16dp
- Padding: 24dp
- Scrim color: #000000 @ 32% alpha
```

---

## 🎨 Patrones de Diseño

### Patrón 1: Card Grid Responsivo
```
Teléfono:  [Card]
Tablet:    [Card] [Card]
Desktop:   [Card] [Card] [Card]

Spacing entre cards: 8dp
```

### Patrón 2: Botones de Acción
```
Botón primario (fill):   Full width, Material3 Primary
Botón secundario (outline): Full width, Outlined
Botón terciario (text):  Text only, no background
```

### Patrón 3: Estructura de Pantalla
```
┌─────────────────────┐
│  Top App Bar (56dp) │
├─────────────────────┤
│                     │
│  Scroll Content     │  Padding: 16dp
│                     │
├─────────────────────┤
│ Bottom Nav (56dp)   │
└─────────────────────┘
```

### Patrón 4: Lista con Dividers
```
[ListItem 1]
──────────
[ListItem 2]
──────────
[ListItem 3]
```

### Patrón 5: Card con Acciones
```
┌─────────────────────┐
│ Título (Bold)       │
│ Subtítulo           │
├─────────────────────┤
│ [Botón] [Botón]     │
└─────────────────────┘
```

---

## 🎓 Ejemplos de Uso

### Ejemplo 1: Botón Simple
```kotlin
DesignSystemButton(
    text = "Continuar",
    onClick = { /* acción */ },
    size = ButtonSize.Large,
    backgroundColor = Color(0xFF7C63D8)
)
```

### Ejemplo 2: Card con Contenido
```kotlin
DesignSystemCard(
    backgroundColor = Color(0xFFF5E6FF)
) {
    DesignSystemTitle(text = "Mi Deck")
    DesignSystemBody(text = "50 flashcards")
}
```

### Ejemplo 3: Lista de Items
```kotlin
DesignSystemListItem(
    title = "Flashcard 1",
    subtitle = "Tap para estudiar",
    onClick = { /* ... */ }
)
```

### Ejemplo 4: Layout Responsivo
```kotlin
val columns = DesignSystemPatterns.getColumnCount(screenWidth)
val padding = DesignSystemPatterns.getResponsivePadding(screenWidth)

Column(
    modifier = Modifier
        .fillMaxSize()
        .padding(padding.dp)
) {
    // Renderizar grid con N columnas
}
```

---

## 🔧 Funciones Helper

### getResponsivePadding(screenWidth: Int): Float
Calcula el padding adaptativo según ancho de pantalla.

### getColumnCount(screenWidth: Int): Int
Calcula el número de columnas para grid responsivo.

### getResponsiveFontSize(screenWidth: Int, baseFontSize: Int): Int
Escala el tamaño de fuente según el dispositivo.

---

## ♿ Pautas de Accesibilidad

### Contraste de Color
- **Mínimo requerido**: 4.5:1 (WCAG AA)
- **Enhanced requerido**: 7:1 (WCAG AAA)

### Touch Target
- **Mínimo**: 48x48 dp
- **Recomendado**: 56x56 dp
- **Confortable**: 64x64 dp

### Tamaño de Fuente
- **Mínimo**: 12sp
- **Recomendado para body**: 14-16sp
- **Escalable**: Debe soportar hasta 200% de zoom

---

## 📱 Integración con Jetpack Compose

### Estructura recomendada
```
app/
├── src/main/java/com/ramsalapps/flashcards/
│   ├── ui/
│   │   ├── screens/
│   │   ├── components/
│   │   └── theme/
│   ├── designsystem/
│   └── ...
```

---

## 🚀 Mejores Prácticas

### ✅ Hacer
- Usar constantes de `DesignSystemPatterns` para todos los valores
- Aplicar componentes reutilizables en lugar de crear nuevos
- Mantener márgenes y paddings consistentes
- Probar en múltiples tamaños de pantalla
- Usar estados de composición para actualizar dinámicamente

### ❌ No Hacer
- Hardcodear valores de dimensión (usar constantes)
- Mezclar patrones de diseño diferentes
- Ignorar guías de accesibilidad
- Usar colores no definidos en la paleta
- Crear componentes nuevos sin revisar si existen

---

## 📊 Recomendaciones por Pantalla

### Dashboard/Home
- Grid de cards responsivo
- 1 columna en móvil, 2 en tablet, 3 en desktop
- Cards con sombra (elevation 4dp)
- Spacing de 8dp entre items

### Pantalla de Estudio
- Card grande con contenido
- Botones de navegación: Anterior/Siguiente
- Indicador de progreso
- Espaciado generoso

### Formularios
- Inputs con border radius 8dp
- Label encima del input
- Mensaje de error en rojo (#FF6B6B)
- Altura mínima 48dp

### Listas
- Items de 56dp mínimo
- Icon + Texto + Action
- Dividers de 0.5dp
- Swipe actions opcionales

---

## 🔗 Referencias

- [Material Design 3](https://m3.material.io/)
- [Jetpack Compose Docs](https://developer.android.com/jetpack/compose)
- [Android Design Guidelines](https://developer.android.com/design)
- [WCAG 2.1 Accessibility](https://www.w3.org/WAI/WCAG21/quickref/)

---

**Última actualización**: 03 Feb 2026
