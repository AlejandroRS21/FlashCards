# Actualización de Icono de App - FlashCards

## Cambios Realizados

Se ha actualizado el icono de la aplicación para que sea más coherente con la temática de **FlashCards** (tarjetas de aprendizaje).

### Detalles de la Actualización

**Ubicación de archivos modificados:**
- `app/src/main/res/drawable/ic_launcher_background.xml` - Fondo
- `app/src/main/res/drawable/ic_launcher_foreground.xml` - Imagen principal

### Diseño del Nuevo Icono

El nuevo icono presenta:

1. **Fondo**: Color índigo (#6366F1) - transmite educación y profesionalismo
2. **Elemento Principal**: Una tarjeta (flashcard) que representa:
   - **Frente**: Zona de pregunta con un símbolo interrogativo (?)
   - **Reverso**: Zona de respuesta con líneas de texto simuladas
   - Línea divisoria central que separa pregunta y respuesta

### Características

- ✅ Diseño vectorial escalable (funciona en todos los tamaños)
- ✅ Color coherente con el tema educativo
- ✅ Fácilmente identificable como tarjeta de aprendizaje
- ✅ Compatible con Android Adaptive Icons
- ✅ Se adapta automáticamente a todas las densidades de pantalla (mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi)

### Cómo Aplicar

Simplemente reconstruye la aplicación:
```bash
./gradlew clean build
```

El icono se aplicará automáticamente a:
- Icono de launcher en la pantalla de inicio
- Icono redondo (si el dispositivo lo soporta)
- Todas las resolutions requeridas por Android

### Especificaciones del Icono

- **Formato**: Vector XML (Adaptive Icon)
- **Tamaño base**: 108dp x 108dp
- **Colores**:
  - Fondo: #6366F1 (Indigo-500)
  - Tarjeta: #FFFFFF (Blanco)
  - Frente: #6366F1 (Indigo-500)
  - Reverso: #818CF8 (Indigo-400)
  - Detalles: #C7D2FE (Indigo-200)
  - Texto: #FFFFFF (Blanco)

---
**Fecha de actualización**: 2026-02-04
