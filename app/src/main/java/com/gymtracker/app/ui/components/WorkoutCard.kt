package com.gymtracker.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gymtracker.app.domain.model.WorkoutSession
import com.gymtracker.app.ui.theme.GymCardStroke
import com.gymtracker.app.ui.theme.GymNeonGreen
import com.gymtracker.app.ui.theme.GymShapes
import com.gymtracker.app.ui.theme.GymSurface
import com.gymtracker.app.ui.theme.GymSurfaceVariant
import com.gymtracker.app.ui.theme.TextPrimary
import com.gymtracker.app.ui.theme.TextSecondary
import com.gymtracker.app.ui.theme.TextTertiary
import java.time.format.DateTimeFormatter

@Composable
fun WorkoutCard(
    workout: WorkoutSession,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormatter = DateTimeFormatter.ofPattern("EEEE, d MMMM")
    val formattedDate = workout.startedAt.format(dateFormatter)
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

    val durationMin = workout.durationSeconds / 60

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(GymSurface, GymShapes.medium)
            .border(1.dp, GymCardStroke, GymShapes.medium)
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = workout.title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "${workout.totalCompletedSets} series",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = GymNeonGreen
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = formattedDate,
                fontSize = 12.sp,
                color = TextTertiary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Exercise summary pills
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                workout.exercises.take(3).forEach { exerciseLog ->
                    Text(
                        text = "• ${exerciseLog.exercise.name} (${exerciseLog.sets.size} series)",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        maxLines = 1
                    )
                }
                if (workout.exercises.size > 3) {
                    Text(
                        text = "+ ${workout.exercises.size - 3} ejercicios más...",
                        fontSize = 12.sp,
                        color = TextTertiary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Footer with metrics: duration & volume
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GymSurfaceVariant.copy(alpha = 0.5f), GymShapes.small)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$durationMin min",
                        fontSize = 13.sp,
                        color = TextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FitnessCenter,
                        contentDescription = null,
                        tint = GymNeonGreen,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${String.format("%.0f", workout.totalVolumeKg)} kg",
                        fontSize = 13.sp,
                        color = GymNeonGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
