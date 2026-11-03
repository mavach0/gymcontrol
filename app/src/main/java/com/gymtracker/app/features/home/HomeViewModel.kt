package com.gymtracker.app.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gymtracker.app.data.local.UserPreferences
import com.gymtracker.app.domain.model.PresetPlanCatalog
import com.gymtracker.app.domain.model.WorkoutSession
import com.gymtracker.app.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class HomeUiState(
    val userName: String = "",
    val selectedPlanTitle: String? = null,
    val recentWorkouts: List<WorkoutSession> = emptyList(),
    val streakDays: Int = 0,
    val weeklyVolumeKg: Double = 0.0,
    val completedWorkoutsThisWeek: Int = 0,
    val completedDaysOfWeek: Set<Int> = emptySet(), // 0 = Lunes, 6 = Domingo
    val hasActiveWorkout: Boolean = false,
    val isLoading: Boolean = false
)

class HomeViewModel(
    private val repository: WorkoutRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        repository.getRecentWorkouts(),
        repository.getActiveWorkout(),
        repository.getActivePlan()
    ) { recentWorkouts, activeWorkout, activePlan ->
        val now = LocalDate.now()
        val dayOfWeekIndex = now.dayOfWeek.value // 1 = Lunes, 7 = Domingo
        val mondayThisWeek = now.minusDays((dayOfWeekIndex - 1).toLong()).atStartOfDay()
        val sundayThisWeek = mondayThisWeek.plusDays(7).minusNanos(1)

        val workoutsThisWeek = recentWorkouts.filter {
            !it.startedAt.isBefore(mondayThisWeek) && !it.startedAt.isAfter(sundayThisWeek)
        }

        val completedDaysOfWeek = workoutsThisWeek.map {
            it.startedAt.dayOfWeek.value - 1
        }.toSet()

        HomeUiState(
            userName = userPreferences.getUserName(),
            selectedPlanTitle = activePlan?.name ?: userPreferences.getSelectedPlanTitle(),
            recentWorkouts = recentWorkouts,
            streakDays = calculateStreak(recentWorkouts.map { it.startedAt.toLocalDate() }),
            weeklyVolumeKg = workoutsThisWeek.sumOf { it.totalVolumeKg },
            completedWorkoutsThisWeek = workoutsThisWeek.size,
            completedDaysOfWeek = completedDaysOfWeek,
            hasActiveWorkout = activeWorkout != null,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState(
            isLoading = true,
            userName = userPreferences.getUserName(),
            selectedPlanTitle = userPreferences.getSelectedPlanTitle()
        )
    )

    fun startQuickWorkout(onStarted: () -> Unit) {
        viewModelScope.launch {
            // Si ya hay una sesión activa, no la sobrescribimos: se continúa.
            if (repository.getActiveWorkout().firstOrNull() != null) {
                onStarted()
                return@launch
            }

            val activePlan = repository.getActivePlan().firstOrNull()
            if (activePlan != null && activePlan.exerciseIds.isNotEmpty()) {
                val exercises = repository.getExercisesByIds(activePlan.exerciseIds)
                repository.startWorkout(title = activePlan.name, initialExercises = exercises)
                onStarted()
                return@launch
            }

            val presetTitle = userPreferences.getSelectedPlanTitle()
            val preset = PresetPlanCatalog.allPlans.firstOrNull { it.title == presetTitle }
            val exercises = preset?.let { repository.getExercisesByNames(it.exerciseNames) }.orEmpty()
            val title = presetTitle?.takeIf { it.isNotBlank() } ?: "Entrenamiento Libre"

            repository.startWorkout(title = title, initialExercises = exercises)
            onStarted()
        }
    }

    private fun calculateStreak(workoutDates: List<LocalDate>): Int {
        val uniqueDates = workoutDates.distinct().toSet()
        if (uniqueDates.isEmpty()) return 0

        var cursor = LocalDate.now()
        // La racha sigue viva si el último entreno fue hoy o ayer.
        if (cursor !in uniqueDates) {
            cursor = cursor.minusDays(1)
            if (cursor !in uniqueDates) return 0
        }

        var streak = 0
        while (cursor in uniqueDates) {
            streak += 1
            cursor = cursor.minusDays(1)
        }
        return streak
    }
}
