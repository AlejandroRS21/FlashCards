# Quick Start - Ejecutar la App

## 🚀 En Android Studio

### Opción 1: Interfaz Gráfica (Más Fácil)
```
1. Abre Android Studio
2. File → Open → Selecciona carpeta FlashCards
3. Espera que sincronice (verás progreso abajo)
4. Click en el botón verde "Run" ▶️ (arriba a la derecha)
5. Selecciona tu emulador o dispositivo
6. ¡Listo! Espera a que compile e instale
```

### Opción 2: Terminal
```bash
cd C:\Users\arami\AndroidStudioProjects\FlashCards
.\gradlew.bat installDebug
```

## 📱 Crear Archivo CSV de Prueba

Copia esto en un archivo `test.csv`:

```csv
¿Cuál es la capital de Francia?,París
¿Cuál es la capital de España?,Madrid
¿Cuál es la capital de Italia?,Roma
¿Cuál es la capital de Portugal?,Lisboa
¿Cuál es la capital de Alemania?,Berlín
```

Guarda en tu teléfono/emulador.

## 🧪 Testing Rápido (5 minutos)

### 1. Importar CSV (1 min)
- Click "Library"
- Click "Import"
- Selecciona tu CSV
- Ingresa nombre: "Capitales de Europa"
- Click "Process CSV"
- Click "Create Deck"

### 2. Ver en Dashboard (30 seg)
- Deberías ver el nuevo deck
- Verifica que tenga los colores pastel

### 3. Probar Estudio (2 min)
- Click en el deck
- Prueba:
  - **Swipe izquierda** → Siguiente
  - **Swipe derecha** → Anterior
  - **Tap en tarjeta** → Girar
  - **Click ✎** → Editar nombre

### 4. Verificar Persistencia (1 min)
- Cierra la app completamente
- Abre nuevamente
- Los datos deberían estar ahí

## ✅ Checklist Final

```
□ App se abre sin crashes
□ Dashboard muestra decks
□ Puedo importar CSV
□ Colores son visibles
□ Swipe funciona en ambas direcciones
□ Puedo editar nombre
□ Datos persisten después de cerrar
□ Todo funciona correctamente
```

## 🆘 Si Hay Problemas

### Build falla
```bash
cd C:\Users\arami\AndroidStudioProjects\FlashCards
.\gradlew.bat clean build
```

### App se crashea
- Revisa Logcat en Android Studio
- Busca el error rojo en los logs

### No puedo seleccionar archivo
- Asegúrate que tienes permisos
- Prueba con una ruta más simple

### No se guardan los datos
- Verifica que no sea un emulador sin almacenamiento
- Reinicia el emulador

## 📞 Documentación Completa

Consulta estos archivos para más información:
- `README.md` - Índice completo
- `USAGE_GUIDE.md` - Cómo usar
- `TESTING_GUIDE.md` - Testing detallado
- `TECHNICAL_DOCUMENTATION.md` - Arquitectura

## 🎉 ¡Listo!

Tu app FlashCards está completamente funcional. 
Solo necesitas abrirla en Android Studio y presionar Run.

¡Disfruta estudiando! 📚✨
