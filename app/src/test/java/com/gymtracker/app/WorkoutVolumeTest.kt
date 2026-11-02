package com.gymtracker.app

import com.gymtracker.app.domain.model.Exercise
import com.gymtracker.app.domain.model.ExerciseLog
import com.gymtracker.app.domain.model.MuscleGroup
import com.gymtracker.app.domain.model.WorkoutSession
import com.gymtracker.app.domain.model.WorkoutSet
import org.junit.Assert.assertEquals
import org.junit.Test

class WorkoutVolumeTest {

    @Test
    fun calculateTotalVolume_correctlySumsCompletedSets() {
        val exercise = Exercise("1", "Press Banca", MuscleGroup.CHEST)
        val sets = listOf(
            WorkoutSet(setNumber = 1, weightKg = 100.0, reps = 10, isCompleted = true),  // 1000 kg
            WorkoutSet(setNumber = 2, weightKg = 100.0, reps = 8, isCompleted = true),   // 800 kg
            WorkoutSet(setNumber = 3, weightKg = 120.0, reps = 5, isCompleted = false)   // not completed
        )
        val exerciseLog = ExerciseLog(exercise = exercise, sets = sets)
        val session = WorkoutSession(
            title = "Pecho Test",
            exercises = listOf(exerciseLog)
        )

        assertEquals(1800.0, session.totalVolumeKg, 0.01)
        assertEquals(2, session.totalCompletedSets)
    }
}
