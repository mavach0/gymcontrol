package com.gymtracker.app.features.exercises

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gymtracker.app.domain.model.CustomPlan
import com.gymtracker.app.domain.model.Exercise
import com.gymtracker.app.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ExerciseDetailUiState(
    val exercise: Exercise? = null,
    val activePlan: CustomPlan? = null,
    val availablePlans: List<CustomPlan> = emptyList(),
    val isInActivePlan: Boolean = false,
    val hasActiveWorkout: Boolean = false,
    val isInActiveWorkout: Boolean = false,
    val message: String? = null
)

class ExerciseDetailViewModel(
    private val repository: WorkoutRepository,
    exerciseId: String
) : ViewModel() {

    private val exercise: Exercise? = repository.getExerciseById(exerciseId)
    private val message = MutableStateFlow<String?>(null)

    val uiState: StateFlow<ExerciseDetailUiState> = combine(
        repository.getActivePlan(),
        repository.getCustomPlans(),
        repository.getActiveWorkout(),
        message
    ) { activePlan, plans, activeWorkout, currentMessage ->
        ExerciseDetailUiState(
            exercise = exercise,
            activePlan = activePlan,
            availablePlans = plans,
            isInActivePlan = activePlan?.exerciseIds?.contains(exercise?.id) == true,
            hasActiveWorkout = activeWorkout != null,
            isInActiveWorkout = activeWorkout?.exercises?.any { it.exercise.id == exercise?.id } == true,
            message = currentMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ExerciseDetailUiState(exercise = exercise)
    )

    fun addToActivePlan() {
        val currentExercise = exercise ?: return
        val plan = repository.getActivePlanSnapshot()

        if (plan == null) {
            message.value = "No tienes un plan activo. Elige uno para añadir el ejercicio."
            return
        }
        if (currentExercise.id in plan.exerciseIds) {
            message.value = "Este ejercicio ya está en ${plan.name}."
            return
        }

        viewModelScope.launch {
            val added = repository.addExerciseToPlan(plan.id, currentExercise.id)
            message.value = if (added) {
                "Ejercicio añadido a ${plan.name}"
            } else {
                "No se pudo añadir el ejercicio al plan"
            }
        }
    }

    fun addToPlan(planId: String) {
        val currentExercise = exercise ?: return
        val plan = repository.getCustomPlanSnapshot(planId) ?: return

        if (currentExercise.id in plan.exerciseIds) {
            message.value = "Este ejercicio ya está en ${plan.name}."
            return
        }

        viewModelScope.launch {
            val added = repository.addExerciseToPlan(planId, currentExercise.id)
            message.value = if (added) {
                "Ejercicio añadido a ${plan.name}"
            } else {
                "No se pudo añadir el ejercicio al plan"
            }
        }
    }

    fun addToActiveWorkout() {
        val currentExercise = exercise ?: return

        viewModelScope.launch {
            val added = repository.addExerciseToActiveWorkout(currentExercise)
            message.value = if (added) {
                "Ejercicio añadido al entrenamiento actual"
            } else {
                "El ejercicio ya está en el entrenamiento o no hay una sesión activa"
            }
        }
    }

    fun clearMessage() {
        message.value = null
    }
}
