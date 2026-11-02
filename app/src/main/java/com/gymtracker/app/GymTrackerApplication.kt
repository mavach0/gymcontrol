package com.gymtracker.app

import android.app.Application
import com.gymtracker.app.data.local.UserPreferences
import com.gymtracker.app.data.repository.InMemoryWorkoutRepository
import com.gymtracker.app.domain.repository.WorkoutRepository

class GymTrackerApplication : Application() {

    // Service locator / repository instance for app-wide injection
    val workoutRepository: WorkoutRepository by lazy {
        InMemoryWorkoutRepository()
    }

    val userPreferences: UserPreferences by lazy {
        UserPreferences(this)
    }

    override fun onCreate() {
        super.onCreate()
    }
}
