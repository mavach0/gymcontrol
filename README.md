# GymTracker 🏋️‍♂️

Aplicación Android moderna para seguimiento de entrenamientos en el gimnasio, desarrollada con **Kotlin** y **Jetpack Compose** (Material 3) con una estética oscura de alto contraste.

---

## 📱 Características Principales

- **Diseño Dark Mode de Alto Contraste**: Paleta oscura profunda (*Onyx/Slate*) con acentos *Neon Volt / Electric Lime* y badges por grupo muscular.
- **Registro Activo de Series**: Control de pesos (kg), repeticiones y selector rápido de finalización de serie.
- **Temporizador de Descanso Integrado**: Cuenta regresiva automática al completar una serie.
- **Dashboard de Progreso (Home)**: Visualizador de racha semanal, cálculo de volumen acumulado (kg levantados) y accesos rápidos.
- **Catálogo de Ejercicios**: Filtrado instantáneo por grupos musculares (Pecho, Espalda, Piernas, Hombros, Brazos, Core) y barra de búsqueda en tiempo real.
- **Historial Completo**: Registro detallado de sesiones anteriores, volumen total y series completadas.

---

## 🛠️ Tecnologías y Dependencias

- **Lenguaje**: Kotlin 2.0.21
- **UI Framework**: Jetpack Compose con Compose Compiler 2.0+
- **Diseño**: Material 3 + Material Icons Extended
- **Navegación**: Navigation Compose 2.8.3
- **Arquitectura**: Clean Architecture + MVVM (Model-View-ViewModel) con Kotlin Coroutines y StateFlow
- **Configuración de Dependencias**: Gradle Version Catalog (`gradle/libs.versions.toml`)

---

## 📂 Estructura del Proyecto

```text
gymcontrol/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/gymtracker/app/
│   │   │   │   ├── data/
│   │   │   │   │   └── repository/        # Repositorio y persistencia (InMemoryWorkoutRepository)
│   │   │   │   ├── domain/
│   │   │   │   │   ├── model/             # Modelos de dominio (Exercise, WorkoutSession, WorkoutSet, etc.)
│   │   │   │   │   └── repository/        # Interfaces de repositorio (WorkoutRepository)
│   │   │   │   ├── ui/
│   │   │   │   │   ├── components/        # Componentes reutilizables (StatCard, WorkoutCard, SetTrackerRow, etc.)
│   │   │   │   │   ├── navigation/        # Rutas y Bottom Navigation (NavGraph, Screen)
│   │   │   │   │   ├── screens/           # Pantallas (Home, ActiveWorkout, Exercises, History)
│   │   │   │   │   └── theme/             # Paleta Dark Mode, Tipografía, Formas y Tema M3
│   │   │   │   ├── GymTrackerApplication.kt
│   │   │   │   └── MainActivity.kt
│   │   │   ├── res/                       # Recursos XML, íconos vectoriales y temas
│   │   │   └── AndroidManifest.xml
│   │   └── test/                          # Pruebas unitarias de cálculo de volumen y series
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── gradle/
│   ├── libs.versions.toml                 # Version Catalog de Gradle
│   └── wrapper/
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```

---

## 🚀 Cómo abrir y ejecutar el proyecto

1. Abre **Android Studio** (versión Jellyfish, Koala, Ladybug o superior recomendada).
2. Selecciona **Open** y abre la carpeta del proyecto: `gymcontrol`.
3. Deja que Gradle sincronice las dependencias mediante el *Version Catalog* (`libs.versions.toml`).
4. Conecta un dispositivo físico o inicia un emulador Android (API 26 o superior).
5. Haz clic en **Run 'app'** (`Shift + F10`).
