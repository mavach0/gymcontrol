package com.gymtracker.app.domain.repository

import com.gymtracker.app.domain.model.CustomPlan
import com.gymtracker.app.domain.model.Exercise
import com.gymtracker.app.domain.model.MuscleGroup
import com.gymtracker.app.domain.model.WorkoutSession
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de acceso a datos de entrenamientos. La capa de features sólo conoce
 * esta interfaz, por lo que la implementación local (o una futura remota) puede
 * cambiarse sin afectar a la UI.
 */
interface WorkoutRepository {

    // Ejercicios
    fun getExercises(): Flow<List<Exercise>>
    fun getExercisesByMuscleGroup(group: MuscleGroup): Flow<List<Exercise>>
    fun getExercisesByNames(names: List<String>): List<Exercise>
    fun getExercisesByIds(ids: List<String>): List<Exercise>
    fun getExerciseById(id: String): Exercise?

    // Entrenamientos
    fun getRecentWorkouts(): Flow<List<WorkoutSession>>
    fun getActiveWorkout(): Flow<WorkoutSession?>
    fun getActiveWorkoutSnapshot(): WorkoutSession?
    fun setActiveWorkout(workout: WorkoutSession)
    suspend fun saveWorkout(workout: WorkoutSession)
    suspend fun startWorkout(title: String, initialExercises: List<Exercise> = emptyList()): WorkoutSession
    suspend fun updateActiveWorkout(workout: WorkoutSession)
    suspend fun finishActiveWorkout()
    suspend fun discardActiveWorkout()
    suspend fun addExerciseToActiveWorkout(exercise: Exercise): Boolean

    // Planes personalizados
    fun getCustomPlans(): Flow<List<CustomPlan>>
    fun getActivePlanId(): Flow<String?>
    fun getActivePlan(): Flow<CustomPlan?>
    fun getCustomPlansSnapshot(): List<CustomPlan>
    fun getCustomPlanSnapshot(planId: String): CustomPlan?
    fun getActivePlanSnapshot(): CustomPlan?
    suspend fun saveCustomPlan(plan: CustomPlan)
    suspend fun deleteCustomPlan(planId: String)
    suspend fun setActivePlan(planId: String?): Boolean
    suspend fun addExerciseToPlan(planId: String, exerciseId: String): Boolean
    suspend fun removeExerciseFromPlan(planId: String, exerciseId: String): Boolean
}
