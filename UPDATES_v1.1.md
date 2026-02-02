# Guía Actualizada - Nuevas Features v1.1

## ✨ Nuevas Funcionalidades Agregadas

### 1. **Colores Pastel Mejorados en Flashcards**
Los colores ahora son más visibles y atractivos:
- Misty Rose (Rosa suave)
- Lavender Blush (Lavanda suave)
- Alice Blue (Azul claro)
- Mint Cream (Verde menta)
- Lemon Chiffon (Amarillo limón)
- Moccasin (Color café claro)

### 2. **Gestos de Deslizamiento (Swipe)**
Ahora puedes navegar entre flashcards con gestos:
- **Desliza a la izquierda**: Ir a la siguiente tarjeta
- **Desliza a la derecha**: Ir a la tarjeta anterior
- Los botones Previous/Next siguen funcionando

### 3. **Editar Nombre del Deck**
Puedes editar el nombre del deck durante una sesión de estudio:
- Presiona el ícono ✎ (lápiz) en la barra de título
- Se abrirá un diálogo para editar el nombre
- Los cambios se guardan automáticamente
- El nombre se actualiza en el dashboard

---

## 🎮 Cómo Usar los Nuevos Gestos

### Swipe para Navegar
1. En la pantalla de estudio, visualiza una flashcard
2. **Para siguiente**: Desliza el dedo hacia la izquierda
3. **Para anterior**: Desliza el dedo hacia la derecha
4. La tarjeta se voltea automáticamente a la pregunta
5. Si alcanzas la primera/última tarjeta, el gesto no hace nada

### Editar Nombre
1. Durante una sesión de estudio, mira la barra de títulos
2. Junto al nombre del deck, verás un ícono ✎ (lápiz)
3. Presiona el ícono para abrir el diálogo de edición
4. Escribe el nuevo nombre
5. Presiona "Save" para guardar los cambios
6. El nombre se actualiza automáticamente en el dashboard

---

## 🎨 Colores en Acción

Cada tarjeta tiene un color pastel diferente dependiendo de la posición:

```
Tarjeta 1, 4, 7... → Misty Rose (rosa suave)
Tarjeta 2, 5, 8... → Lavender Blush (lavanda)
Tarjeta 3, 6, 9... → Alice Blue (azul claro)
Tarjeta 4, 7...    → Mint Cream (verde menta)
Tarjeta 5, 8...    → Lemon Chiffon (amarillo)
Tarjeta 6, 9...    → Moccasin (café claro)
```

Los colores se repiten cíclicamente para mantener variedad visual.

---

## 📱 Pantalla de Estudio Mejorada

```
┌─────────────────────────────────────┐
│ ✕  Biología 101  ✎  📋             │  ← Ahora puedes editar
├─────────────────────────────────────┤
│ Session Progress                    │
│ Card 3 / 10                         │
│ ████░░░░░░░░░░░░░░░░░░░░░░         │
├─────────────────────────────────────┤
│                                     │
│      ┌──────────────────────┐       │
│      │  [Color Pastel]      │       │ ← Nuevos colores
│      │                      │       │
│      │  ¿Pregunta?          │       │
│      │                      │       │
│      └──────────────────────┘       │
│                                     │
│      Tap to see answer              │
│      Swipe left/right to navigate   │ ← Nueva opción
│                                     │
│  [◀ Previous]  [Next ▶]            │
└─────────────────────────────────────┘
```

---

## ⚙️ Cambios Técnicos

### Imports Agregados
```kotlin
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.absoluteValue
```

### Funciones Nuevas
- `goToNext()` - Navega a la siguiente tarjeta
- `goToPrevious()` - Navega a la tarjeta anterior
- `detectHorizontalDragGestures()` - Detecta gestos de deslizamiento

### Variables Nuevas
- `showEditDialog` - Controla si el diálogo de edición está visible
- `editedDeckName` - Almacena el nombre editado

---

## 🔄 Flujo de Edición de Nombre

```
Usuario presiona ✎
        ↓
Se abre AlertDialog
        ↓
Usuario escribe nuevo nombre
        ↓
Presiona "Save"
        ↓
Se actualiza el deck en DataManager
        ↓
Se recarga la lista de decks en Dashboard
        ↓
Nombres se sincroniza en todas partes
```

---

## 📊 Gestión de Swipe

El gesto de swipe funciona así:

```
Deslizar hacia la izquierda (<-)
        ↓
dragAmount < 0 (negativo)
        ↓
if (dragAmount.absoluteValue > 100dp) → goToNext()
        ↓
Ir a siguiente tarjeta

---

Deslizar hacia la derecha (->)
        ↓
dragAmount > 0 (positivo)
        ↓
if (dragAmount.absoluteValue > 100dp) → goToPrevious()
        ↓
Ir a tarjeta anterior
```

Se requieren al menos 100dp de movimiento para activar el gesto.

---

## 🐛 Casos Especiales

### En la Primera Tarjeta
- No puedes deslizar hacia la derecha
- El gesto se ignora
- Solo "Next" está disponible

### En la Última Tarjeta
- No puedes deslizar hacia la izquierda
- El gesto se ignora
- Solo "Previous" está disponible

### Con Deck de Una Tarjeta
- Los botones no se muestran
- No hay gestos de swipe disponibles
- Solo puedes girar la tarjeta (tap)

---

## ✅ Testing

### Probar Swipe
1. Abre un deck con múltiples tarjetas
2. Intenta deslizar hacia la izquierda (siguiente)
3. Intenta deslizar hacia la derecha (anterior)
4. Verifica que funcionan correctamente
5. Comprueba que los botones Previous/Next siguen funcionando

### Probar Edición de Nombre
1. Abre un deck
2. Presiona el ícono ✎
3. Cambia el nombre a algo diferente
4. Presiona "Save"
5. Verifica que el nombre cambió en la pantalla
6. Cierra la sesión
7. Vuelve al dashboard
8. Comprueba que el nombre se actualizó en la lista

### Probar Colores
1. Abre un deck con múltiples tarjetas
2. Navega entre tarjetas
3. Verifica que cada tarjeta tiene un color diferente
4. Los colores deben ser visibles y bonitos

---

## 🎯 Conclusión

Ahora tienes:
✅ Colores pastel mejorados
✅ Navegación por swipe
✅ Edición de nombres de decks
✅ Mejor experiencia de usuario

¡La app es más intuitiva y funcional! 🚀
