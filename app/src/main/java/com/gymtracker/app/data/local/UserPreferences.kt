package com.gymtracker.app.data.local

import android.content.Context
import android.content.SharedPreferences
import com.gymtracker.app.domain.model.FitnessGoal
import com.gymtracker.app.domain.model.TrainingLocation

data class UserProfile(
    val name: String,
    val heightCm: Int?,
    val weightKg: Double?,
    val goal: FitnessGoal?,
    val location: TrainingLocation?,
    val planTitle: String?
)

class UserPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun isOnboardingCompleted(): Boolean {
        return runCatching { prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false) }.getOrDefault(false)
    }

    fun setOnboardingCompleted(completed: Boolean) {
        runCatching {
            prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, completed).apply()
        }
    }

    fun getUserName(): String {
        return runCatching { prefs.getString(KEY_USER_NAME, "") }.getOrNull() ?: ""
    }

    fun getSelectedPlanTitle(): String? {
        return runCatching { prefs.getString(KEY_PLAN_TITLE, null) }.getOrNull()
    }

    fun getUserProfile(): UserProfile {
        return runCatching {
            val name = prefs.getString(KEY_USER_NAME, "") ?: ""
            val height = if (prefs.contains(KEY_USER_HEIGHT)) prefs.getInt(KEY_USER_HEIGHT, 0) else null
            val weight = if (prefs.contains(KEY_USER_WEIGHT)) prefs.getFloat(KEY_USER_WEIGHT, 0f).toDouble() else null
            val goalStr = prefs.getString(KEY_GOAL, null)
            val locationStr = prefs.getString(KEY_LOCATION, null)
            val planTitle = prefs.getString(KEY_PLAN_TITLE, null)

            val goal = goalStr?.let { runCatching { FitnessGoal.valueOf(it) }.getOrNull() }
            val location = locationStr?.let { runCatching { TrainingLocation.valueOf(it) }.getOrNull() }

            UserProfile(
                name = name,
                heightCm = height,
                weightKg = weight,
                goal = goal,
                location = location,
                planTitle = planTitle
            )
        }.getOrDefault(
            UserProfile(name = "", heightCm = null, weightKg = null, goal = null, location = null, planTitle = null)
        )
    }

    fun saveUserProfile(
        name: String,
        heightCm: Int?,
        weightKg: Double?,
        goal: FitnessGoal? = null,
        location: TrainingLocation? = null,
        planTitle: String? = null
    ) {
        runCatching {
            val editor = prefs.edit()
                .putBoolean(KEY_ONBOARDING_COMPLETED, true)
                .putString(KEY_USER_NAME, name.trim())

            if (heightCm != null && heightCm > 0) {
                editor.putInt(KEY_USER_HEIGHT, heightCm)
            } else {
                editor.remove(KEY_USER_HEIGHT)
            }

            if (weightKg != null && weightKg > 0.0) {
                editor.putFloat(KEY_USER_WEIGHT, weightKg.toFloat())
            } else {
                editor.remove(KEY_USER_WEIGHT)
            }

            if (goal != null) {
                editor.putString(KEY_GOAL, goal.name)
            } else {
                editor.remove(KEY_GOAL)
            }

            if (location != null) {
                editor.putString(KEY_LOCATION, location.name)
            } else {
                editor.remove(KEY_LOCATION)
            }

            if (!planTitle.isNullOrBlank()) {
                editor.putString(KEY_PLAN_TITLE, planTitle)
            } else {
                editor.remove(KEY_PLAN_TITLE)
            }

            editor.apply()
        }
    }

    companion object {
        private const val PREFS_NAME = "gymtracker_user_prefs"
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_HEIGHT = "user_height"
        private const val KEY_USER_WEIGHT = "user_weight"
        private const val KEY_GOAL = "user_goal"
        private const val KEY_LOCATION = "user_location"
        private const val KEY_PLAN_TITLE = "user_plan_title"
    }
}
