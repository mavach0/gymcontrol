package com.gymtracker.app.features.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gymtracker.app.domain.model.Exercise
import com.gymtracker.app.domain.model.ExerciseLog
import com.gymtracker.app.domain.model.WorkoutSession
import com.gymtracker.app.domain.model.WorkoutSet
import com.gymtracker.app.domain.repository.WorkoutRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ActiveWorkoutUiState(
    val workout: WorkoutSession? = null,
    val restTimerSeconds: Int = 0,
    val isRestTimerRunning: Boolean = false,
    val allExercises: List<Exercise> = emptyList(),
    val showAddExerciseDialog: Boolean = false
)

class WorkoutViewModel(
    private val repository: WorkoutRepository
) : ViewModel() {

    private val _restTimerSeconds = MutableStateFlow(0)
    private val _isRestTimerRunning = MutableStateFlow(false)
    private val _showAddExerciseDialog = MutableStateFlow(false)
    private var restTimerJob: Job? = null

    val uiState: StateFlow<ActiveWorkoutUiState> = combine(
        repository.getActiveWorkout(),
        repository.getExercises(),
        _restTimerSeconds,
        _isRestTimerRunning,
        _showAddExerciseDialog
    ) { activeWorkout, exercises, restTimer, isRunning, showDialog ->
        ActiveWorkoutUiState(
            workout = activeWorkout,
            restTimerSeconds = restTimer,
            isRestTimerRunning = isRunning,
            allExercises = exercises,
            showAddExerciseDialog = showDialog
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ActiveWorkoutUiState()
    )

    fun startWorkoutIfNotActive() {
        viewModelScope.launch {
            if (repository.getActiveWorkoutSnapshot() == null) {
                repository.startWorkout("Entrenamiento Actual")
            }
        }
    }

    /**
     * Actualiza solo el peso leyendo el último estado guardado, para no pisar
     * cambios de repeticiones hechos justo antes.
     */
    fun updateWeight(exerciseIndex: Int, setIndex: Int, weight: Double) {
        val currentWorkout = repository.getActiveWorkoutSnapshot() ?: return
        val currentExercises = currentWorkout.exercises.toMutableList()
        val currentLog = currentExercises.getOrNull(exerciseIndex) ?: return
        val sets = currentLog.sets.toMutableList()
        val currentSet = sets.getOrNull(setIndex) ?: return

        sets[setIndex] = currentSet.copy(weightKg = weight.coerceAtLeast(0.0))
        currentExercises[exerciseIndex] = currentLog.copy(sets = sets)

        repository.setActiveWorkout(currentWorkout.copy(exercises = currentExercises))
    }

    /**
     * Ajusta las repeticiones leyendo siempre el último estado guardado. Así dos
     * pulsaciones rápidas seguidas no se pisan entre sí con un valor obsoleto.
     */
    fun adjustReps(exerciseIndex: Int, setIndex: Int, delta: Int) {
        val currentWorkout = repository.getActiveWorkoutSnapshot() ?: return
        val currentExercises = currentWorkout.exercises.toMutableList()
        val currentLog = currentExercises.getOrNull(exerciseIndex) ?: return
        val sets = currentLog.sets.toMutableList()
        val currentSet = sets.getOrNull(setIndex) ?: return

        sets[setIndex] = currentSet.copy(
            reps = (currentSet.reps + delta).coerceIn(MIN_REPS, MAX_REPS)
        )
        currentExercises[exerciseIndex] = currentLog.copy(sets = sets)

        repository.setActiveWorkout(currentWorkout.copy(exercises = currentExercises))
    }

    fun toggleSetCompleted(exerciseIndex: Int, setIndex: Int) {
        val currentWorkout = repository.getActiveWorkoutSnapshot() ?: return
        val currentExercises = currentWorkout.exercises.toMutableList()
        val currentLog = currentExercises.getOrNull(exerciseIndex) ?: return
        val sets = currentLog.sets.toMutableList()
        val currentSet = sets.getOrNull(setIndex) ?: return

        val newCompleted = !currentSet.isCompleted
        sets[setIndex] = currentSet.copy(isCompleted = newCompleted)
        currentExercises[exerciseIndex] = currentLog.copy(sets = sets)

        repository.setActiveWorkout(currentWorkout.copy(exercises = currentExercises))

        if (newCompleted) {
            startRestTimer(currentLog.exercise.defaultRestSeconds)
        }
    }

    fun addSetToExercise(exerciseIndex: Int) {
        val currentWorkout = repository.getActiveWorkoutSnapshot() ?: return
        val currentExercises = currentWorkout.exercises.toMutableList()
        val currentLog = currentExercises.getOrNull(exerciseIndex) ?: return
        val sets = currentLog.sets.toMutableList()
        if (sets.size >= MAX_SETS) return

        val lastSet = sets.lastOrNull()
        val newSet = WorkoutSet(
            setNumber = sets.size + 1,
            weightKg = lastSet?.weightKg ?: 0.0,
            reps = lastSet?.reps ?: DEFAULT_REPS,
            isCompleted = false
        )
        sets.add(newSet)
        currentExercises[exerciseIndex] = currentLog.copy(sets = sets)

        repository.setActiveWorkout(currentWorkout.copy(exercises = currentExercises))
    }

    /**
     * Elimina la última serie de un ejercicio. Nunca permite bajar de una serie,
     * pero deja de estar bloqueado el mínimo artificial de tres.
     */
    fun removeSetFromExercise(exerciseIndex: Int) {
        val currentWorkout = repository.getActiveWorkoutSnapshot() ?: return
        val currentExercises = currentWorkout.exercises.toMutableList()
        val currentLog = currentExercises.getOrNull(exerciseIndex) ?: return
        if (currentLog.sets.size <= MIN_SETS) return

        val updatedSets = currentLog.sets
            .dropLast(1)
            .mapIndexed { index, set -> set.copy(setNumber = index + 1) }

        currentExercises[exerciseIndex] = currentLog.copy(sets = updatedSets)

        repository.setActiveWorkout(currentWorkout.copy(exercises = currentExercises))
    }

    fun removeExerciseFromWorkout(exerciseIndex: Int) {
        val currentWorkout = repository.getActiveWorkoutSnapshot() ?: return
        val currentExercises = currentWorkout.exercises.toMutableList()
        if (exerciseIndex !in currentExercises.indices) return

        currentExercises.removeAt(exerciseIndex)

        repository.setActiveWorkout(currentWorkout.copy(exercises = currentExercises))
    }

    fun addExerciseToWorkout(exercise: Exercise) {
        val currentWorkout = repository.getActiveWorkoutSnapshot() ?: return
        if (currentWorkout.exercises.any { it.exercise.id == exercise.id }) {
            _showAddExerciseDialog.value = false
            return
        }

        val newLog = ExerciseLog(
            exercise = exercise,
            sets = (1..DEFAULT_SETS).map { setNumber ->
                WorkoutSet(
                    setNumber = setNumber,
                    weightKg = 0.0,
                    reps = DEFAULT_REPS,
                    isCompleted = false
                )
            }
        )
        val currentExercises = currentWorkout.exercises + newLog

        repository.setActiveWorkout(currentWorkout.copy(exercises = currentExercises))
        _showAddExerciseDialog.value = false
    }

    fun toggleAddExerciseDialog(show: Boolean) {
        _showAddExerciseDialog.value = show
    }

    fun startRestTimer(seconds: Int) {
        restTimerJob?.cancel()
        _restTimerSeconds.value = seconds.coerceAtLeast(0)
        _isRestTimerRunning.value = _restTimerSeconds.value > 0

        restTimerJob = viewModelScope.launch {
            while (_restTimerSeconds.value > 0) {
                delay(1_000)
                _restTimerSeconds.value -= 1
            }
            _isRestTimerRunning.value = false
        }
    }

    fun skipRestTimer() {
        restTimerJob?.cancel()
        _isRestTimerRunning.value = false
        _restTimerSeconds.value = 0
    }

    fun finishWorkout(onFinished: () -> Unit) {
        viewModelScope.launch {
            repository.finishActiveWorkout()
            onFinished()
        }
    }

    fun discardWorkout(onDiscarded: () -> Unit) {
        viewModelScope.launch {
            repository.discardActiveWorkout()
            onDiscarded()
        }
    }

    companion object {
        private const val MIN_REPS = 1
        private const val MAX_REPS = 999
        private const val MIN_SETS = 1
        private const val MAX_SETS = 20
        private const val DEFAULT_SETS = 3
        private const val DEFAULT_REPS = 10
    }
}
