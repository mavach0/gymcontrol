package com.gymtracker.app.features.workout

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
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gymtracker.app.domain.model.ExerciseLog
import com.gymtracker.app.ui.components.ExerciseThumbnail
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
    var showDiscardConfirm by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GymBackground)
    ) {
        GymTopBar(
            title = state.workout?.title ?: "Entrenar",
            navigationIcon = {
                if (state.workout != null) {
                    IconButton(onClick = { showDiscardConfirm = true }) {
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

        if (state.isRestTimerRunning) {
            RestTimerBanner(
                secondsLeft = state.restTimerSeconds,
                onSkip = { viewModel.skipRestTimer() }
            )
        }

        val workout = state.workout

        if (workout == null) {
            EmptyWorkoutState(onStart = { viewModel.startWorkoutIfNotActive() })
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
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

                itemsIndexed(
                    items = workout.exercises,
                    key = { _, log -> log.exercise.id }
                ) { exIndex, exerciseLog ->
                    ActiveExerciseCard(
                        exerciseLog = exerciseLog,
                        onWeightChange = { setIdx, weight ->
                            viewModel.updateWeight(exIndex, setIdx, weight)
                        },
                        onRepsDelta = { setIdx, delta ->
                            viewModel.adjustReps(exIndex, setIdx, delta)
                        },
                        onToggleSetComplete = { setIdx ->
                            viewModel.toggleSetCompleted(exIndex, setIdx)
                        },
                        onAddSet = { viewModel.addSetToExercise(exIndex) },
                        onRemoveSet = { viewModel.removeSetFromExercise(exIndex) },
                        onRemoveExercise = { viewModel.removeExerciseFromWorkout(exIndex) }
                    )
                }

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

    if (state.showAddExerciseDialog) {
        AddExerciseDialog(
            exercises = state.allExercises,
            addedExerciseIds = state.workout?.exercises?.map { it.exercise.id }?.toSet().orEmpty(),
            onSelect = { exercise -> viewModel.addExerciseToWorkout(exercise) },
            onDismiss = { viewModel.toggleAddExerciseDialog(false) }
        )
    }

    if (showDiscardConfirm) {
        AlertDialog(
            onDismissRequest = { showDiscardConfirm = false },
            containerColor = GymSurface,
            title = {
                Text(
                    text = "Descartar entrenamiento",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Se perderán las series y pesos registrados en esta sesión. ¿Quieres descartarla?",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDiscardConfirm = false
                        viewModel.discardWorkout(onWorkoutFinished)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GymRed,
                        contentColor = TextPrimary
                    )
                ) {
                    Text(text = "Descartar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardConfirm = false }) {
                    Text(text = "Cancelar", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
private fun EmptyWorkoutState(onStart: () -> Unit) {
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
                onClick = onStart,
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
}

@Composable
private fun AddExerciseDialog(
    exercises: List<com.gymtracker.app.domain.model.Exercise>,
    addedExerciseIds: Set<String>,
    onSelect: (com.gymtracker.app.domain.model.Exercise) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
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
                modifier = Modifier.height(340.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(exercises, key = { _, exercise -> exercise.id }) { _, exercise ->
                    val alreadyAdded = exercise.id in addedExerciseIds
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(GymSurfaceVariant.copy(alpha = 0.5f))
                            .clickable(enabled = !alreadyAdded) { onSelect(exercise) }
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            ExerciseThumbnail(exercise = exercise, size = 40.dp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = exercise.name,
                                    color = if (alreadyAdded) TextTertiary else TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = exercise.muscleGroup.displayName,
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                        Icon(
                            imageVector = if (alreadyAdded) Icons.Default.Close else Icons.Default.Add,
                            contentDescription = null,
                            tint = if (alreadyAdded) TextTertiary else GymNeonGreen,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cerrar", color = TextSecondary)
            }
        }
    )
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
    onRepsDelta: (setIdx: Int, delta: Int) -> Unit,
    onToggleSetComplete: (setIdx: Int) -> Unit,
    onAddSet: () -> Unit,
    onRemoveSet: () -> Unit,
    onRemoveExercise: () -> Unit
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                ExerciseThumbnail(exercise = exerciseLog.exercise, size = 42.dp)
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = exerciseLog.exercise.name,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    MuscleGroupBadge(muscleGroup = exerciseLog.exercise.muscleGroup)
                }
                IconButton(onClick = onRemoveExercise) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Quitar ejercicio",
                        tint = GymRed,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "SERIE", color = TextTertiary, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                Text(text = "PESO", color = TextTertiary, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                Text(text = "REPS", color = TextTertiary, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                Text(text = "LISTO", color = TextTertiary, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                exerciseLog.sets.forEachIndexed { setIdx, set ->
                    key(set.id) {
                        SetTrackerRow(
                            set = set,
                            onWeightChange = { newWeight -> onWeightChange(setIdx, newWeight) },
                            onRepsDelta = { delta -> onRepsDelta(setIdx, delta) },
                            onToggleComplete = { onToggleSetComplete(setIdx) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SetActionButton(
                    text = "Quitar serie",
                    icon = Icons.Default.Remove,
                    enabled = exerciseLog.sets.size > 1,
                    onClick = onRemoveSet,
                    modifier = Modifier.weight(1f)
                )
                SetActionButton(
                    text = "Añadir serie",
                    icon = Icons.Default.Add,
                    enabled = exerciseLog.sets.size < 20,
                    onClick = onAddSet,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun SetActionButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(GymSurfaceVariant.copy(alpha = if (enabled) 0.55f else 0.22f))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (enabled) TextSecondary else TextTertiary.copy(alpha = 0.45f),
            modifier = Modifier.size(15.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            color = if (enabled) TextSecondary else TextTertiary.copy(alpha = 0.45f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
