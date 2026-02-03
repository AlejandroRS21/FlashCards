# Guía Rápida: Android Mobile Design Skill para FlashCards

---

## 🎯 Quick Reference

### Espaciado Rápido
```
4dp   → DesignSystemPatterns.Spacing.xs
8dp   → DesignSystemPatterns.Spacing.sm
12dp  → DesignSystemPatterns.Spacing.md (por defecto)
16dp  → DesignSystemPatterns.Spacing.lg
24dp  → DesignSystemPatterns.Spacing.xl
32dp  → DesignSystemPatterns.Spacing.xxl
```

### Colores Principales
```
Morado Pastel  → #7C63D8 (Primary)
Rosa Pastel    → #FF8ECD (Secondary)
Cyan Pastel    → #7CEFFF (Tertiary)
Blanco         → #FFFFFF (Background)
Gris Claro     → #F5F5F5 (Surface)
Rojo Error     → #FF6B6B (Error)
```

### Tamaños de Componentes
```
Botón Small    → 36dp altura
Botón Medium   → 44dp altura (por defecto)
Botón Large    → 52dp altura
Icon Small     → 20dp
Icon Medium    → 24dp
Icon Large     → 32dp
Touch Target   → 48dp mínimo
```

---

## 📋 Checklist de Diseño para FlashCards

### Dashboard Screen
- [ ] Grid responsivo (1-3 columnas según pantalla)
- [ ] Cards con elevation 4dp
- [ ] Spacing 8dp entre items
- [ ] Botón flotante (FAB) para crear deck
- [ ] Empty state si no hay decks
- [ ] Loading state mientras carga

### Study Session Screen
- [ ] Card grande (12dp border radius)
- [ ] Título en Headline (24sp)
- [ ] Contenido en Body (14sp)
- [ ] Botones de navegación (Anterior/Siguiente)
- [ ] Indicador de progreso
- [ ] Swipe gestures para navegar
- [ ] Colores distintos por tarjeta

### Import Screen
- [ ] Input field con height 48dp
- [ ] Botones de acción (Large size)
- [ ] Progress bar mientras importa
- [ ] Success/Error state después

### Settings Screen
- [ ] List items con dividers
- [ ] Toggles para opciones
- [ ] Secciones con títulos
- [ ] Scroll si hay muchas opciones

---

## 🔨 Snippets de Código Reutilizable

### Botón Principal
```kotlin
DesignSystemButton(
    text = "Continuar",
    onClick = { /* ... */ },
    size = ButtonSize.Large
)
```

### Card de Contenido
```kotlin
DesignSystemCard(
    modifier = Modifier.fillMaxWidth()
) {
    DesignSystemTitle(text = "Título")
    DesignSystemVerticalSpacer()
    DesignSystemBody(text = "Contenido")
}
```

### Grid Responsivo
```kotlin
val columns = DesignSystemPatterns.getColumnCount(screenWidth)
LazyVerticalGrid(
    columns = GridCells.Fixed(columns),
    horizontalArrangement = Arrangement.spacedBy(
        DesignSystemPatterns.Spacing.md
    ),
    verticalArrangement = Arrangement.spacedBy(
        DesignSystemPatterns.Spacing.md
    )
) {
    items(items.size) { index ->
        // Item
    }
}
```

### Lista con Items
```kotlin
LazyColumn(
    verticalArrangement = Arrangement.spacedBy(
        DesignSystemPatterns.Spacing.sm
    )
) {
    items(items.size) { index ->
        DesignSystemListItem(
            title = items[index].name,
            subtitle = items[index].description,
            onClick = { /* ... */ }
        )
        DesignSystemDivider()
    }
}
```

---

## 🎨 Guía de Colores para Tarjetas

```
Tarjeta 1 → #7C63D8 (Morado)
Tarjeta 2 → #FF8ECD (Rosa)
Tarjeta 3 → #7CEFFF (Cyan)
Tarjeta 4 → #FFB88E (Naranja)
Tarjeta 5 → #88E4FF (Azul claro)
Tarjeta 6 → #FFD6A5 (Melocotón)
```

---

## 📐 Márgenes Estándar

### Pantalla Completa
```
Padding: 16dp (mobile), 24dp (tablet), 32dp (desktop)
```

### Top App Bar
```
Altura: 56dp | Icon size: 24dp
```

### Bottom Navigation
```
Altura: 56dp | Icon size: 24dp
```

### Items en Listas
```
Altura: 56dp | Padding: 12dp
```

---

## 🔍 Debugging & Testing

### Preview en diferentes tamaños
```kotlin
@Preview(widthDp = 412, heightDp = 915)  // Móvil
@Preview(widthDp = 600, heightDp = 800)  // Tablet pequeño
@Preview(widthDp = 1000, heightDp = 900) // Tablet grande
@Composable
fun PreviewAllSizes() {
    MiPantalla()
}
```

### Checklist de accesibilidad
- [ ] Contraste de color > 4.5:1
- [ ] Touch targets ≥ 48dp
- [ ] Font size ≥ 12sp
- [ ] Teclado accesible

---

## ⚡ Tips de Rendimiento

✅ Usar `LazyColumn` y `LazyRow` para listas largas  
✅ Memoizar composables que no cambian frecuentemente  
✅ Usar `remember { }` para estados locales  

---

## 🚨 Problemas Comunes

| Problema | Solución |
|----------|----------|
| Componente se ve diferente | Usar `DesignSystemPatterns.getResponsivePadding()` |
| Texto se corta | Usar `maxLines` y `TextOverflow.Ellipsis` |
| Botón muy pequeño | Asegurar altura ≥ 48dp |
| Colores no coinciden | Usar colores definidos en la paleta |

---

**Para usar con**: Android Mobile Design Skill v1.0.0  
**Actualizado**: 03 Feb 2026
