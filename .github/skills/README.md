# 🎨 Android Mobile Design Skill

**Versión**: 1.0.0  
**Proyecto**: FlashCards  
**Estado**: En implementación (50% cumplimiento)

---

## 📚 Documentación

### Para Desarrolladores

- **[ANDROID_MOBILE_DESIGN_SKILL.md](./android-mobile-design/docs/ANDROID_MOBILE_DESIGN_SKILL.md)** — Especificaciones técnicas completas
  - Design system: espaciado, colores, tipografía, border radius
  - Componentes: Button, Card, TextField, ListItem, TopAppBar, BottomNav
  - Pantallas: Dashboard, StudySession, Import, Settings
  - Patrones responsivos y accesibilidad

- **[SKILL_QUICK_REFERENCE.md](./android-mobile-design/docs/SKILL_QUICK_REFERENCE.md)** — Referencia rápida
  - Tabla de colores con hex
  - Tabla de espaciado xs/sm/md/lg/xl/xxl
  - Tamaños de componentes (Button 36/44/52dp, Card radius 12dp)
  - Breakpoints responsivos

- **[INTEGRATION_GUIDE.md](./android-mobile-design/docs/INTEGRATION_GUIDE.md)** — Guía de implementación Android
  - 7 pasos para integrar en Jetpack Compose
  - Ejemplos de código Kotlin
  - Material3 setup con custom theme
  - Accesibilidad WCAG AA

### Para QA / Validación

- **[VALIDATION_CHECKLIST.md](./android-mobile-design/docs/VALIDATION_CHECKLIST.md)** — Criterios de aceptación
  - 7 categorías de validación
  - Per-screen checklist
  - Estado actual: 50% cumplimiento
  - Gaps identificados y prioridades

### Para Implementación

- **[FLASHCARDS_IMPLEMENTATION.md](./android-mobile-design/docs/FLASHCARDS_IMPLEMENTATION.md)** — Casos de uso proyecto
  - Cómo se implementa cada pantalla
  - Patrones de uso específicos
  - Ejemplos reales del código

---

## ⚙️ Configuración

Archivo: `skill.config.json`

Define:
- **Spacing**: xs (4dp) → xxl (32dp)
- **Colors**: 6 primarios (Accent, Pastel variants)
- **Border Radius**: none (0dp) → full (999dp)
- **Typography**: bodyLarge, titleMedium, labelSmall

---

## 📊 Estado Actual (50% Cumplimiento)

| Categoría | Estado | % | Notas |
|-----------|--------|---|-------|
| Design System | ⚠️ Parcial | 45% | Colores ✅, Spacing ❌ variables |
| Componentes | ⚠️ Parcial | 35% | Material3 ✅, Custom wrappers ❌ |
| Pantallas | ✅ Cumple | 80% | 5 pantallas implementadas |
| Responsive | ❌ Falta | 0% | Sin breakpoints |
| Accesibilidad | ⚠️ Parcial | 55% | ContentDesc ✅, WCAG ❌ |
| Testing | ⚠️ Parcial | 35% | Previews ✅, Unit tests ❌ |
| Documentación | ⚠️ Parcial | 50% | README ✅, Código ❌ |

---

## 🎯 Próximas Tareas (Prioridad)

### 1️⃣ Crear Sistema de Espaciado (GAP #1 - CRÍTICO)
**Archivo**: `app/src/main/java/com/ramsalapps/flashcards/ui/theme/Spacing.kt`
```kotlin
object DesignSystemPatterns {
    object Spacing {
        val xs = 4.dp
        val sm = 8.dp
        val md = 12.dp
        val lg = 16.dp
        val xl = 24.dp
        val xxl = 32.dp
    }
}
```
**Impacto**: Reemplaza 50+ valores hardcodeado → consistencia en toda la app

### 2️⃣ Implementar Responsive Design (GAP #2 - CRÍTICO)
**Archivo**: `app/src/main/java/com/ramsalapps/flashcards/ui/DashboardScreen.kt`
```kotlin
BoxWithConstraints {
    val columns = when {
        maxWidth < 600.dp -> 1
        maxWidth < 840.dp -> 2
        else -> 3
    }
    LazyVerticalGrid(columns = GridCells.Fixed(columns)) { ... }
}
```
**Impacto**: App adapta a móvil/tablet/desktop

### 3️⃣ Componentes Custom Design System (GAP #3 - ALTO)
**Carpeta**: `app/src/main/java/com/ramsalapps/flashcards/designsystem/components/`
- `DesignSystemButton.kt` (small 36dp, medium 44dp, large 52dp)
- `DesignSystemCard.kt` (elevation 4dp, padding 16dp)
- `DesignSystemTextField.kt` (height 48dp)
- `DesignSystemListItem.kt`
- `DesignSystemTopAppBar.kt` (height 56dp)

**Impacto**: Facilita mantenimiento, single source of truth para componentes

---

## 🔗 Referencias Cruzadas

- **Inicio rápido**: [../../START_HERE.md](../../START_HERE.md)
- **Setup inicial**: [../../SKILL_SETUP.md](../../SKILL_SETUP.md)
- **Proyecto**: [../../README.md](../../README.md)

---

**Mantener actualizado**: Cada cambio en design system debe reflejarse en `skill.config.json` y documentación.
