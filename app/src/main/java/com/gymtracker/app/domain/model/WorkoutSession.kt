package com.gymtracker.app.domain.model

import java.time.LocalDateTime

data class WorkoutSession(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val startedAt: LocalDateTime = LocalDateTime.now(),
    val durationSeconds: Long = 0,
    val exercises: List<ExerciseLog> = emptyList(),
    val isCompleted: Boolean = false,
    val notes: String = ""
) {
    val totalVolumeKg: Double
        get() = exercises.sumOf { it.totalVolumeKg }

    val totalCompletedSets: Int
        get() = exercises.sumOf { it.completedSetsCount }
}
