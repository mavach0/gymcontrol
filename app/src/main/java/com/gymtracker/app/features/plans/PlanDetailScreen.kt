package com.gymtracker.app.features.plans

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import com.gymtracker.app.domain.model.Exercise
import com.gymtracker.app.ui.components.ExerciseListItem
import com.gymtracker.app.ui.components.GymTopBar
import com.gymtracker.app.ui.theme.GymBackground
import com.gymtracker.app.ui.theme.GymCardStroke
import com.gymtracker.app.ui.theme.GymNeonGreen
import com.gymtracker.app.ui.theme.GymRed
import com.gymtracker.app.ui.theme.GymShapes
import com.gymtracker.app.ui.theme.GymSurface
import com.gymtracker.app.ui.theme.GymSurfaceVariant
import com.gymtracker.app.ui.theme.TextPrimary
import com.gymtracker.app.ui.theme.TextSecondary
import com.gymtracker.app.ui.theme.TextTertiary

@Composable
fun PlanDetailScreen(
    viewModel: PlansViewModel,
    planId: String,
    onBack: () -> Unit,
    onExerciseClick: (String) -> Unit,
    onStartWorkout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showAddExercises by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(state.message) {
        val message = state.message
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            viewModel.clearMessage()
        }
    }

    val plan = state.planById(planId)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = GymBackground,
        topBar = {
            GymTopBar(
                title = plan?.name ?: "Plan",
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = TextSecondary
                        )
                    }
                },
                actions = {
                    if (plan != null) {
                        IconButton(onClick = { showDeleteConfirm = true }) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Eliminar plan",
                                tint = GymRed
                            )
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        if (plan == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (state.isLoading) "Cargando plan..." else "Este plan ya no existe.",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                    if (!state.isLoading) {
                        Spacer(modifier = Modifier.height(12.dp))
                        TextButton(onClick = onBack) {
                            Text(text = "Volver", color = GymNeonGreen)
                        }
                    }
                }
            }
        } else {
            val planExercises = state.exercisesFor(plan)
            val isActive = plan.id == state.activePlanId

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(GymSurface, GymShapes.medium)
                            .border(
                                1.dp,
                                if (isActive) GymNeonGreen.copy(alpha = 0.5f) else GymCardStroke,
                                GymShapes.medium
                            )
                            .padding(16.dp)
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
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${planExercises.size} ejercicios",
                                        color = TextSecondary,
                                        fontSize = 12.sp
                                    )
                                }

                                if (isActive) {
                                    Row(
                                        modifier = Modifier
                                            .background(
                                                GymNeonGreen.copy(alpha = 0.14f),
                                                RoundedCornerShape(20.dp)
                                            )
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

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = { viewModel.startPlanWorkout(plan, onStartWorkout) },
                                enabled = planExercises.isNotEmpty(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GymNeonGreen,
                                    contentColor = GymBackground,
                                    disabledContainerColor = GymSurfaceVariant,
                                    disabledContentColor = TextTertiary
                                ),
                                shape = GymShapes.medium
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Iniciar entrenamiento",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedButton(
                                onClick = {
                                    if (isActive) {
                                        viewModel.clearActivePlan()
                                    } else {
                                        viewModel.setActivePlan(plan.id)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp),
                                shape = GymShapes.small,
                                border = androidx.compose.foundation.BorderStroke(1.dp, GymCardStroke)
                            ) {
                                Text(
                                    text = if (isActive) "Desactivar plan" else "Marcar como plan activo",
                                    color = TextSecondary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Ejercicios del plan",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(onClick = { showAddExercises = true }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = GymNeonGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Añadir",
                                color = GymNeonGreen,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                if (planExercises.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(GymSurface, GymShapes.medium)
                                .border(1.dp, GymCardStroke, GymShapes.medium)
                                .padding(22.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Este plan todavía no tiene ejercicios.",
                                    color = TextSecondary,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = { showAddExercises = true },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = GymNeonGreen,
                                        contentColor = GymBackground
                                    ),
                                    shape = GymShapes.small
                                ) {
                                    Text(text = "Añadir ejercicios", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                } else {
                    items(planExercises, key = { it.id }) { exercise ->
                        ExerciseListItem(
                            exercise = exercise,
                            onClick = { onExerciseClick(exercise.id) },
                            onRemoveClick = { viewModel.removeExerciseFromPlan(plan.id, exercise.id) }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }

    if (showAddExercises && plan != null) {
        AddExercisesDialog(
            exercises = state.exercises.filterNot { it.id in plan.exerciseIds },
            onDismiss = { showAddExercises = false },
            onAdd = { ids ->
                viewModel.addExercisesToPlan(plan.id, ids)
                showAddExercises = false
            }
        )
    }

    if (showDeleteConfirm && plan != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            containerColor = GymSurface,
            title = {
                Text(
                    text = "Eliminar plan",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "¿Seguro que quieres eliminar \"${plan.name}\"? Esta acción no se puede deshacer.",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        viewModel.deletePlan(plan.id)
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GymRed,
                        contentColor = TextPrimary
                    )
                ) {
                    Text(text = "Eliminar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text(text = "Cancelar", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
private fun AddExercisesDialog(
    exercises: List<Exercise>,
    onDismiss: () -> Unit,
    onAdd: (List<String>) -> Unit
) {
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
                text = "Añadir ejercicios",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
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
                    text = "${selectedIds.size} seleccionados",
                    color = if (selectedIds.isEmpty()) TextTertiary else GymNeonGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(6.dp))

                if (filteredExercises.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay más ejercicios disponibles.",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.height(260.dp),
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
            }
        },
        confirmButton = {
            Button(
                onClick = { onAdd(selectedIds.toList()) },
                enabled = selectedIds.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GymNeonGreen,
                    contentColor = GymBackground,
                    disabledContainerColor = GymSurfaceVariant,
                    disabledContentColor = TextTertiary
                )
            ) {
                Text(text = "Añadir", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancelar", color = TextSecondary)
            }
        }
    )
}
