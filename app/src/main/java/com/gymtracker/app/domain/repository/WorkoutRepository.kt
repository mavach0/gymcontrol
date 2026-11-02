package com.gymtracker.app.domain.repository

import com.gymtracker.app.domain.model.Exercise
import com.gymtracker.app.domain.model.MuscleGroup
import com.gymtracker.app.domain.model.WorkoutSession
import kotlinx.coroutines.flow.Flow

interface WorkoutRepository {
    fun getExercises(): Flow<List<Exercise>>
    fun getExercisesByMuscleGroup(group: MuscleGroup): Flow<List<Exercise>>
    fun getExercisesByNames(names: List<String>): List<Exercise>
    fun getRecentWorkouts(): Flow<List<WorkoutSession>>
    fun getActiveWorkout(): Flow<WorkoutSession?>
    suspend fun saveWorkout(workout: WorkoutSession)
    suspend fun startWorkout(title: String, initialExercises: List<Exercise> = emptyList()): WorkoutSession
    suspend fun updateActiveWorkout(workout: WorkoutSession)
    suspend fun finishActiveWorkout()
    suspend fun discardActiveWorkout()
}
