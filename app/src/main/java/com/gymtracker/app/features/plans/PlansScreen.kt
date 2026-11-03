package com.gymtracker.app.features.plans

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gymtracker.app.domain.model.CustomPlan
import com.gymtracker.app.domain.model.Exercise
import com.gymtracker.app.ui.components.ExerciseThumbnail
import com.gymtracker.app.ui.components.GymTopBar
import com.gymtracker.app.ui.components.MuscleGroupBadge
import com.gymtracker.app.ui.theme.GymBackground
import com.gymtracker.app.ui.theme.GymCardStroke
import com.gymtracker.app.ui.theme.GymNeonGreen
import com.gymtracker.app.ui.theme.GymShapes
import com.gymtracker.app.ui.theme.GymSurface
import com.gymtracker.app.ui.theme.GymSurfaceVariant
import com.gymtracker.app.ui.theme.TextPrimary
import com.gymtracker.app.ui.theme.TextSecondary
import com.gymtracker.app.ui.theme.TextTertiary

@Composable
fun PlansScreen(
    viewModel: PlansViewModel,
    onPlanClick: (String) -> Unit,
    onStartWorkout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showCreateDialog by remember { mutableStateOf(false) }

    LaunchedEffect(state.message) {
        val message = state.message
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = GymBackground,
        topBar = {
            GymTopBar(
                title = "Planes de Entrenamiento",
                actions = {
                    IconButton(onClick = { showCreateDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Crear plan",
                            tint = GymNeonGreen
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        if (state.plans.isEmpty() && !state.isLoading) {
            EmptyPlansState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                onCreatePlan = { showCreateDialog = true }
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Crea rutinas propias, actívalas y añade ejercicios desde el catálogo.",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                    )
                }

                items(state.plans, key = { it.id }) { plan ->
                    val planExercises = state.exercisesFor(plan)
                    PlanCard(
                        plan = plan,
                        exercises = planExercises,
                        isActive = plan.id == state.activePlanId,
                        onClick = { onPlanClick(plan.id) },
                        onActivate = { viewModel.setActivePlan(plan.id) },
                        onStart = { viewModel.startPlanWorkout(plan, onStartWorkout) }
                    )
                }

                item {
                    OutlinedButton(
                        onClick = { showCreateDialog = true },
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
                            text = "Crear nuevo plan",
                            color = GymNeonGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }

    if (showCreateDialog) {
        CreatePlanDialog(
            exercises = state.exercises,
            onDismiss = { showCreateDialog = false },
            onCreate = { name, exerciseIds ->
                viewModel.createPlan(
                    name = name,
                    exerciseIds = exerciseIds,
                    activate = true,
                    onCreated = { showCreateDialog = false }
                )
            }
        )
    }
}

@Composable
private fun EmptyPlansState(
    onCreatePlan: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.padding(28.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(GymNeonGreen.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Assignment,
                    contentDescription = null,
                    tint = GymNeonGreen,
                    modifier = Modifier.size(34.dp)
                )
            }
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = "Todavía no tienes planes",
                color = TextPrimary,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Crea tu primer plan personalizado, ponle nombre y agrupa los ejercicios que quieras entrenar.",
                color = TextSecondary,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(22.dp))
            Button(
                onClick = onCreatePlan,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GymNeonGreen,
                    contentColor = GymBackground
                ),
                shape = GymShapes.medium
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Crear mi primer plan", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun PlanCard(
    plan: CustomPlan,
    exercises: List<Exercise>,
    isActive: Boolean,
    onClick: () -> Unit,
    onActivate: () -> Unit,
    onStart: () -> Unit
) {
    val borderColor = if (isActive) GymNeonGreen.copy(alpha = 0.6f) else GymCardStroke

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(GymSurface, GymShapes.medium)
            .border(1.dp, borderColor, GymShapes.medium)
            .clickable(onClick = onClick)
            .padding(15.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = plan.name,
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "${plan.exerciseIds.size} ejercicios",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                if (isActive) {
                    Row(
                        modifier = Modifier
                            .background(GymNeonGreen.copy(alpha = 0.14f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = GymNeonGreen,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "ACTIVO",
                            color = GymNeonGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (exercises.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    exercises.take(5).forEach { exercise ->
                        ExerciseThumbnail(exercise = exercise, size = 34.dp)
                    }
                    if (exercises.size > 5) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(GymSurfaceVariant, RoundedCornerShape(11.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "+${exercises.size - 5}",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!isActive) {
                    OutlinedButton(
                        onClick = onActivate,
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        shape = GymShapes.small,
                        border = androidx.compose.foundation.BorderStroke(1.dp, GymCardStroke)
                    ) {
                        Text(
                            text = "Activar",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Button(
                    onClick = onStart,
                    enabled = plan.exerciseIds.isNotEmpty(),
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GymNeonGreen,
                        contentColor = GymBackground,
                        disabledContainerColor = GymSurfaceVariant,
                        disabledContentColor = TextTertiary
                    ),
                    shape = GymShapes.small
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Entrenar",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun CreatePlanDialog(
    exercises: List<Exercise>,
    onDismiss: () -> Unit,
    onCreate: (String, List<String>) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var query by remember { mutableStateOf("") }
    var selectedIds by remember { mutableStateOf(setOf<String>()) }

    val filteredExercises = remember(query, exercises) {
        if (query.isBlank()) {
            exercises
        } else {
            exercises.filter {
                it.name.contains(query, ignoreCase = true) ||
                    it.equipment.contains(query, ignoreCase = true) ||
                    it.muscleGroup.displayName.contains(query, ignoreCase = true)
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = GymSurface,
        title = {
            Text(
                text = "Nuevo plan de entrenamiento",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("Ej: Torso - Pierna", color = TextTertiary, fontSize = 13.sp) },
                    singleLine = true,
                    shape = GymShapes.small,
                    colors = planFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Buscar ejercicio...", color = TextTertiary, fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = TextTertiary,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { query = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Limpiar",
                                    tint = TextTertiary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = GymShapes.small,
                    colors = planFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${selectedIds.size} ejercicios seleccionados",
                    color = if (selectedIds.isEmpty()) TextTertiary else GymNeonGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(6.dp))

                LazyColumn(
                    modifier = Modifier.height(250.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filteredExercises, key = { it.id }) { exercise ->
                        ExerciseSelectionRow(
                            exercise = exercise,
                            isSelected = exercise.id in selectedIds,
                            onToggle = {
                                selectedIds = if (exercise.id in selectedIds) {
                                    selectedIds - exercise.id
                                } else {
                                    selectedIds + exercise.id
                                }
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onCreate(name, selectedIds.toList()) },
                enabled = name.isNotBlank() && selectedIds.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GymNeonGreen,
                    contentColor = GymBackground,
                    disabledContainerColor = GymSurfaceVariant,
                    disabledContentColor = TextTertiary
                )
            ) {
                Text(text = "Crear plan", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancelar", color = TextSecondary)
            }
        }
    )
}

@Composable
fun ExerciseSelectionRow(
    exercise: Exercise,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isSelected) GymNeonGreen.copy(alpha = 0.09f) else GymSurfaceVariant.copy(alpha = 0.4f),
                RoundedCornerShape(9.dp)
            )
            .border(
                1.dp,
                if (isSelected) GymNeonGreen.copy(alpha = 0.45f) else GymCardStroke,
                RoundedCornerShape(9.dp)
            )
            .clickable(onClick = onToggle)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ExerciseThumbnail(exercise = exercise, size = 38.dp)
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = exercise.name,
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(3.dp))
            MuscleGroupBadge(muscleGroup = exercise.muscleGroup)
        }
        Box(
            modifier = Modifier
                .size(22.dp)
                .background(
                    if (isSelected) GymNeonGreen else GymSurfaceVariant,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = GymBackground,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
fun planFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = GymBackground,
    unfocusedContainerColor = GymBackground,
    focusedBorderColor = GymNeonGreen,
    unfocusedBorderColor = GymCardStroke,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    cursorColor = GymNeonGreen
)
