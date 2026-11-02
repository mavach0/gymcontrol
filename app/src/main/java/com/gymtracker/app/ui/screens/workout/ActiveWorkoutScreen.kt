package com.gymtracker.app.ui.screens.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gymtracker.app.domain.model.ExerciseLog
import com.gymtracker.app.ui.components.GymTopBar
import com.gymtracker.app.ui.components.MuscleGroupBadge
import com.gymtracker.app.ui.components.SetTrackerRow
import com.gymtracker.app.ui.theme.GymBackground
import com.gymtracker.app.ui.theme.GymCardStroke
import com.gymtracker.app.ui.theme.GymCyan
import com.gymtracker.app.ui.theme.GymNeonGreen
import com.gymtracker.app.ui.theme.GymRed
import com.gymtracker.app.ui.theme.GymShapes
import com.gymtracker.app.ui.theme.GymSurface
import com.gymtracker.app.ui.theme.GymSurfaceVariant
import com.gymtracker.app.ui.theme.TextPrimary
import com.gymtracker.app.ui.theme.TextSecondary
import com.gymtracker.app.ui.theme.TextTertiary

@Composable
fun ActiveWorkoutScreen(
    viewModel: WorkoutViewModel,
    onWorkoutFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.startWorkoutIfNotActive()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GymBackground)
    ) {
        GymTopBar(
            title = state.workout?.title ?: "Entrenar",
            navigationIcon = {
                if (state.workout != null) {
                    IconButton(onClick = { viewModel.discardWorkout(onWorkoutFinished) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Descartar",
                            tint = TextTertiary
                        )
                    }
                }
            },
            actions = {
                if (state.workout != null) {
                    Button(
                        onClick = { viewModel.finishWorkout(onWorkoutFinished) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GymNeonGreen,
                            contentColor = GymBackground
                        ),
                        shape = GymShapes.small,
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(
                            text = "TERMINAR",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        )

        // Rest Timer Banner
        if (state.isRestTimerRunning) {
            RestTimerBanner(
                secondsLeft = state.restTimerSeconds,
                onSkip = { viewModel.skipRestTimer() }
            )
        }

        val workout = state.workout

        if (workout == null) {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.FitnessCenter,
                        contentDescription = null,
                        tint = GymNeonGreen,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Sin sesión activa",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Inicia un nuevo entrenamiento para comenzar a registrar tus series y pesos.",
                        color = TextSecondary,
                        fontSize = 14.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { viewModel.startWorkoutIfNotActive() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GymNeonGreen,
                            contentColor = GymBackground
                        ),
                        shape = GymShapes.medium
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Empezar Entrenamiento", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Workout overview header
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(GymSurface, GymShapes.medium)
                            .border(1.dp, GymCardStroke, GymShapes.medium)
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Volumen Total", color = TextSecondary, fontSize = 11.sp)
                            Text(
                                text = "${String.format("%.0f", workout.totalVolumeKg)} kg",
                                color = GymNeonGreen,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Series Completas", color = TextSecondary, fontSize = 11.sp)
                            Text(
                                text = "${workout.totalCompletedSets} series",
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Exercises in Active Workout
                itemsIndexed(workout.exercises) { exIndex, exerciseLog ->
                    ActiveExerciseCard(
                        exerciseLog = exerciseLog,
                        onWeightChange = { setIdx, weight ->
                            val currentReps = exerciseLog.sets.getOrNull(setIdx)?.reps ?: 0
                            viewModel.updateSet(exIndex, setIdx, weight, currentReps)
                        },
                        onRepsChange = { setIdx, reps ->
                            val currentWeight = exerciseLog.sets.getOrNull(setIdx)?.weightKg ?: 0.0
                            viewModel.updateSet(exIndex, setIdx, currentWeight, reps)
                        },
                        onToggleSetComplete = { setIdx ->
                            viewModel.toggleSetCompleted(exIndex, setIdx)
                        },
                        onAddSet = {
                            viewModel.addSetToExercise(exIndex)
                        }
                    )
                }

                // Add Exercise Button
                item {
                    OutlinedButton(
                        onClick = { viewModel.toggleAddExerciseDialog(true) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = GymShapes.medium,
                        border = androidx.compose.foundation.BorderStroke(1.dp, GymNeonGreen)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = GymNeonGreen
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Añadir Ejercicio",
                            color = GymNeonGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }

    // Modal to pick exercise
    if (state.showAddExerciseDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.toggleAddExerciseDialog(false) },
            containerColor = GymSurface,
            title = {
                Text(
                    text = "Seleccionar Ejercicio",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier.height(300.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(state.allExercises) { _, ex ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(GymSurfaceVariant.copy(alpha = 0.5f))
                                .clickable {
                                    viewModel.addExerciseToWorkout(ex)
                                }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = ex.name,
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = ex.muscleGroup.displayName,
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = GymNeonGreen,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { viewModel.toggleAddExerciseDialog(false) }) {
                    Text(text = "Cancelar", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
private fun RestTimerBanner(
    secondsLeft: Int,
    onSkip: () -> Unit
) {
    val minutes = secondsLeft / 60
    val seconds = secondsLeft % 60
    val formattedTime = String.format("%02d:%02d", minutes, seconds)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(GymCyan.copy(alpha = 0.15f))
            .border(1.dp, GymCyan.copy(alpha = 0.3f))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.HourglassTop,
                contentDescription = null,
                tint = GymCyan,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Descanso: $formattedTime",
                color = GymCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .clickable { onSkip() }
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Saltar",
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Saltar",
                tint = TextPrimary,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
private fun ActiveExerciseCard(
    exerciseLog: ExerciseLog,
    onWeightChange: (setIdx: Int, weight: Double) -> Unit,
    onRepsChange: (setIdx: Int, reps: Int) -> Unit,
    onToggleSetComplete: (setIdx: Int) -> Unit,
    onAddSet: () -> Unit
) {
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
                    text = exerciseLog.exercise.name,
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                MuscleGroupBadge(muscleGroup = exerciseLog.exercise.muscleGroup)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Table headers
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "SERIE", color = TextTertiary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                Text(text = "PESO", color = TextTertiary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                Text(text = "REPETICIONES", color = TextTertiary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                Text(text = "LISTO", color = TextTertiary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Set rows
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                exerciseLog.sets.forEachIndexed { setIdx, set ->
                    SetTrackerRow(
                        set = set,
                        onWeightChange = { newWeight -> onWeightChange(setIdx, newWeight) },
                        onRepsChange = { newReps -> onRepsChange(setIdx, newReps) },
                        onToggleComplete = { onToggleSetComplete(setIdx) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Add set button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(GymSurfaceVariant.copy(alpha = 0.5f))
                    .clickable { onAddSet() }
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Añadir Serie",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
