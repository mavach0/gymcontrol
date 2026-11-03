package com.gymtracker.app.features.exercises

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gymtracker.app.domain.model.Exercise
import com.gymtracker.app.domain.model.MuscleGroup
import com.gymtracker.app.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class ExercisesUiState(
    val exercises: List<Exercise> = emptyList(),
    val filteredExercises: List<Exercise> = emptyList(),
    val searchQuery: String = "",
    val selectedMuscleGroup: MuscleGroup? = null
)

class ExercisesViewModel(
    private val repository: WorkoutRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedMuscleGroup = MutableStateFlow<MuscleGroup?>(null)

    val uiState: StateFlow<ExercisesUiState> = combine(
        repository.getExercises(),
        _searchQuery,
        _selectedMuscleGroup
    ) { exercises, query, selectedGroup ->
        val filtered = exercises.filter { exercise ->
            val matchesQuery = query.isBlank() ||
                exercise.name.contains(query, ignoreCase = true) ||
                exercise.equipment.contains(query, ignoreCase = true) ||
                exercise.muscleGroup.displayName.contains(query, ignoreCase = true)
            val matchesGroup = selectedGroup == null || exercise.muscleGroup == selectedGroup
            matchesQuery && matchesGroup
        }

        ExercisesUiState(
            exercises = exercises,
            filteredExercises = filtered,
            searchQuery = query,
            selectedMuscleGroup = selectedGroup
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ExercisesUiState()
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onMuscleGroupSelected(group: MuscleGroup?) {
        _selectedMuscleGroup.value = if (_selectedMuscleGroup.value == group) null else group
    }
}
