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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gymtracker.app.core.di.GenericViewModelFactory
import com.gymtracker.app.data.local.UserPreferences
import com.gymtracker.app.domain.repository.WorkoutRepository
import com.gymtracker.app.features.exercises.ExerciseDetailScreen
import com.gymtracker.app.features.exercises.ExerciseDetailViewModel
import com.gymtracker.app.features.exercises.ExercisesScreen
import com.gymtracker.app.features.exercises.ExercisesViewModel
import com.gymtracker.app.features.history.HistoryScreen
import com.gymtracker.app.features.history.HistoryViewModel
import com.gymtracker.app.features.home.HomeScreen
import com.gymtracker.app.features.home.HomeViewModel
import com.gymtracker.app.features.onboarding.PantallaOnboarding
import com.gymtracker.app.features.plans.PlanDetailScreen
import com.gymtracker.app.features.plans.PlansScreen
import com.gymtracker.app.features.plans.PlansViewModel
import com.gymtracker.app.features.workout.ActiveWorkoutScreen
import com.gymtracker.app.features.workout.WorkoutViewModel
import com.gymtracker.app.ui.theme.GymBackground
import com.gymtracker.app.ui.theme.GymPrimary
import com.gymtracker.app.ui.theme.GymSurface
import com.gymtracker.app.ui.theme.TextPrimary
import com.gymtracker.app.ui.theme.TextTertiary

@Composable
fun GymTrackerNavGraph(
    userPreferences: UserPreferences,
    repository: WorkoutRepository,
    homeViewModel: HomeViewModel,
    workoutViewModel: WorkoutViewModel,
    exercisesViewModel: ExercisesViewModel,
    plansViewModel: PlansViewModel,
    historyViewModel: HistoryViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // La ruta inicial se decide una sola vez y se conserva durante recreaciones.
    val startDestination = rememberSaveable {
        if (userPreferences.isOnboardingCompleted()) {
            Screen.Home.route
        } else {
            Screen.Onboarding.route
        }
    }

    Scaffold(
        modifier = modifier,
        containerColor = GymBackground,
        bottomBar = {
            val isMainScreen = currentRoute != null &&
                bottomNavItems.any { it.route == currentRoute }

            if (isMainScreen) {
                NavigationBar(
                    containerColor = GymSurface,
                    contentColor = TextPrimary
                ) {
                    bottomNavItems.forEach { screen ->
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
                                    fontSize = 10.sp,
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
                    onFinish = { planTitle, exerciseNames ->
                        if (!planTitle.isNullOrBlank()) {
                            plansViewModel.createPlanFromPreset(
                                title = planTitle,
                                exerciseNames = exerciseNames,
                                activate = true
                            )
                        }
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
                        navController.navigate(Screen.ActiveWorkout.route) {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToPlans = {
                        navController.navigate(Screen.Plans.route) {
                            launchSingleTop = true
                        }
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
                    viewModel = exercisesViewModel,
                    onExerciseClick = { exerciseId ->
                        navController.navigate(Screen.ExerciseDetail.createRoute(exerciseId))
                    }
                )
            }

            composable(Screen.ExerciseDetail.route) { backStackEntry ->
                val exerciseId = backStackEntry.arguments
                    ?.getString(Screen.ExerciseDetail.ARG_EXERCISE_ID)
                    .orEmpty()

                val detailViewModel: ExerciseDetailViewModel = viewModel(
                    factory = GenericViewModelFactory {
                        ExerciseDetailViewModel(repository, exerciseId)
                    }
                )

                ExerciseDetailScreen(
                    viewModel = detailViewModel,
                    onBack = { navController.popBackStack() },
                    onNavigateToPlans = {
                        navController.navigate(Screen.Plans.route) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(Screen.Plans.route) {
                PlansScreen(
                    viewModel = plansViewModel,
                    onPlanClick = { planId ->
                        navController.navigate(Screen.PlanDetail.createRoute(planId))
                    },
                    onStartWorkout = {
                        navController.navigate(Screen.ActiveWorkout.route) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(Screen.PlanDetail.route) { backStackEntry ->
                val planId = backStackEntry.arguments
                    ?.getString(Screen.PlanDetail.ARG_PLAN_ID)
                    .orEmpty()

                PlanDetailScreen(
                    viewModel = plansViewModel,
                    planId = planId,
                    onBack = { navController.popBackStack() },
                    onExerciseClick = { exerciseId ->
                        navController.navigate(Screen.ExerciseDetail.createRoute(exerciseId))
                    },
                    onStartWorkout = {
                        navController.navigate(Screen.ActiveWorkout.route) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(Screen.History.route) {
                HistoryScreen(viewModel = historyViewModel)
            }
        }
    }
}
