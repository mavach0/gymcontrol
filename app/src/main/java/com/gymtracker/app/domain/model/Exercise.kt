package com.gymtracker.app.domain.model

data class Exercise(
    val id: String,
    val name: String,
    val muscleGroup: MuscleGroup,
    val equipment: String = "Barra",
    val defaultRestSeconds: Int = 90,
    val description: String = "",
    val tips: List<String> = emptyList()
)
