package com.gymtracker.app.domain.model

enum class SetType {
    WARMUP,
    NORMAL,
    DROPSET,
    FAILURE
}

data class WorkoutSet(
    val id: String = java.util.UUID.randomUUID().toString(),
    val setNumber: Int,
    val weightKg: Double = 0.0,
    val reps: Int = 0,
    val isCompleted: Boolean = false,
    val type: SetType = SetType.NORMAL
)
