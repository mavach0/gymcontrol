package com.gymtracker.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gymtracker.app.data.local.UserPreferences
import com.gymtracker.app.domain.model.PresetPlanCatalog
import com.gymtracker.app.domain.model.WorkoutSession
import com.gymtracker.app.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
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
        repository.getActiveWorkout()
    ) { recentWorkouts, activeWorkout ->
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

        val streak = if (recentWorkouts.isEmpty()) 0 else {
            recentWorkouts.map { it.startedAt.toLocalDate() }.distinct().count()
        }

        HomeUiState(
            userName = userPreferences.getUserName(),
            selectedPlanTitle = userPreferences.getSelectedPlanTitle(),
            recentWorkouts = recentWorkouts,
            streakDays = streak,
            weeklyVolumeKg = workoutsThisWeek.sumOf { it.totalVolumeKg },
            completedWorkoutsThisWeek = workoutsThisWeek.size,
            completedDaysOfWeek = completedDaysOfWeek,
            hasActiveWorkout = activeWorkout != null,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(
            isLoading = true,
            userName = userPreferences.getUserName(),
            selectedPlanTitle = userPreferences.getSelectedPlanTitle()
        )
    )

    fun startQuickWorkout(onStarted: () -> Unit) {
        viewModelScope.launch {
            val planTitle = userPreferences.getSelectedPlanTitle()
            val plan = PresetPlanCatalog.allPlans.firstOrNull { it.title == planTitle }
            val exercises = if (plan != null) {
                repository.getExercisesByNames(plan.exerciseNames)
            } else {
                emptyList()
            }
            val title = if (!planTitle.isNullOrBlank()) planTitle else "Entrenamiento Libre"
            repository.startWorkout(title = title, initialExercises = exercises)
            onStarted()
        }
    }
}
