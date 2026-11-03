package com.gymtracker.app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.gymtracker.app.core.di.AppContainer
import com.gymtracker.app.core.di.GenericViewModelFactory
import com.gymtracker.app.features.exercises.ExercisesViewModel
import com.gymtracker.app.features.history.HistoryViewModel
import com.gymtracker.app.features.home.HomeViewModel
import com.gymtracker.app.features.plans.PlansViewModel
import com.gymtracker.app.features.workout.WorkoutViewModel
import com.gymtracker.app.ui.navigation.GymTrackerNavGraph
import com.gymtracker.app.ui.theme.GymTrackerTheme

class MainActivity : ComponentActivity() {

    private val container: AppContainer by lazy {
        (application as? GymTrackerApplication)?.container ?: AppContainer(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        runCatching { enableEdgeToEdge() }
            .onFailure { Log.w("GymControl", "EdgeToEdge no disponible: ${it.message}") }

        val repository = container.workoutRepository
        val preferences = container.userPreferences

        val homeViewModel = ViewModelProvider(
            this,
            GenericViewModelFactory { HomeViewModel(repository, preferences) }
        )[HomeViewModel::class.java]

        val workoutViewModel = ViewModelProvider(
            this,
            GenericViewModelFactory { WorkoutViewModel(repository) }
        )[WorkoutViewModel::class.java]

        val exercisesViewModel = ViewModelProvider(
            this,
            GenericViewModelFactory { ExercisesViewModel(repository) }
        )[ExercisesViewModel::class.java]

        val plansViewModel = ViewModelProvider(
            this,
            GenericViewModelFactory { PlansViewModel(repository) }
        )[PlansViewModel::class.java]

        val historyViewModel = ViewModelProvider(
            this,
            GenericViewModelFactory { HistoryViewModel(repository) }
        )[HistoryViewModel::class.java]

        setContent {
            GymTrackerTheme {
                GymTrackerNavGraph(
                    userPreferences = preferences,
                    repository = repository,
                    homeViewModel = homeViewModel,
                    workoutViewModel = workoutViewModel,
                    exercisesViewModel = exercisesViewModel,
                    plansViewModel = plansViewModel,
                    historyViewModel = historyViewModel
                )
            }
        }
    }
}
