package com.gymtracker.app.features.exercises

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import com.gymtracker.app.ui.components.ExerciseThumbnail
import com.gymtracker.app.ui.components.GymTopBar
import com.gymtracker.app.ui.components.MuscleGroupBadge
import com.gymtracker.app.ui.components.muscleGroupColor
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
fun ExerciseDetailScreen(
    viewModel: ExerciseDetailViewModel,
    onBack: () -> Unit,
    onNavigateToPlans: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showPlanPicker by remember { mutableStateOf(false) }

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
                title = "Detalle del Ejercicio",
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = TextSecondary
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        val exercise = state.exercise

        if (exercise == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No se encontró el ejercicio solicitado.",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ExerciseThumbnail(exercise = exercise, size = 76.dp)
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = exercise.name,
                            color = TextPrimary,
                            fontSize = 21.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(7.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MuscleGroupBadge(muscleGroup = exercise.muscleGroup)
                            Text(
                                text = "• ${exercise.equipment}",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Descanso recomendado: ${exercise.defaultRestSeconds}s",
                            color = TextTertiary,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                DetailSection(
                    title = "Cómo realizarlo correctamente",
                    icon = Icons.AutoMirrored.Filled.MenuBook,
                    iconTint = muscleGroupColor(exercise.muscleGroup)
                ) {
                    Text(
                        text = exercise.description.ifBlank {
                            "Realiza el movimiento de forma controlada, manteniendo una técnica estricta y un rango de movimiento completo."
                        },
                        color = TextSecondary,
                        fontSize = 14.sp,
                        lineHeight = 21.sp
                    )
                }

                if (exercise.tips.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))

                    DetailSection(
                        title = "Consejos técnicos",
                        icon = Icons.Default.Lightbulb,
                        iconTint = muscleGroupColor(exercise.muscleGroup)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                            exercise.tips.forEach { tip ->
                                Row(verticalAlignment = Alignment.Top) {
                                    Box(
                                        modifier = Modifier
                                            .padding(top = 2.dp)
                                            .size(18.dp)
                                            .background(
                                                muscleGroupColor(exercise.muscleGroup).copy(alpha = 0.16f),
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = muscleGroupColor(exercise.muscleGroup),
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(9.dp))
                                    Text(
                                        text = tip,
                                        color = TextSecondary,
                                        fontSize = 13.sp,
                                        lineHeight = 19.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                val activePlan = state.activePlan
                val activePlanName = activePlan?.name

                Button(
                    onClick = {
                        if (activePlan == null) {
                            showPlanPicker = true
                        } else {
                            viewModel.addToActivePlan()
                        }
                    },
                    enabled = activePlan == null || !state.isInActivePlan,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GymNeonGreen,
                        contentColor = GymBackground,
                        disabledContainerColor = GymSurfaceVariant,
                        disabledContentColor = TextTertiary
                    ),
                    shape = GymShapes.medium
                ) {
                    Icon(
                        imageVector = if (activePlan == null) Icons.AutoMirrored.Filled.Assignment else Icons.Default.Add,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when {
                            activePlan == null -> "Elegir plan de entrenamiento"
                            state.isInActivePlan -> "Ya está en $activePlanName"
                            else -> "Añadir a $activePlanName"
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (state.hasActiveWorkout) {
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = { viewModel.addToActiveWorkout() },
                        enabled = !state.isInActiveWorkout,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = GymShapes.medium,
                        border = androidx.compose.foundation.BorderStroke(1.dp, GymCardStroke)
                    ) {
                        Text(
                            text = if (state.isInActiveWorkout) {
                                "Ya está en el entrenamiento actual"
                            } else {
                                "Añadir al entrenamiento actual"
                            },
                            color = if (state.isInActiveWorkout) TextTertiary else TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                TextButton(
                    onClick = onNavigateToPlans,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Gestionar mis planes de entrenamiento",
                        color = TextTertiary,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    if (showPlanPicker) {
        PlanPickerDialog(
            plans = state.availablePlans,
            exerciseId = state.exercise?.id,
            onSelect = { planId ->
                viewModel.addToPlan(planId)
                showPlanPicker = false
            },
            onCreatePlan = {
                showPlanPicker = false
                onNavigateToPlans()
            },
            onDismiss = { showPlanPicker = false }
        )
    }
}

@Composable
private fun DetailSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: androidx.compose.ui.graphics.Color,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(GymSurface, GymShapes.medium)
            .border(1.dp, GymCardStroke, GymShapes.medium)
            .padding(16.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .background(iconTint.copy(alpha = 0.14f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun PlanPickerDialog(
    plans: List<CustomPlan>,
    exerciseId: String?,
    onSelect: (String) -> Unit,
    onCreatePlan: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = GymSurface,
        title = {
            Text(
                text = "Añadir a un plan",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            if (plans.isEmpty()) {
                Text(
                    text = "Todavía no tienes planes personalizados. Crea uno para agrupar tus ejercicios favoritos.",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            } else {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 320.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(plans, key = { it.id }) { plan ->
                        val alreadyIncluded = exerciseId != null && exerciseId in plan.exerciseIds
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(GymSurfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                .clickable(enabled = !alreadyIncluded) { onSelect(plan.id) }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = plan.name,
                                    color = if (alreadyIncluded) TextTertiary else TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "${plan.exerciseIds.size} ejercicios",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                            Icon(
                                imageVector = if (alreadyIncluded) Icons.Default.Check else Icons.Default.Add,
                                contentDescription = null,
                                tint = if (alreadyIncluded) TextTertiary else GymNeonGreen,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (plans.isEmpty()) {
                Button(
                    onClick = onCreatePlan,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GymNeonGreen,
                        contentColor = GymBackground
                    )
                ) {
                    Text(text = "Crear mi primer plan", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancelar", color = TextSecondary)
            }
        }
    )
}
