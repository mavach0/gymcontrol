package com.gymtracker.app.ui.screens.workout

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
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ActiveWorkoutUiState()
    )

    fun startWorkoutIfNotActive() {
        viewModelScope.launch {
            if (uiState.value.workout == null) {
                repository.startWorkout("Entrenamiento Actual")
            }
        }
    }

    fun updateSet(exerciseIndex: Int, setIndex: Int, weight: Double, reps: Int) {
        val currentWorkout = uiState.value.workout ?: return
        val currentExercises = currentWorkout.exercises.toMutableList()
        val currentLog = currentExercises.getOrNull(exerciseIndex) ?: return
        val sets = currentLog.sets.toMutableList()
        val currentSet = sets.getOrNull(setIndex) ?: return

        sets[setIndex] = currentSet.copy(weightKg = weight, reps = reps)
        currentExercises[exerciseIndex] = currentLog.copy(sets = sets)

        viewModelScope.launch {
            repository.updateActiveWorkout(currentWorkout.copy(exercises = currentExercises))
        }
    }

    fun toggleSetCompleted(exerciseIndex: Int, setIndex: Int) {
        val currentWorkout = uiState.value.workout ?: return
        val currentExercises = currentWorkout.exercises.toMutableList()
        val currentLog = currentExercises.getOrNull(exerciseIndex) ?: return
        val sets = currentLog.sets.toMutableList()
        val currentSet = sets.getOrNull(setIndex) ?: return

        val newCompleted = !currentSet.isCompleted
        sets[setIndex] = currentSet.copy(isCompleted = newCompleted)
        currentExercises[exerciseIndex] = currentLog.copy(sets = sets)

        viewModelScope.launch {
            repository.updateActiveWorkout(currentWorkout.copy(exercises = currentExercises))
        }

        if (newCompleted) {
            startRestTimer(currentLog.exercise.defaultRestSeconds)
        }
    }

    fun addSetToExercise(exerciseIndex: Int) {
        val currentWorkout = uiState.value.workout ?: return
        val currentExercises = currentWorkout.exercises.toMutableList()
        val currentLog = currentExercises.getOrNull(exerciseIndex) ?: return
        val sets = currentLog.sets.toMutableList()

        val lastSet = sets.lastOrNull()
        val newSet = WorkoutSet(
            setNumber = sets.size + 1,
            weightKg = lastSet?.weightKg ?: 0.0,
            reps = lastSet?.reps ?: 10,
            isCompleted = false
        )
        sets.add(newSet)
        currentExercises[exerciseIndex] = currentLog.copy(sets = sets)

        viewModelScope.launch {
            repository.updateActiveWorkout(currentWorkout.copy(exercises = currentExercises))
        }
    }

    fun addExerciseToWorkout(exercise: Exercise) {
        val currentWorkout = uiState.value.workout ?: return
        val currentExercises = currentWorkout.exercises.toMutableList()

        val newLog = ExerciseLog(
            exercise = exercise,
            sets = listOf(
                WorkoutSet(setNumber = 1, weightKg = 40.0, reps = 10, isCompleted = false),
                WorkoutSet(setNumber = 2, weightKg = 40.0, reps = 10, isCompleted = false),
                WorkoutSet(setNumber = 3, weightKg = 40.0, reps = 10, isCompleted = false)
            )
        )
        currentExercises.add(newLog)

        viewModelScope.launch {
            repository.updateActiveWorkout(currentWorkout.copy(exercises = currentExercises))
            _showAddExerciseDialog.value = false
        }
    }

    fun toggleAddExerciseDialog(show: Boolean) {
        _showAddExerciseDialog.value = show
    }

    fun startRestTimer(seconds: Int) {
        restTimerJob?.cancel()
        _restTimerSeconds.value = seconds
        _isRestTimerRunning.value = true

        restTimerJob = viewModelScope.launch {
            while (_restTimerSeconds.value > 0) {
                delay(1000)
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
}
