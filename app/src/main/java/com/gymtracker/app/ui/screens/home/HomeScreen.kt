package com.gymtracker.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gymtracker.app.ui.components.GymStatCard
import com.gymtracker.app.ui.components.GymTopBar
import com.gymtracker.app.ui.components.WorkoutCard
import com.gymtracker.app.ui.theme.GymBackground
import com.gymtracker.app.ui.theme.GymCardStroke
import com.gymtracker.app.ui.theme.GymCyan
import com.gymtracker.app.ui.theme.GymOrange
import com.gymtracker.app.ui.theme.GymPrimary
import com.gymtracker.app.ui.theme.GymShapes
import com.gymtracker.app.ui.theme.GymSurface
import com.gymtracker.app.ui.theme.GymSurfaceVariant
import com.gymtracker.app.ui.theme.TextPrimary
import com.gymtracker.app.ui.theme.TextSecondary
import com.gymtracker.app.ui.theme.TextTertiary

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToActiveWorkout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GymBackground)
    ) {
        GymTopBar(
            title = "GymTracker"
        )

        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = GymPrimary)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // User Greeting Header
                item {
                    val greetingName = if (state.userName.isNotBlank()) " ${state.userName}" else ""
                    Column(modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)) {
                        Text(
                            text = "¡Hola$greetingName! 👋",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = if (state.streakDays > 0) "¡Gran constancia! Listo para el siguiente entreno."
                            else "Comienza hoy tu registro de entrenamientos.",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }
                }

                // Days of week streak strip (Dynamic according to actual workouts)
                item {
                    WeeklyStreakStrip(
                        activeDays = state.completedDaysOfWeek,
                        completedCount = state.completedWorkoutsThisWeek
                    )
                }

                // Routine Plan Card if chosen
                if (!state.selectedPlanTitle.isNullOrBlank()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(GymSurface, GymShapes.medium)
                                .border(1.dp, GymPrimary.copy(alpha = 0.35f), GymShapes.medium)
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .background(GymPrimary.copy(alpha = 0.15f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Assignment,
                                            contentDescription = null,
                                            tint = GymPrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "TU PLAN ACTIVO",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GymPrimary,
                                            letterSpacing = 0.5.sp
                                        )
                                        Text(
                                            text = state.selectedPlanTitle ?: "",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TextPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // CTA Button: Start Workout
                item {
                    Button(
                        onClick = {
                            viewModel.startQuickWorkout(onNavigateToActiveWorkout)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GymPrimary,
                            contentColor = TextPrimary
                        ),
                        shape = GymShapes.medium
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = TextPrimary
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = if (state.hasActiveWorkout) "CONTINUAR SESIÓN"
                            else if (!state.selectedPlanTitle.isNullOrBlank()) "ENTRENAR: ${state.selectedPlanTitle?.uppercase()}"
                            else "INICIAR ENTRENAMIENTO",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                // Stats Metrics Grid
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        GymStatCard(
                            title = "Racha",
                            value = "${state.streakDays} días",
                            subtitle = if (state.streakDays > 0) "¡En racha!" else "Sin racha aún",
                            icon = Icons.Default.LocalFireDepartment,
                            iconTint = GymOrange,
                            modifier = Modifier.weight(1f)
                        )
                        GymStatCard(
                            title = "Volumen Semanal",
                            value = "${String.format("%.0f", state.weeklyVolumeKg)} kg",
                            subtitle = "Esta semana",
                            icon = Icons.Default.TrendingUp,
                            iconTint = GymCyan,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Recent Workouts Section Header
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Historial Reciente",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                // List of Recent Workouts or Clean Empty State
                if (state.recentWorkouts.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(GymSurface, GymShapes.medium)
                                .border(1.dp, GymCardStroke, GymShapes.medium)
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(GymSurfaceVariant, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FitnessCenter,
                                        contentDescription = null,
                                        tint = TextTertiary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Sin entrenamientos completados",
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Cuando termines tu primera sesión, aquí se registrarán tus series, pesos y marcas alcanzadas.",
                                    color = TextSecondary,
                                    fontSize = 13.sp,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                } else {
                    items(state.recentWorkouts) { workout ->
                        WorkoutCard(
                            workout = workout,
                            onClick = { /* Detail dialog or view */ }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun WeeklyStreakStrip(
    activeDays: Set<Int>,
    completedCount: Int
) {
    val days = listOf("L", "M", "X", "J", "V", "S", "D")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(GymSurface, GymShapes.medium)
            .border(1.dp, GymCardStroke, GymShapes.medium)
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Consistencia Semanal",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Text(
                    text = "$completedCount días completados",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (completedCount > 0) GymPrimary else TextTertiary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                days.forEachIndexed { index, day ->
                    val isActive = index in activeDays
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = day,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isActive) TextPrimary else TextTertiary
                        )
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(if (isActive) GymPrimary else GymSurfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isActive) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = TextPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
