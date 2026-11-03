package com.gymtracker.app.domain.model

import java.util.UUID

data class CustomPlan(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val exerciseIds: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)
