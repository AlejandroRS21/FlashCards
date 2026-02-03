# 📚 Skills - Android Mobile Design System

Directorio de skills para el proyecto FlashCards. Contiene patrones de diseño, componentes reutilizables y guías de implementación.

## 📂 Estructura

```
skills/
└── android-mobile-design/
    ├── skill.config.json          # Configuración maestra
    ├── docs/                       # Documentación
    │   ├── README.md              # Inicio rápido
    │   ├── ANDROID_MOBILE_DESIGN_SKILL.md
    │   ├── FLASHCARDS_IMPLEMENTATION.md
    │   ├── SKILL_QUICK_REFERENCE.md
    │   ├── INDEX_SKILL_FILES.md
    │   └── VALIDATION_CHECKLIST.md
    ├── components/                 # Componentes Compose
    └── patterns/                   # Patrones de diseño
```

## 🚀 Inicio Rápido

1. **Leer documentación**: Comienza con [docs/README.md](./android-mobile-design/docs/README.md)
2. **Referencia rápida**: Usa [SKILL_QUICK_REFERENCE.md](./android-mobile-design/docs/SKILL_QUICK_REFERENCE.md) para consultas rápidas
3. **Implementación**: Sigue [FLASHCARDS_IMPLEMENTATION.md](./android-mobile-design/docs/FLASHCARDS_IMPLEMENTATION.md)
4. **Validación**: Usa [VALIDATION_CHECKLIST.md](./android-mobile-design/docs/VALIDATION_CHECKLIST.md)

## 📖 Skills Disponibles

### Android Mobile Design (v1.0.0)
- **Descripción**: Sistema completo de patrones y componentes para Android/Jetpack Compose
- **Categoría**: Mobile Design & UI/UX
- **Status**: ✅ Activo
- **Documentación**: [Ir a documentación](./android-mobile-design/docs/README.md)
- **Configuración**: [Ver config](./android-mobile-design/skill.config.json)

## 🔧 Configuración

Cada skill tiene un archivo `skill.config.json` que contiene:
- Metadatos de la skill
- Sistema de diseño (colores, espaciado, bordes)
- Lista de componentes
- Puntos de integración
- Referencias de archivos

## 📋 Cómo usar una skill

1. **Navega a la carpeta de la skill**
   ```
   cd skills/android-mobile-design
   ```

2. **Consulta la configuración**
   ```json
   cat skill.config.json
   ```

3. **Lee la documentación**
   - Comienza con `docs/README.md`
   - Referencia: `docs/SKILL_QUICK_REFERENCE.md`

4. **Implementa los patrones**
   - Sigue `docs/FLASHCARDS_IMPLEMENTATION.md`

5. **Valida la implementación**
   - Usa `docs/VALIDATION_CHECKLIST.md`

## 🎯 Integración con FlashCards

La skill Android Mobile Design está integrada para:
- Unificar el sistema de diseño en toda la app
- Proporcionar componentes reutilizables
- Garantizar responsive design
- Cumplir estándares de accesibilidad WCAG AA
- Acelerar el desarrollo de nuevas pantallas

### Pantallas Incluidas
- ✅ Dashboard/Home
- ✅ Study Session
- ✅ Import
- ✅ Settings

### Implementación
Los componentes se implementan en:
```
app/src/main/java/com/ramsalapps/flashcards/ui/
├── components/     # Componentes reutilizables
├── screens/        # Pantallas principales
└── theme/          # Tema y colores
```

## 📊 Configuración del Sistema de Diseño

### Espaciado
```
xs:  4dp  | sm:  8dp  | md:  12dp
lg:  16dp | xl:  24dp | xxl: 32dp
```

### Colores Primarios
```
Primary:   #7C63D8 (Morado)   | Secondary: #FF8ECD (Rosa)
Tertiary:  #7CEFFF (Cyan)     | Error:     #FF6B6B (Rojo)
Background: #FFFFFF (Blanco)  | Surface:   #F5F5F5 (Gris)
```

### Border Radius
```
xs: 4dp | sm: 8dp | md: 12dp | lg: 16dp | full: 999dp
```

## ✅ Checklist de Configuración

- [x] Crear estructura de carpetas `skills/android-mobile-design`
- [x] Crear archivo `skill.config.json` con configuración maestra
- [x] Organizar documentación en carpeta `docs/`
- [x] Preparar carpeta `components/` para archivos Kotlin
- [x] Preparar carpeta `patterns/` para ejemplos
- [x] Crear este README maestro
- [ ] Implementar componentes en `app/src/main/java/...`
- [ ] Aplicar patrones en pantallas principales
- [ ] Validar con checklist

## 🔗 Referencias

- [Material Design 3](https://m3.material.io/)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [WCAG Accessibility](https://www.w3.org/WAI/WCAG21/quickref/)

## 📝 Notas

- La skill fue creada por MCPMarket (otro LLM)
- Se ha reorganizado en esta estructura para integración adecuada
- Los documentos originales están preservados en `docs/`
- La configuración está centralizada en `skill.config.json`

---

**Última actualización**: 03 Feb 2026
