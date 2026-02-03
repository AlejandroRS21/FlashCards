# 🚀 Android Mobile Design - Inicio Rápido

**Proyecto**: FlashCards | **Skill**: v1.0.0 | **Status**: ✅ Configurada

---

## 👤 Sigue tu Rol

| Rol | Pasos |
|-----|-------|
| **👨‍💻 Developer** | 1. `skills/android-mobile-design/docs/SKILL_QUICK_REFERENCE.md` (consulta rápida)<br>2. `skills/android-mobile-design/docs/INTEGRATION_GUIDE.md` (7 pasos)<br>3. Implementa en `app/src/main/java/designsystem/` |
| **👨‍💼 Manager** | 1. `skills/README.md`<br>2. `skills/android-mobile-design/docs/VALIDATION_CHECKLIST.md`<br>3. Planifica 4 fases |
| **🎨 Designer** | 1. `skills/android-mobile-design/docs/ANDROID_MOBILE_DESIGN_SKILL.md`<br>2. Busca: "Sistema de Colores"<br>3. Coordina con equipo |
| **🏗️ Architect** | 1. `skills/android-mobile-design/docs/ANDROID_MOBILE_DESIGN_SKILL.md` (completo)<br>2. `skills/android-mobile-design/docs/INTEGRATION_GUIDE.md`<br>3. Planifica en `VALIDATION_CHECKLIST.md` |

---

## 📂 Documentación (6 archivos)

**`skills/android-mobile-design/docs/`**

- **README.md** — Guía de inicio
- **ANDROID_MOBILE_DESIGN_SKILL.md** — Sistema completo (50+ patrones, 10 componentes, accesibilidad)
- **SKILL_QUICK_REFERENCE.md** — Colores, spacing, code snippets
- **FLASHCARDS_IMPLEMENTATION.md** — Ejemplos en Kotlin
- **INTEGRATION_GUIDE.md** — 7 pasos setup Android
- **VALIDATION_CHECKLIST.md** — Control de implementación

---

## 🎨 Sistema de Diseño

| Aspecto | Valores |
|---------|---------|
| **Spacing** | `xs(4dp)` `sm(8dp)` `md(12dp)` `lg(16dp)` `xl(24dp)` `xxl(32dp)` |
| **Colores** | 🟣`#7C63D8` 🌸`#FF8ECD` 🔷`#7CEFFF` ⚪`#FFF` ⚫`#F5F5F5` 🔴`#FF6B6B` |
| **Radius** | `0dp` `4dp` `8dp` `12dp` `16dp` `999dp` |
| **Componentes** | Button, Card, ListItem, TextField, TopAppBar, BottomNav, Divider, Text |

---

## ⚡ Acciones Rápidas

```kotlin
// Archivo: DesignSystemPatterns.kt
object DesignSystemPatterns {
    object Spacing {
        val xs = 4.dp
        val sm = 8.dp
        val md = 12.dp
        val lg = 16.dp
        val xl = 24.dp
        val xxl = 32.dp
    }
    object Colors {
        val Primary = Color(0xFF7C63D8)      // Morado
        val Secondary = Color(0xFFFF8ECD)    // Rosa
        val Tertiary = Color(0xFF7CEFFF)     // Cyan
        val Background = Color(0xFFFFFFFF)   // Blanco
        val Surface = Color(0xFFF5F5F5)      // Gris claro
        val Error = Color(0xFFFF6B6B)        // Rojo
    }
}
```

---

**Más info**: Abre `skills/README.md` o directamente a `skills/android-mobile-design/docs/`
