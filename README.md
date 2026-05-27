# FlashCards

## Resumen
FlashCards es una aplicación Android orientada al aprendizaje mediante tarjetas didácticas (flashcards). Su objetivo es facilitar la memorización activa mediante sesiones de estudio breves, con gestión local de mazos e importación masiva de contenidos desde archivos CSV.

## Propósito académico
El proyecto se concibe como una herramienta de apoyo al estudio autónomo y estructurado. La aplicación permite:
- organizar contenidos por mazos temáticos;
- transformar material tabular en tarjetas de pregunta y respuesta;
- realizar prácticas de repaso con seguimiento básico de avance.

## Funcionalidades principales
1. **Gestión de mazos**: creación, edición y eliminación de colecciones de tarjetas.
2. **Importación CSV**: lectura y validación de archivos con pares pregunta–respuesta.
3. **Sesión de estudio**: navegación entre tarjetas, volteo de contenido y control de progreso.
4. **Persistencia local**: almacenamiento de datos en el dispositivo para uso sin conexión.

## Requisitos técnicos
- Android Studio (versión reciente)
- JDK 11 o superior
- SDK mínimo de Android: 33

## Estructura general del proyecto
- `/app/src/main/java/com/ramsalapps/flashcards/`: lógica principal de la aplicación.
- `/app/src/main/java/com/ramsalapps/flashcards/ui/`: pantallas y componentes de interfaz.
- `/app/src/main/java/com/ramsalapps/flashcards/data/`: acceso y gestión de datos.
- `/app/src/main/res/`: recursos visuales y de configuración.

## Formato esperado para importación CSV
Cada línea debe contener dos columnas separadas por coma:
1. Pregunta
2. Respuesta

Ejemplo conceptual: `Pregunta,Respuesta`

## Ejecución del proyecto
1. Clonar el repositorio.
2. Abrir el proyecto en Android Studio.
3. Sincronizar dependencias Gradle.
4. Ejecutar la aplicación en emulador o dispositivo físico.

## Tecnologías empleadas
- Kotlin
- Jetpack Compose
- Material Design 3
- Gradle

## Licencia
No se ha definido una licencia explícita en este repositorio.
