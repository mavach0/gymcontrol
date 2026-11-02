package com.gymtracker.app.data.repository

import com.gymtracker.app.domain.model.Exercise
import com.gymtracker.app.domain.model.ExerciseLog
import com.gymtracker.app.domain.model.MuscleGroup
import com.gymtracker.app.domain.model.WorkoutSession
import com.gymtracker.app.domain.model.WorkoutSet
import com.gymtracker.app.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime

class InMemoryWorkoutRepository : WorkoutRepository {

    private val exercisesList = listOf(
        // Pecho
        Exercise("1", "Press de Banca Plano", MuscleGroup.CHEST, "Barra", 90),
        Exercise("2", "Press Inclinado con Mancuernas", MuscleGroup.CHEST, "Mancuernas", 75),
        Exercise("3", "Aperturas en Polea (Cruce)", MuscleGroup.CHEST, "Polea", 60),
        Exercise("4", "Fondos en Paralelas", MuscleGroup.CHEST, "Peso Corporal", 90),

        // Espalda
        Exercise("5", "Peso Muerto Convencional", MuscleGroup.BACK, "Barra", 120),
        Exercise("6", "Dominadas Pronas", MuscleGroup.BACK, "Peso Corporal", 90),
        Exercise("7", "Remo con Barra Pendlay", MuscleGroup.BACK, "Barra", 90),
        Exercise("8", "Jalón al Pecho en Polea", MuscleGroup.BACK, "Polea", 60),

        // Piernas
        Exercise("9", "Sentadilla Trasera con Barra", MuscleGroup.LEGS, "Barra", 120),
        Exercise("10", "Prensa de Piernas 45°", MuscleGroup.LEGS, "Máquina", 90),
        Exercise("11", "Curl Femoral Tumbado", MuscleGroup.LEGS, "Máquina", 60),
        Exercise("12", "Elevación de Gemelos en Máquina", MuscleGroup.LEGS, "Máquina", 45),

        // Hombros
        Exercise("13", "Press Militar con Barra", MuscleGroup.SHOULDERS, "Barra", 90),
        Exercise("14", "Elevaciones Laterales", MuscleGroup.SHOULDERS, "Mancuernas", 60),
        Exercise("15", "Pájaros para Deltoides Posterior", MuscleGroup.SHOULDERS, "Mancuernas", 60),

        // Brazos
        Exercise("16", "Curl de Bíceps con Barra Z", MuscleGroup.ARMS, "Barra", 60),
        Exercise("17", "Press Francés con Mancuernas", MuscleGroup.ARMS, "Mancuernas", 60),
        Exercise("18", "Extensiones de Tríceps en Polea", MuscleGroup.ARMS, "Polea", 45),
        Exercise("19", "Curl Martillo", MuscleGroup.ARMS, "Mancuernas", 60),

        // Core
        Exercise("20", "Plancha Abdominal", MuscleGroup.CORE, "Peso Corporal", 60),
        Exercise("21", "Elevación de Piernas Colgado", MuscleGroup.CORE, "Barra", 60),
        Exercise("22", "Rueda Abdominal (Ab Wheel)", MuscleGroup.CORE, "Accesorios", 60)
    )

    private val exercisesFlow = MutableStateFlow(exercisesList)

    // Start with empty history so new users start at 0 days and 0 kg
    private val workoutsFlow = MutableStateFlow<List<WorkoutSession>>(emptyList())
    private val activeWorkoutFlow = MutableStateFlow<WorkoutSession?>(null)

    override fun getExercises(): Flow<List<Exercise>> = exercisesFlow.asStateFlow()

    override fun getExercisesByMuscleGroup(group: MuscleGroup): Flow<List<Exercise>> {
        return exercisesFlow.map { list -> list.filter { it.muscleGroup == group } }
    }

    override fun getExercisesByNames(names: List<String>): List<Exercise> {
        return names.mapNotNull { name ->
            exercisesList.firstOrNull { it.name.equals(name, ignoreCase = true) }
        }
    }

    override fun getRecentWorkouts(): Flow<List<WorkoutSession>> = workoutsFlow.asStateFlow()

    override fun getActiveWorkout(): Flow<WorkoutSession?> = activeWorkoutFlow.asStateFlow()

    override suspend fun saveWorkout(workout: WorkoutSession) {
        val current = workoutsFlow.value.toMutableList()
        current.add(0, workout.copy(isCompleted = true))
        workoutsFlow.value = current
    }

    override suspend fun startWorkout(title: String, initialExercises: List<Exercise>): WorkoutSession {
        val exercisesToUse = if (initialExercises.isNotEmpty()) {
            initialExercises
        } else {
            listOf(exercisesList[0], exercisesList[1], exercisesList[2])
        }

        val exercisesLogs = exercisesToUse.map { exercise ->
            ExerciseLog(
                exercise = exercise,
                sets = listOf(
                    WorkoutSet(setNumber = 1, weightKg = 0.0, reps = 10, isCompleted = false),
                    WorkoutSet(setNumber = 2, weightKg = 0.0, reps = 10, isCompleted = false),
                    WorkoutSet(setNumber = 3, weightKg = 0.0, reps = 10, isCompleted = false)
                )
            )
        }

        val session = WorkoutSession(
            title = title,
            startedAt = LocalDateTime.now(),
            exercises = exercisesLogs,
            isCompleted = false
        )
        activeWorkoutFlow.value = session
        return session
    }

    override suspend fun updateActiveWorkout(workout: WorkoutSession) {
        activeWorkoutFlow.value = workout
    }

    override suspend fun finishActiveWorkout() {
        val current = activeWorkoutFlow.value
        if (current != null) {
            saveWorkout(current)
            activeWorkoutFlow.value = null
        }
    }

    override suspend fun discardActiveWorkout() {
        activeWorkoutFlow.value = null
    }
}
