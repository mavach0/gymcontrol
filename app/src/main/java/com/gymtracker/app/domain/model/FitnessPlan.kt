package com.gymtracker.app.domain.model

enum class FitnessGoal(val title: String, val subtitle: String) {
    BUILD_MUSCLE("Ganar Músculo", "Hipertrofia, volumen y ganancia de masa magra"),
    GAIN_STRENGTH("Aumentar Fuerza", "Sobrecarga progresiva, básicos y potencia"),
    LOSE_FAT("Definir y Tonificar", "Mayor gasto calórico, densidad y mantener tono")
}

enum class TrainingLocation(val title: String, val subtitle: String) {
    GYM("Gimnasio", "Acceso a barras, mancuernas, máquinas y poleas"),
    HOME("En Casa", "Mancuernas y ejercicios de peso corporal")
}

data class PresetPlan(
    val id: String,
    val title: String,
    val goal: FitnessGoal,
    val location: TrainingLocation,
    val daysPerWeek: Int,
    val description: String,
    val exerciseNames: List<String>
)

object PresetPlanCatalog {

    val allPlans = listOf(
        // 1. Gym + Músculo
        PresetPlan(
            id = "plan_gym_muscle",
            title = "Torso - Pierna Clásica",
            goal = FitnessGoal.BUILD_MUSCLE,
            location = TrainingLocation.GYM,
            daysPerWeek = 4,
            description = "División óptima de frecuencia 2 para máxima hipertrofia con ejercicios compuestos y analíticos.",
            exerciseNames = listOf(
                "Press de Banca Plano",
                "Remo con Barra Pendlay",
                "Sentadilla Trasera con Barra",
                "Prensa de Piernas 45°",
                "Press Militar con Barra",
                "Extensiones de Tríceps en Polea"
            )
        ),
        // 2. Gym + Fuerza
        PresetPlan(
            id = "plan_gym_strength",
            title = "Fuerza Básica 5x5",
            goal = FitnessGoal.GAIN_STRENGTH,
            location = TrainingLocation.GYM,
            daysPerWeek = 3,
            description = "Foco en los tres levantamientos principales y accesorios clave para construir fuerza pura.",
            exerciseNames = listOf(
                "Sentadilla Trasera con Barra",
                "Press de Banca Plano",
                "Peso Muerto Convencional",
                "Press Militar con Barra",
                "Dominadas Pronas"
            )
        ),
        // 3. Gym + Definir
        PresetPlan(
            id = "plan_gym_fat_loss",
            title = "Push - Pull - Legs Metabólico",
            goal = FitnessGoal.LOSE_FAT,
            location = TrainingLocation.GYM,
            daysPerWeek = 4,
            description = "Entrenamientos estructurados con descansos controlados para retener músculo y acelerar el gasto calórico.",
            exerciseNames = listOf(
                "Press Inclinado con Mancuernas",
                "Fondos en Paralelas",
                "Jalón al Pecho en Polea",
                "Curl Femoral Tumbado",
                "Elevaciones Laterales",
                "Rueda Abdominal (Ab Wheel)"
            )
        ),
        // 4. Casa + Músculo
        PresetPlan(
            id = "plan_home_muscle",
            title = "Hipertrofia con Mancuernas en Casa",
            goal = FitnessGoal.BUILD_MUSCLE,
            location = TrainingLocation.HOME,
            daysPerWeek = 4,
            description = "Rutina completa para desarrollar músculo desde casa con mancuernas ajustables y calistenia.",
            exerciseNames = listOf(
                "Press Inclinado con Mancuernas",
                "Dominadas Pronas",
                "Curl Martillo",
                "Press Francés con Mancuernas",
                "Plancha Abdominal"
            )
        ),
        // 5. Casa + Fuerza
        PresetPlan(
            id = "plan_home_strength",
            title = "Calistenia & Fuerza Progresiva",
            goal = FitnessGoal.GAIN_STRENGTH,
            location = TrainingLocation.HOME,
            daysPerWeek = 3,
            description = "Desarrolla fuerza relativa y control corporal con movimientos gimnásticos exigentes y variaciones avanzadas.",
            exerciseNames = listOf(
                "Dominadas Pronas",
                "Fondos en Paralelas",
                "Elevación de Piernas Colgado",
                "Rueda Abdominal (Ab Wheel)",
                "Plancha Abdominal"
            )
        ),
        // 6. Casa + Definir
        PresetPlan(
            id = "plan_home_fat_loss",
            title = "Full-Body Funcional & Definición",
            goal = FitnessGoal.LOSE_FAT,
            location = TrainingLocation.HOME,
            daysPerWeek = 3,
            description = "Sesiones dinámicas de cuerpo completo diseñadas para acelerar el metabolismo y quemar grasa.",
            exerciseNames = listOf(
                "Fondos en Paralelas",
                "Dominadas Pronas",
                "Elevaciones Laterales",
                "Plancha Abdominal",
                "Elevación de Piernas Colgado"
            )
        )
    )

    fun getRecommendedPlan(goal: FitnessGoal, location: TrainingLocation): PresetPlan {
        return allPlans.firstOrNull { it.goal == goal && it.location == location }
            ?: allPlans.first()
    }
}
