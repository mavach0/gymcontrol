package com.gymtracker.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.SportsGymnastics
import androidx.compose.material.icons.filled.SportsMartialArts
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gymtracker.app.domain.model.Exercise
import com.gymtracker.app.domain.model.MuscleGroup
import com.gymtracker.app.ui.theme.BadgeArms
import com.gymtracker.app.ui.theme.BadgeBack
import com.gymtracker.app.ui.theme.BadgeChest
import com.gymtracker.app.ui.theme.BadgeCore
import com.gymtracker.app.ui.theme.BadgeLegs
import com.gymtracker.app.ui.theme.BadgeShoulders

/**
 * Icono de referencia visual para cada ejercicio. Se apoya en el grupo muscular
 * para dar una pista rápida sin necesidad de descargar imágenes.
 */
@Composable
fun ExerciseThumbnail(
    exercise: Exercise,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp
) {
    val color = muscleGroupColor(exercise.muscleGroup)
    val shape = RoundedCornerShape(size / 3)

    Box(
        modifier = modifier
            .size(size)
            .background(color.copy(alpha = 0.14f), shape)
            .border(1.dp, color.copy(alpha = 0.32f), shape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = muscleGroupIcon(exercise.muscleGroup),
            contentDescription = exercise.name,
            tint = color,
            modifier = Modifier.size(size / 2)
        )
    }
}

fun muscleGroupColor(group: MuscleGroup): Color = when (group) {
    MuscleGroup.CHEST -> BadgeChest
    MuscleGroup.BACK -> BadgeBack
    MuscleGroup.LEGS -> BadgeLegs
    MuscleGroup.SHOULDERS -> BadgeShoulders
    MuscleGroup.ARMS -> BadgeArms
    MuscleGroup.CORE -> BadgeCore
}

fun muscleGroupIcon(group: MuscleGroup): ImageVector = when (group) {
    MuscleGroup.CHEST -> Icons.Default.FitnessCenter
    MuscleGroup.BACK -> Icons.Default.AccessibilityNew
    MuscleGroup.LEGS -> Icons.AutoMirrored.Filled.DirectionsRun
    MuscleGroup.SHOULDERS -> Icons.Default.SportsGymnastics
    MuscleGroup.ARMS -> Icons.Default.SportsMartialArts
    MuscleGroup.CORE -> Icons.Default.SelfImprovement
}
