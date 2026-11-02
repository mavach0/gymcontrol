package com.gymtracker.app.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gymtracker.app.domain.model.WorkoutSession
import com.gymtracker.app.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class HistoryUiState(
    val workouts: List<WorkoutSession> = emptyList(),
    val totalWorkouts: Int = 0,
    val totalVolumeKg: Double = 0.0,
    val totalHours: Double = 0.0
)

class HistoryViewModel(
    repository: WorkoutRepository
) : ViewModel() {

    val uiState: StateFlow<HistoryUiState> = repository.getRecentWorkouts()
        .map { list ->
            HistoryUiState(
                workouts = list,
                totalWorkouts = list.size,
                totalVolumeKg = list.sumOf { it.totalVolumeKg },
                totalHours = list.sumOf { it.durationSeconds }.toDouble() / 3600.0
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HistoryUiState()
        )
}
