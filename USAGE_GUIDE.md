# Guía de Uso - Importación de Flashcards

## ¿Cómo importar flashcards?

### Paso 1: Preparar tu archivo CSV
El archivo debe estar en formato CSV con el siguiente estructura:
```
Pregunta 1,Respuesta 1
Pregunta 2,Respuesta 2
Pregunta 3,Respuesta 3
```

**Ejemplo válido:**
```
¿Cuál es la capital de Francia?,París
¿Cuál es la capital de España?,Madrid
¿Cuál es la capital de Italia?,Roma
```

### Paso 2: Iniciar importación
1. En la pantalla de Dashboard, presiona el botón **"Library"** en la barra inferior
2. Presiona **"Import"** o el ícono de importación

### Paso 3: Seleccionar archivo
1. Presiona el botón **"Browse Files"**
2. Selecciona tu archivo CSV desde tu dispositivo
3. El nombre del archivo se mostrará automáticamente

### Paso 4: Nombrar tu deck
1. En el campo **"Deck Name"** escribe el nombre para tu colección
   - Por ejemplo: "Capitales de Europa", "Biología 101", etc.
2. Este nombre aparecerá como la categoría de todas las tarjetas

### Paso 5: Procesar CSV
1. Presiona el botón **"Process CSV"**
2. El sistema parseará el archivo y mostrará un preview

### Paso 6: Revisar y crear
1. Verás un preview de las primeras 5 tarjetas
2. Se mostrará el número total de tarjetas
3. Puedes editar el nombre del deck si lo deseas
4. Presiona **"Create Deck"** para guardar

### Paso 7: Listo
- Serás devuelto a Dashboard automáticamente
- Tu nuevo deck aparecerá en la sección **"My Decks"**
- Todas las tarjetas se han guardado en el dispositivo

---

## Validaciones

El sistema verifica que:
- ✅ El archivo esté seleccionado
- ✅ El nombre del deck no esté vacío
- ✅ El formato CSV sea válido (pregunta, respuesta)
- ✅ Haya al menos una tarjeta en el archivo

Si algo no es válido, verás un mensaje de error explicativo.

---

## Preguntas Frecuentes

**P: ¿Qué pasa si el archivo tiene más de 2 columnas?**
R: Se usan solo la primera (pregunta) y segunda (respuesta). Las demás se ignoran.

**P: ¿Puedo editar un deck después de crearlo?**
R: No en esta versión. Tienes que crear uno nuevo. Considera eliminar el anterior.

**P: ¿Dónde se guardan mis decks?**
R: Se guardan en el almacenamiento local del dispositivo (SharedPreferences) en formato JSON.

**P: ¿Puedo exportar mis decks?**
R: No en esta versión, pero está en el roadmap.

**P: ¿Qué pasa si reinicio la app?**
R: Todos tus decks se guardan automáticamente y aparecerán cuando reinicies.

---

## Solución de problemas

### "No se encontraron flashcards en el archivo"
- Verifica que tu CSV tenga al menos una línea con el formato correcto
- Asegúrate de que haya una coma separando pregunta y respuesta
- Intenta con un archivo más simple primero

### "Error al procesar el archivo"
- Verifica que el archivo sea un CSV válido
- Intenta renombrarlo para asegurar que tiene extensión .csv
- Revisa que no haya caracteres especiales problemáticos

### El deck no aparece después de crear
- Intenta volver atrás y reintentar
- Si persiste, reinicia la aplicación

---

## Formato CSV recomendado

Usa estas herramientas para crear/editar CSV:
- **Excel/Google Sheets**: Exporta como CSV
- **NotebookLM**: Exporta directamente a CSV
- **Editor de texto**: Crea manualmente con estructura correcta

### Ejemplo en Excel:
| Pregunta | Respuesta |
|----------|-----------|
| ¿Cuál es...? | Respuesta |
| ¿Quién es...? | Respuesta |

Luego exporta como CSV.
