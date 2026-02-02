# FlashCards

Aplicación Android para estudiar mediante tarjetas de memoria (flashcards) importadas desde archivos CSV.

## Descripción

FlashCards es una aplicación móvil que permite a los usuarios crear y estudiar mazos de tarjetas de memoria. La característica principal es la capacidad de importar tarjetas desde archivos CSV, facilitando la creación masiva de contenido de estudio.

## Funcionalidades

### Gestión de Mazos
- Crear mazos de flashcards desde archivos CSV
- Visualizar lista de mazos disponibles
- Editar nombres de mazos existentes
- Eliminar mazos
- Seguimiento de progreso por mazo

### Importación desde CSV
- Selección de archivos CSV desde el dispositivo
- Validación automática del formato
- Vista previa de las tarjetas antes de importar
- Asignación de nombre personalizado al mazo

### Sistema de Estudio
- Navegación mediante gestos de swipe
- Cambio de color al voltear tarjetas (verde/rojo)
- Modo de lectura biónica opcional
- Contador de progreso durante la sesión

## Formato CSV

El archivo CSV debe seguir este formato:

```
pregunta,respuesta
¿Cuál es la capital de Francia?,París
¿Qué es la fotosíntesis?,Proceso por el cual las plantas convierten luz en energía
```

- Primera columna: pregunta
- Segunda columna: respuesta
- Sin encabezados
- Separador: coma

## Requisitos Técnicos

- Android Studio
- Kotlin
- Jetpack Compose
- Minimum SDK: 33

## Instalación

1. Clonar el repositorio
2. Abrir el proyecto en Android Studio
3. Sincronizar dependencias
4. Ejecutar en emulador o dispositivo físico

## Arquitectura

La aplicación utiliza:
- **Jetpack Compose** para la UI
- **SharedPreferences** para persistencia de datos
- **CSVParser** personalizado para procesamiento de archivos
- **DataManager** para gestión de datos

## Componentes Principales

- `MainActivity.kt` - Punto de entrada y navegación
- `DashboardScreen.kt` - Pantalla principal con lista de mazos
- `ImportScreen.kt` - Flujo de importación de CSV
- `StudySessionScreen.kt` - Pantalla de estudio
- `CSVParser.kt` - Procesamiento de archivos CSV
- `DataManager.kt` - Gestión de persistencia

## Versión

Versión actual: 1.2