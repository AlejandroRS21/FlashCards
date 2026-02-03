# .gitignore Configuration

**Proyecto**: FlashCards  
**Status**: ✅ Configurado  
**Fecha**: 03 Feb 2026

---

## 📋 Qué se Ignora

### 🔧 Android Studio / IntelliJ IDEA
```
- .idea/                  (Configuración del IDE)
- *.iml                   (Archivos de módulo)
- .project, .classpath    (Configuración Eclipse)
```

### 📦 Gradle
```
- .gradle/                (Cache de Gradle)
- **/build/               (Directorios de compilación)
- gradle-wrapper.jar      (Wrapper)
```

### 🏗️ Android Build
```
- /local.properties       (Propiedades locales)
- *.apk                   (APK compilados)
- *.aab                   (App Bundles)
- .externalNativeBuild/   (Build NDK)
- .cxx/                   (Cache C++)
```

### 💻 OS Files
```
- .DS_Store               (macOS)
- Thumbs.db               (Windows)
- .Spotlight-V100         (macOS indexing)
```

### 📝 Temp & Backup
```
- *.bak, *.tmp, *~        (Archivos temporales)
- .#*                     (Archivos de bloqueo)
```

### 📊 Logs & Reports
```
- *.log                   (Archivos de log)
- lint-results*.xml       (Reportes de lint)
- crash_reports/          (Reportes de crash)
```

### 🔐 Sensitive
```
- *.jks, *.keystore       (Keystores)
- .env, .env.local        (Variables de entorno)
- **/build.gradle.kts.bak (Backups)
```

### 📂 Project Specific
```
- skills/**/build/        (Builds dentro de skills/)
- output.log              (Log de output)
```

---

## ✅ Qué SÍ se Incluye en Git

```
✅ src/                   (Código fuente)
✅ res/                   (Recursos Android)
✅ build.gradle.kts       (Configuración Gradle)
✅ settings.gradle.kts    (Settings Gradle)
✅ proguard-rules.pro     (ProGuard rules)
✅ README.md              (Documentación)
✅ skills/                (Documentación de skills)
✅ AndroidManifest.xml    (Manifest)
✅ .gitignore             (Este archivo)
```

---

## 🚀 Uso

Este `.gitignore` está optimizado para:
- ✅ Android Studio / IntelliJ IDEA
- ✅ Gradle (Java 8+, Kotlin)
- ✅ Android NDK
- ✅ macOS, Windows, Linux
- ✅ Jetpack Compose
- ✅ Proyectos con múltiples módulos

---

## 📝 Cambios Respecto al Original

| Cambio | Razón |
|--------|-------|
| **Agregado**: Toda la carpeta `.idea/` | Evitar incluir todas las configuraciones del IDE |
| **Agregado**: `gradle-wrapper.jar` | No es necesario en el repo |
| **Agregado**: Archivos de backup (`.bak`, `*.tmp`) | Evitar archivos temporales |
| **Agregado**: Archivos de log | No son parte del código |
| **Agregado**: Keystores y certificados | Seguridad (no exponerlos) |
| **Agregado**: Organizados por categorías | Mejor mantenibilidad |
| **Mantenido**: `local.properties` | Específico del ambiente |

---

## 🔒 Archivos Sensibles Ignorados

⚠️ Los siguientes archivos sensibles son ignorados por seguridad:
```
- *.jks              (Java KeyStore - certificados)
- *.keystore         (Keystores Android)
- .env               (Variables de entorno)
- local.properties   (Configuración local)
```

**Nota**: Si necesitas compartir credenciales, usa `.env.example` con valores placeholder.

---

## 📚 Más Información

Ver [.gitignore](./.gitignore) para la lista completa.

---

**Status**: ✅ Configurado correctamente
