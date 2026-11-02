package com.gymtracker.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Onboarding : Screen("onboarding", "Bienvenida", Icons.Default.Home)
    object Home : Screen("home", "Inicio", Icons.Default.Home)
    object ActiveWorkout : Screen("active_workout", "Entrenar", Icons.Default.PlayArrow)
    object Exercises : Screen("exercises", "Ejercicios", Icons.Default.FitnessCenter)
    object History : Screen("history", "Historial", Icons.Default.DateRange)

    companion object {
        val bottomNavItems = listOf(Home, ActiveWorkout, Exercises, History)
    }
}
