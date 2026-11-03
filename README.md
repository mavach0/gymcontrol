# GymControl 🏋️‍♂️

[![Descargar APK](https://img.shields.io/badge/Descargar-APK%20(Pruebas)-3B82F6?style=for-the-badge&logo=android&logoColor=white)](https://github.com/mavach0/gymcontrol/releases)

Aplicación Android moderna para seguimiento de entrenamientos en el gimnasio, desarrollada con **Kotlin** y **Jetpack Compose** (Material 3) con una estética oscura seria y profesional (*Charcoal & Cobalt Blue*).

---

## 📲 ¿Cómo descargar e instalar la app para probarla?

1. Desde tu teléfono Android, entra al enlace de lanzamientos:  
   👉 **[Descargar la última versión de GymControl](https://github.com/mavach0/gymcontrol/releases)**
2. En la sección **Assets**, pulsa sobre `GymControl.apk` para descargarlo.
3. Abre el archivo descargado en tu teléfono. Si Android te solicita permisos, activa **"Permitir desde esta fuente"** (Instalación de orígenes desconocidos).
4. ¡Listo! Abre **GymControl** y comienza a probar.

---

## 📱 Características Principales

- **Diseño Dark Mode Minimalista**: Paleta oscura sobria (*Charcoal & Graphite*) con acentos en azul cobalto (*Cobalt Blue*).
- **Onboarding Personalizado**: Configuración inicial de nombre, estatura, peso, objetivo fitness (definir, fuerza, masa muscular) y lugar de entreno (gym o casa).
- **Rutina Predefinida Inteligente**: Recomendación de plan adaptado (Torso/Pierna, 5x5, Hipertrofia con mancuernas, Calistenia, etc.) que se convierte automáticamente en un plan personalizado activo.
- **Planes de Entrenamiento Personalizados**: Crea planes con nombre propio, agrupa los ejercicios que quieras, actívalos y edítalos en cualquier momento.
- **Detalle de Ejercicio**: Ficha con descripción, consejos técnicos, descanso recomendado y acceso directo para añadir el ejercicio al plan activo o al entrenamiento en curso.
- **Registro Activo de Series**: Control de pesos (kg) y repeticiones con contadores libres (mínimo 1 serie y 1 repetición), añadir o quitar series y ejercicios.
- **Temporizador de Descanso Integrado**: Cuenta regresiva automática al completar una serie.
- **Dashboard de Progreso Real**: Racha y volumen acumulado calculados según tus entrenamientos reales (sin datos falsos).
- **Catálogo de Ejercicios**: Filtrado instantáneo por grupos musculares y búsqueda por nombre, equipamiento o grupo muscular.
- **Historial Completo**: Registro detallado de sesiones anteriores, volumen total y series completadas.
- **Persistencia Local Robusta**: Todo el estado (entrenamientos, sesión activa, planes y plan activo) se guarda en un único JSON escrito de forma atómica, con recuperación automática ante ficheros corruptos.

---

## 🛠️ Tecnologías y Dependencias

- **Lenguaje**: Kotlin 2.0.21
- **UI Framework**: Jetpack Compose con Compose Compiler 2.0+
- **Diseño**: Material 3 + Material Icons Extended
- **Navegación**: Navigation Compose 2.8.3
- **Arquitectura**: Clean Architecture + MVVM (Model-View-ViewModel) con Kotlin Coroutines y StateFlow
- **Configuración de Dependencias**: Gradle Version Catalog (`gradle/libs.versions.toml`)
- **CI/CD Automatizado**: GitHub Actions para compilar y publicar el APK en cada actualización.

---

## 📂 Estructura del Proyecto

```text
gymcontrol/
├── .github/
│   └── workflows/
│       └── build-apk.yml              # Compilación y publicación automática del APK
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/gymtracker/app/
│   │   │   │   ├── core/
│   │   │   │   │   └── di/            # Contenedor de dependencias y fábrica de ViewModels
│   │   │   │   ├── data/
│   │   │   │   │   ├── catalog/       # Catálogo de ejercicios (ExerciseCatalog)
│   │   │   │   │   ├── local/         # Preferencias y persistencia JSON atómica (WorkoutStateStore)
│   │   │   │   │   └── repository/    # Implementación del repositorio (LocalWorkoutRepository)
│   │   │   │   ├── domain/
│   │   │   │   │   ├── model/         # Modelos de dominio (Exercise, CustomPlan, WorkoutSession, etc.)
│   │   │   │   │   └── repository/    # Contratos de repositorio (WorkoutRepository)
│   │   │   │   ├── features/
│   │   │   │   │   ├── exercises/     # Catálogo y detalle de ejercicio
│   │   │   │   │   ├── history/       # Historial de sesiones
│   │   │   │   │   ├── home/          # Dashboard de inicio
│   │   │   │   │   ├── onboarding/    # Configuración inicial
│   │   │   │   │   ├── plans/         # Planes personalizados y su detalle
│   │   │   │   │   └── workout/       # Entrenamiento activo
│   │   │   │   ├── ui/
│   │   │   │   │   ├── components/    # Componentes reutilizables (SetTrackerRow, ExerciseListItem, etc.)
│   │   │   │   │   ├── navigation/    # Rutas y Bottom Navigation (NavGraph, Screen)
│   │   │   │   │   └── theme/         # Paleta Dark Mode, Tipografía, Formas y Tema M3
│   │   │   │   ├── GymTrackerApplication.kt
│   │   │   │   └── MainActivity.kt
│   │   │   ├── res/                   # Recursos XML, íconos vectoriales y temas
│   │   │   └── AndroidManifest.xml
│   │   └── test/                      # Pruebas unitarias de cálculo de volumen y series
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── gradle/
│   ├── libs.versions.toml             # Version Catalog de Gradle
│   └── wrapper/
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```

---

## 🚀 Cómo abrir y ejecutar el proyecto localmente

1. Abre **Android Studio** (versión Jellyfish, Koala, Ladybug o superior recomendada).
2. Selecciona **Open** y abre la carpeta del proyecto: `gymcontrol`.
3. Deja que Gradle sincronice las dependencias mediante el *Version Catalog* (`libs.versions.toml`).
4. Conecta un dispositivo físico o inicia un emulador Android (API 26 o superior).
5. Haz clic en **Run 'app'** (`Shift + F10`).
