package com.gymtracker.app.features.exercises

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gymtracker.app.domain.model.MuscleGroup
import com.gymtracker.app.ui.components.ExerciseListItem
import com.gymtracker.app.ui.components.GymTopBar
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
fun ExercisesScreen(
    viewModel: ExercisesViewModel,
    onExerciseClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GymBackground)
    ) {
        GymTopBar(title = "Ejercicios")

        // Search bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            TextField(
                value = state.searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                placeholder = {
                    Text(text = "Buscar ejercicio o máquina...", color = TextTertiary, fontSize = 14.sp)
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = TextSecondary)
                },
                trailingIcon = {
                    if (state.searchQuery.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Limpiar",
                            tint = TextSecondary,
                            modifier = Modifier.clickable { viewModel.onSearchQueryChanged("") }
                        )
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = GymSurface,
                    unfocusedContainerColor = GymSurface,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = GymNeonGreen,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = GymShapes.medium,
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GymCardStroke, GymShapes.medium)
            )
        }

        // Muscle Group Filter Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                MuscleFilterChip(
                    text = "Todos",
                    isSelected = state.selectedMuscleGroup == null,
                    onClick = { viewModel.onMuscleGroupSelected(null) }
                )
            }
            items(MuscleGroup.entries) { group ->
                MuscleFilterChip(
                    text = group.displayName,
                    isSelected = state.selectedMuscleGroup == group,
                    onClick = { viewModel.onMuscleGroupSelected(group) }
                )
            }
        }

        // Exercise count
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${state.filteredExercises.size} ejercicios encontrados",
                color = TextTertiary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Exercise list
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(state.filteredExercises, key = { it.id }) { exercise ->
                ExerciseListItem(
                    exercise = exercise,
                    onClick = { onExerciseClick(exercise.id) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun MuscleFilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bg = if (isSelected) GymNeonGreen else GymSurface
    val textCol = if (isSelected) GymBackground else TextSecondary
    val strokeCol = if (isSelected) GymNeonGreen else GymCardStroke

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .border(1.dp, strokeCol, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textCol,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
