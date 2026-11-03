package com.gymtracker.app.data.repository

import android.util.Log
import com.gymtracker.app.data.catalog.ExerciseCatalog
import com.gymtracker.app.data.local.WorkoutState
import com.gymtracker.app.data.local.WorkoutStateStore
import com.gymtracker.app.domain.model.CustomPlan
import com.gymtracker.app.domain.model.Exercise
import com.gymtracker.app.domain.model.ExerciseLog
import com.gymtracker.app.domain.model.MuscleGroup
import com.gymtracker.app.domain.model.WorkoutSession
import com.gymtracker.app.domain.model.WorkoutSet
import com.gymtracker.app.domain.repository.WorkoutRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.LocalDateTime

/**
 * Repositorio offline que mantiene el estado en memoria y lo persiste en disco.
 *
 * - La lectura inicial está protegida: un fichero corrupto se ignora y la app
 *   arranca con un estado vacío.
 * - Las escrituras usan [WorkoutStateStore] con AtomicFile.
 * - Las actualizaciones de la sesión activa se agrupan (debounce) para no escribir
 *   en cada pulsación; las operaciones importantes escriben de inmediato.
 */
class LocalWorkoutRepository(
    private val store: WorkoutStateStore? = null
) : WorkoutRepository {

    private val exercisesList: List<Exercise> = ExerciseCatalog.all
    private val restoredState: WorkoutState = store?.load() ?: WorkoutState()

    private val exercisesFlow = MutableStateFlow(exercisesList)
    private val workoutsFlow = MutableStateFlow(restoredState.workouts)
    private val activeWorkoutFlow = MutableStateFlow(restoredState.activeWorkout)
    private val customPlansFlow = MutableStateFlow(restoredState.customPlans)
    private val activePlanIdFlow = MutableStateFlow(restoredState.activePlanId)

    private val persistenceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val writeMutex = Mutex()
    private var saveJob: Job? = null

    // ---------------------------------------------------------------------
    // Ejercicios
    // ---------------------------------------------------------------------

    override fun getExercises(): Flow<List<Exercise>> = exercisesFlow.asStateFlow()

    override fun getExercisesByMuscleGroup(group: MuscleGroup): Flow<List<Exercise>> =
        exercisesFlow.map { list -> list.filter { it.muscleGroup == group } }

    override fun getExercisesByNames(names: List<String>): List<Exercise> =
        ExerciseCatalog.byNames(names)

    override fun getExercisesByIds(ids: List<String>): List<Exercise> =
        ExerciseCatalog.byIds(ids)

    override fun getExerciseById(id: String): Exercise? = ExerciseCatalog.byId(id)

    // ---------------------------------------------------------------------
    // Entrenamientos
    // ---------------------------------------------------------------------

    override fun getRecentWorkouts(): Flow<List<WorkoutSession>> = workoutsFlow.asStateFlow()

    override fun getActiveWorkout(): Flow<WorkoutSession?> = activeWorkoutFlow.asStateFlow()

    override fun getActiveWorkoutSnapshot(): WorkoutSession? = activeWorkoutFlow.value

    override fun setActiveWorkout(workout: WorkoutSession) {
        activeWorkoutFlow.value = workout
        persistDebounced()
    }

    override suspend fun saveWorkout(workout: WorkoutSession) {
        val completed = workout.copy(isCompleted = true)
        workoutsFlow.value = listOf(completed) + workoutsFlow.value.filterNot { it.id == completed.id }
        persistNow()
    }

    override suspend fun startWorkout(
        title: String,
        initialExercises: List<Exercise>
    ): WorkoutSession {
        val exercisesToUse = initialExercises.ifEmpty { exercisesList.take(DEFAULT_EXERCISE_COUNT) }
        val session = WorkoutSession(
            title = title.ifBlank { "Entrenamiento Libre" },
            startedAt = LocalDateTime.now(),
            exercises = exercisesToUse.map { createExerciseLog(it) },
            isCompleted = false
        )
        activeWorkoutFlow.value = session
        persistNow()
        return session
    }

    override suspend fun updateActiveWorkout(workout: WorkoutSession) {
        setActiveWorkout(workout)
    }

    override suspend fun finishActiveWorkout() {
        val current = activeWorkoutFlow.value ?: return
        val elapsedSeconds = runCatching {
            Duration.between(current.startedAt, LocalDateTime.now()).seconds
        }.getOrDefault(0L).coerceAtLeast(0L)

        val completed = current.copy(
            durationSeconds = elapsedSeconds,
            isCompleted = true
        )
        workoutsFlow.value = listOf(completed) + workoutsFlow.value.filterNot { it.id == completed.id }
        activeWorkoutFlow.value = null
        persistNow()
    }

    override suspend fun discardActiveWorkout() {
        activeWorkoutFlow.value = null
        persistNow()
    }

    override suspend fun addExerciseToActiveWorkout(exercise: Exercise): Boolean {
        val current = activeWorkoutFlow.value ?: return false
        if (current.exercises.any { it.exercise.id == exercise.id }) return false

        activeWorkoutFlow.value = current.copy(
            exercises = current.exercises + createExerciseLog(exercise)
        )
        persistNow()
        return true
    }

    // ---------------------------------------------------------------------
    // Planes personalizados
    // ---------------------------------------------------------------------

    override fun getCustomPlans(): Flow<List<CustomPlan>> = customPlansFlow.asStateFlow()

    override fun getActivePlanId(): Flow<String?> = activePlanIdFlow.asStateFlow()

    override fun getActivePlan(): Flow<CustomPlan?> = combine(
        customPlansFlow,
        activePlanIdFlow
    ) { plans, activeId ->
        plans.firstOrNull { it.id == activeId }
    }

    override fun getCustomPlansSnapshot(): List<CustomPlan> = customPlansFlow.value

    override fun getCustomPlanSnapshot(planId: String): CustomPlan? =
        customPlansFlow.value.firstOrNull { it.id == planId }

    override fun getActivePlanSnapshot(): CustomPlan? =
        customPlansFlow.value.firstOrNull { it.id == activePlanIdFlow.value }

    override suspend fun saveCustomPlan(plan: CustomPlan) {
        val sanitized = plan.copy(
            name = plan.name.trim().ifBlank { "Mi plan" },
            exerciseIds = plan.exerciseIds.distinct()
        )

        val current = customPlansFlow.value.toMutableList()
        val index = current.indexOfFirst { it.id == sanitized.id }
        if (index >= 0) {
            current[index] = sanitized
        } else {
            current.add(sanitized)
        }
        customPlansFlow.value = current
        persistNow()
    }

    override suspend fun deleteCustomPlan(planId: String) {
        customPlansFlow.value = customPlansFlow.value.filterNot { it.id == planId }
        if (activePlanIdFlow.value == planId) {
            activePlanIdFlow.value = null
        }
        persistNow()
    }

    override suspend fun setActivePlan(planId: String?): Boolean {
        if (planId == null) {
            activePlanIdFlow.value = null
            persistNow()
            return true
        }

        if (customPlansFlow.value.none { it.id == planId }) return false

        activePlanIdFlow.value = planId
        persistNow()
        return true
    }

    override suspend fun addExerciseToPlan(planId: String, exerciseId: String): Boolean {
        if (ExerciseCatalog.byId(exerciseId) == null) return false

        val current = customPlansFlow.value.toMutableList()
        val index = current.indexOfFirst { it.id == planId }
        if (index < 0) return false

        val plan = current[index]
        if (exerciseId in plan.exerciseIds) return false

        current[index] = plan.copy(exerciseIds = plan.exerciseIds + exerciseId)
        customPlansFlow.value = current
        persistNow()
        return true
    }

    override suspend fun removeExerciseFromPlan(planId: String, exerciseId: String): Boolean {
        val current = customPlansFlow.value.toMutableList()
        val index = current.indexOfFirst { it.id == planId }
        if (index < 0) return false

        val plan = current[index]
        if (exerciseId !in plan.exerciseIds) return false

        current[index] = plan.copy(exerciseIds = plan.exerciseIds - exerciseId)
        customPlansFlow.value = current
        persistNow()
        return true
    }

    // ---------------------------------------------------------------------
    // Persistencia
    // ---------------------------------------------------------------------

    /** Fuerza un guardado inmediato. Se usa al pasar la app a segundo plano. */
    fun flush() {
        if (store == null) return
        saveJob?.cancel()
        saveJob = persistenceScope.launch {
            writeMutex.withLock {
                runCatching { store.save(snapshot()) }
                    .onFailure { Log.e(TAG, "No se pudo guardar el estado", it) }
            }
        }
    }

    private suspend fun persistNow() {
        val storage = store ?: return
        saveJob?.cancel()
        withContext(Dispatchers.IO) {
            writeMutex.withLock {
                runCatching { storage.save(snapshot()) }
                    .onFailure { Log.e(TAG, "No se pudo guardar el estado", it) }
            }
        }
    }

    private fun persistDebounced() {
        val storage = store ?: return
        saveJob?.cancel()
        saveJob = persistenceScope.launch {
            delay(SAVE_DEBOUNCE_MS)
            writeMutex.withLock {
                runCatching { storage.save(snapshot()) }
                    .onFailure { Log.e(TAG, "No se pudo guardar el estado", it) }
            }
        }
    }

    private fun snapshot(): WorkoutState = WorkoutState(
        workouts = workoutsFlow.value,
        activeWorkout = activeWorkoutFlow.value,
        customPlans = customPlansFlow.value,
        activePlanId = activePlanIdFlow.value
    )

    private fun createExerciseLog(exercise: Exercise): ExerciseLog = ExerciseLog(
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

    companion object {
        private const val TAG = "GymControlRepository"
        private const val DEFAULT_SETS = 3
        private const val DEFAULT_REPS = 10
        private const val DEFAULT_EXERCISE_COUNT = 3
        private const val SAVE_DEBOUNCE_MS = 250L
    }
}
