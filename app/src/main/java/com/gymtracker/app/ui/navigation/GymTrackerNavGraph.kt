package com.gymtracker.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gymtracker.app.data.local.UserPreferences
import com.gymtracker.app.ui.screens.exercises.ExercisesScreen
import com.gymtracker.app.ui.screens.exercises.ExercisesViewModel
import com.gymtracker.app.ui.screens.history.HistoryScreen
import com.gymtracker.app.ui.screens.history.HistoryViewModel
import com.gymtracker.app.ui.screens.home.HomeScreen
import com.gymtracker.app.ui.screens.home.HomeViewModel
import com.gymtracker.app.ui.screens.onboarding.PantallaOnboarding
import com.gymtracker.app.ui.screens.workout.ActiveWorkoutScreen
import com.gymtracker.app.ui.screens.workout.WorkoutViewModel
import com.gymtracker.app.ui.theme.GymBackground
import com.gymtracker.app.ui.theme.GymPrimary
import com.gymtracker.app.ui.theme.GymSurface
import com.gymtracker.app.ui.theme.TextPrimary
import com.gymtracker.app.ui.theme.TextTertiary

@Composable
fun GymTrackerNavGraph(
    userPreferences: UserPreferences,
    homeViewModel: HomeViewModel,
    workoutViewModel: WorkoutViewModel,
    exercisesViewModel: ExercisesViewModel,
    historyViewModel: HistoryViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isOnboardingDone = remember { userPreferences.isOnboardingCompleted() }
    val startDestination = if (isOnboardingDone) {
        Screen.Home.route
    } else {
        Screen.Onboarding.route
    }

    Scaffold(
        modifier = modifier,
        containerColor = GymBackground,
        bottomBar = {
            // Only show bottom navigation if we are on one of the main tabs
            val isMainScreen = currentRoute != null &&
                    currentRoute != Screen.Onboarding.route &&
                    Screen.bottomNavItems.any { it.route == currentRoute }

            if (isMainScreen) {
                NavigationBar(
                    containerColor = GymSurface,
                    contentColor = TextPrimary
                ) {
                    Screen.bottomNavItems.forEach { screen ->
                        val isSelected = currentRoute == screen.route

                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = screen.icon,
                                    contentDescription = screen.title
                                )
                            },
                            label = {
                                Text(
                                    text = screen.title,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = TextPrimary,
                                selectedTextColor = GymPrimary,
                                indicatorColor = GymPrimary,
                                unselectedIconColor = TextTertiary,
                                unselectedTextColor = TextTertiary
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Onboarding.route) {
                PantallaOnboarding(
                    userPreferences = userPreferences,
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = homeViewModel,
                    onNavigateToActiveWorkout = {
                        navController.navigate(Screen.ActiveWorkout.route)
                    }
                )
            }
            composable(Screen.ActiveWorkout.route) {
                ActiveWorkoutScreen(
                    viewModel = workoutViewModel,
                    onWorkoutFinished = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Exercises.route) {
                ExercisesScreen(
                    viewModel = exercisesViewModel
                )
            }
            composable(Screen.History.route) {
                HistoryScreen(
                    viewModel = historyViewModel
                )
            }
        }
    }
}
