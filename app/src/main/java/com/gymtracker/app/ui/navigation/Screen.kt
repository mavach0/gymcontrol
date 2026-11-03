package com.gymtracker.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {

    object Onboarding : Screen("onboarding", "Bienvenida", Icons.Default.Home)
    object Home : Screen("home", "Inicio", Icons.Default.Home)
    object ActiveWorkout : Screen("active_workout", "Entrenar", Icons.Default.PlayArrow)
    object Exercises : Screen("exercises", "Ejercicios", Icons.Default.FitnessCenter)
    object Plans : Screen("plans", "Planes", Icons.AutoMirrored.Filled.Assignment)
    object History : Screen("history", "Historial", Icons.Default.DateRange)

    object ExerciseDetail : Screen("exercise_detail/{exerciseId}", "Detalle", Icons.Default.Info) {
        const val ARG_EXERCISE_ID = "exerciseId"
        fun createRoute(exerciseId: String) = "exercise_detail/$exerciseId"
    }

    object PlanDetail : Screen("plan_detail/{planId}", "Plan", Icons.AutoMirrored.Filled.Assignment) {
        const val ARG_PLAN_ID = "planId"
        fun createRoute(planId: String) = "plan_detail/$planId"
    }
}

/**
 * Lista de destinos principales. Se declara fuera de [Screen] a propósito:
 * construirla dentro del inicializador estático del sealed class provoca un
 * ciclo de inicialización en ART y los objetos anidados se leen como null.
 */
val bottomNavItems: List<Screen> = listOf(
    Screen.Home,
    Screen.ActiveWorkout,
    Screen.Exercises,
    Screen.Plans,
    Screen.History
)
