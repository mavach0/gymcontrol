package com.gymtracker.app.domain.model

data class ExerciseLog(
    val exercise: Exercise,
    val sets: List<WorkoutSet> = emptyList()
) {
    val totalVolumeKg: Double
        get() = sets.filter { it.isCompleted }.sumOf { it.weightKg * it.reps }

    val completedSetsCount: Int
        get() = sets.count { it.isCompleted }
}
