# GymTracker 🏋️‍♂️

[![Descargar APK](https://img.shields.io/badge/Descargar-APK%20(Pruebas)-3B82F6?style=for-the-badge&logo=android&logoColor=white)](https://github.com/mavach0/gymcontrol/releases)

Aplicación Android moderna para seguimiento de entrenamientos en el gimnasio, desarrollada con **Kotlin** y **Jetpack Compose** (Material 3) con una estética oscura seria y profesional (*Charcoal & Cobalt Blue*).

---

## 📲 ¿Cómo descargar e instalar la app para probarla?

1. Desde tu teléfono Android, entra al enlace de lanzamientos:  
   👉 **[Descargar la última versión de GymTracker](https://github.com/mavach0/gymcontrol/releases)**
2. En la sección **Assets**, pulsa sobre `GymTracker.apk` para descargarlo.
3. Abre el archivo descargado en tu teléfono. Si Android te solicita permisos, activa **"Permitir desde esta fuente"** (Instalación de orígenes desconocidos).
4. ¡Listo! Abre **GymTracker** y comienza a probar.

---

## 📱 Características Principales

- **Diseño Dark Mode Minimalista**: Paleta oscura sobria (*Charcoal & Graphite*) con acentos en azul cobalto (*Cobalt Blue*).
- **Onboarding Personalizado**: Configuración inicial de nombre, estatura, peso, objetivo fitness (definir, fuerza, masa muscular) y lugar de entreno (gym o casa).
- **Rutina Predefinida Inteligente**: Recomendación de plan adaptado (Torso/Pierna, 5x5, Hipertrofia con mancuernas, Calistenia, etc.).
- **Registro Activo de Series**: Control de pesos (kg), repeticiones y selector rápido de finalización de serie.
- **Temporizador de Descanso Integrado**: Cuenta regresiva automática al completar una serie.
- **Dashboard de Progreso Real**: Racha y volumen acumulado calculados según tus entrenamientos reales (sin datos falsos).
- **Catálogo de Ejercicios**: Filtrado instantáneo por grupos musculares y barra de búsqueda en tiempo real.
- **Historial Completo**: Registro detallado de sesiones anteriores, volumen total y series completadas.

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
│   │   │   │   ├── data/
│   │   │   │   │   ├── local/         # Preferencias locales de usuario (UserPreferences)
│   │   │   │   │   └── repository/    # Repositorio (InMemoryWorkoutRepository)
│   │   │   │   ├── domain/
│   │   │   │   │   ├── model/         # Modelos de dominio (Exercise, FitnessPlan, WorkoutSession, etc.)
│   │   │   │   │   └── repository/    # Interfaces de repositorio (WorkoutRepository)
│   │   │   │   ├── ui/
│   │   │   │   │   ├── components/    # Componentes reutilizables (StatCard, WorkoutCard, SetTrackerRow, etc.)
│   │   │   │   │   ├── navigation/    # Rutas y Bottom Navigation (NavGraph, Screen)
│   │   │   │   │   ├── screens/       # Pantallas (Home, ActiveWorkout, Exercises, History, PantallaOnboarding)
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
