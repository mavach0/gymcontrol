package com.gymtracker.app.features.plans

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

data class PlansUiState(
    val plans: List<CustomPlan> = emptyList(),
    val activePlanId: String? = null,
    val exercises: List<Exercise> = emptyList(),
    val message: String? = null,
    val isLoading: Boolean = true
) {
    val activePlan: CustomPlan?
        get() = plans.firstOrNull { it.id == activePlanId }

    fun planById(planId: String): CustomPlan? = plans.firstOrNull { it.id == planId }

    fun exercisesFor(plan: CustomPlan): List<Exercise> =
        plan.exerciseIds.mapNotNull { id -> exercises.firstOrNull { it.id == id } }
}

class PlansViewModel(
    private val repository: WorkoutRepository
) : ViewModel() {

    private val message = MutableStateFlow<String?>(null)

    val uiState: StateFlow<PlansUiState> = combine(
        repository.getCustomPlans(),
        repository.getActivePlanId(),
        repository.getExercises(),
        message
    ) { plans, activePlanId, exercises, currentMessage ->
        PlansUiState(
            plans = plans.sortedByDescending { it.createdAt },
            activePlanId = activePlanId,
            exercises = exercises,
            message = currentMessage,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PlansUiState()
    )

    fun createPlan(
        name: String,
        exerciseIds: List<String>,
        activate: Boolean = true,
        onCreated: (CustomPlan) -> Unit = {}
    ) {
        if (name.isBlank()) {
            message.value = "Escribe un nombre para el plan"
            return
        }
        if (exerciseIds.isEmpty()) {
            message.value = "Selecciona al menos un ejercicio"
            return
        }

        viewModelScope.launch {
            val plan = CustomPlan(
                name = name.trim(),
                exerciseIds = exerciseIds.distinct()
            )
            repository.saveCustomPlan(plan)
            if (activate) repository.setActivePlan(plan.id)
            message.value = "Plan \"${plan.name}\" creado"
            onCreated(plan)
        }
    }

    /** Crea un plan personalizado a partir del plan sugerido en el onboarding. */
    fun createPlanFromPreset(
        title: String,
        exerciseNames: List<String>,
        activate: Boolean = true
    ) {
        viewModelScope.launch {
            val exerciseIds = repository.getExercisesByNames(exerciseNames).map { it.id }
            if (exerciseIds.isEmpty()) return@launch

            val plan = CustomPlan(name = title, exerciseIds = exerciseIds)
            repository.saveCustomPlan(plan)
            if (activate) repository.setActivePlan(plan.id)
        }
    }

    fun deletePlan(planId: String) {
        viewModelScope.launch {
            repository.deleteCustomPlan(planId)
            message.value = "Plan eliminado"
        }
    }

    fun setActivePlan(planId: String) {
        viewModelScope.launch {
            val activated = repository.setActivePlan(planId)
            message.value = if (activated) "Plan activado" else "No se pudo activar el plan"
        }
    }

    fun clearActivePlan() {
        viewModelScope.launch {
            repository.setActivePlan(null)
            message.value = "Plan activo desactivado"
        }
    }

    fun addExerciseToPlan(planId: String, exerciseId: String) {
        viewModelScope.launch {
            val added = repository.addExerciseToPlan(planId, exerciseId)
            message.value = if (added) "Ejercicio añadido al plan" else "El ejercicio ya estaba en el plan"
        }
    }

    fun addExercisesToPlan(planId: String, exerciseIds: List<String>) {
        if (exerciseIds.isEmpty()) return

        viewModelScope.launch {
            val plan = repository.getCustomPlanSnapshot(planId) ?: return@launch
            val updated = plan.copy(
                exerciseIds = (plan.exerciseIds + exerciseIds).distinct()
            )
            repository.saveCustomPlan(updated)
            message.value = "Ejercicios añadidos al plan"
        }
    }

    fun removeExerciseFromPlan(planId: String, exerciseId: String) {
        viewModelScope.launch {
            val removed = repository.removeExerciseFromPlan(planId, exerciseId)
            if (removed) message.value = "Ejercicio quitado del plan"
        }
    }

    fun startPlanWorkout(plan: CustomPlan, onStarted: () -> Unit) {
        viewModelScope.launch {
            val exercises = repository.getExercisesByIds(plan.exerciseIds)
            repository.startWorkout(title = plan.name, initialExercises = exercises)
            onStarted()
        }
    }

    fun clearMessage() {
        message.value = null
    }
}
