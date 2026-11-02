package com.gymtracker.app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.gymtracker.app.data.local.UserPreferences
import com.gymtracker.app.data.repository.InMemoryWorkoutRepository
import com.gymtracker.app.domain.repository.WorkoutRepository
import com.gymtracker.app.ui.navigation.GymTrackerNavGraph
import com.gymtracker.app.ui.screens.exercises.ExercisesViewModel
import com.gymtracker.app.ui.screens.history.HistoryViewModel
import com.gymtracker.app.ui.screens.home.HomeViewModel
import com.gymtracker.app.ui.screens.workout.WorkoutViewModel
import com.gymtracker.app.ui.theme.GymBackground
import com.gymtracker.app.ui.theme.GymTrackerTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            enableEdgeToEdge()
        } catch (e: Exception) {
            Log.w("GymControl", "EdgeToEdge warning: ${e.message}")
        }

        try {
            val app = application as? GymTrackerApplication
            val repository: WorkoutRepository = app?.workoutRepository ?: InMemoryWorkoutRepository()
            val userPreferences: UserPreferences = app?.userPreferences ?: UserPreferences(applicationContext)

            val homeViewModel = ViewModelProvider(
                this,
                GenericViewModelFactory { HomeViewModel(repository, userPreferences) }
            )[HomeViewModel::class.java]

            val workoutViewModel = ViewModelProvider(
                this,
                GenericViewModelFactory { WorkoutViewModel(repository) }
            )[WorkoutViewModel::class.java]

            val exercisesViewModel = ViewModelProvider(
                this,
                GenericViewModelFactory { ExercisesViewModel(repository) }
            )[ExercisesViewModel::class.java]

            val historyViewModel = ViewModelProvider(
                this,
                GenericViewModelFactory { HistoryViewModel(repository) }
            )[HistoryViewModel::class.java]

            setContent {
                GymTrackerTheme {
                    GymTrackerNavGraph(
                        userPreferences = userPreferences,
                        homeViewModel = homeViewModel,
                        workoutViewModel = workoutViewModel,
                        exercisesViewModel = exercisesViewModel,
                        historyViewModel = historyViewModel
                    )
                }
            }
        } catch (t: Throwable) {
            Log.e("GYM_CRASH", "Error al iniciar GymControl", t)
            setContent {
                GymTrackerTheme {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(GymBackground)
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Error al iniciar:\n${t.localizedMessage ?: t.javaClass.simpleName}\n\n${t.stackTraceToString().take(600)}",
                            color = Color(0xFFEF4444),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

class GenericViewModelFactory<T : ViewModel>(
    private val creator: () -> T
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <VM : ViewModel> create(modelClass: Class<VM>): VM {
        return creator() as VM
    }

    @Suppress("UNCHECKED_CAST")
    override fun <VM : ViewModel> create(modelClass: Class<VM>, extras: CreationExtras): VM {
        return creator() as VM
    }
}
