package com.gymtracker.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gymtracker.app.domain.model.WorkoutSet
import com.gymtracker.app.ui.theme.GymBackground
import com.gymtracker.app.ui.theme.GymNeonGreen
import com.gymtracker.app.ui.theme.GymSurfaceVariant
import com.gymtracker.app.ui.theme.TextPrimary
import com.gymtracker.app.ui.theme.TextSecondary
import com.gymtracker.app.ui.theme.TextTertiary

@Composable
fun SetTrackerRow(
    set: WorkoutSet,
    onWeightChange: (Double) -> Unit,
    onRepsChange: (Int) -> Unit,
    onToggleComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rowBackground = if (set.isCompleted) {
        GymNeonGreen.copy(alpha = 0.08f)
    } else {
        GymSurfaceVariant.copy(alpha = 0.4f)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(rowBackground)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Set number badge
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(GymSurfaceVariant, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${set.setNumber}",
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Weight Input Box (kg)
        Box(
            modifier = Modifier
                .width(76.dp)
                .height(38.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(GymBackground)
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            BasicTextField(
                value = if (set.weightKg > 0.0) set.weightKg.toString() else "",
                onValueChange = { input ->
                    val weight = input.toDoubleOrNull() ?: 0.0
                    onWeightChange(weight)
                },
                textStyle = TextStyle(
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                cursorBrush = SolidColor(GymNeonGreen),
                singleLine = true,
                decorationBox = { innerTextField ->
                    if (set.weightKg == 0.0) {
                        Text(
                            text = "0 kg",
                            color = TextTertiary,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                    innerTextField()
                }
            )
        }

        Text(
            text = "kg",
            color = TextTertiary,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Reps Input Box
        Box(
            modifier = Modifier
                .width(64.dp)
                .height(38.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(GymBackground)
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            BasicTextField(
                value = if (set.reps > 0) set.reps.toString() else "",
                onValueChange = { input ->
                    val reps = input.toIntOrNull() ?: 0
                    onRepsChange(reps)
                },
                textStyle = TextStyle(
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                cursorBrush = SolidColor(GymNeonGreen),
                singleLine = true,
                decorationBox = { innerTextField ->
                    if (set.reps == 0) {
                        Text(
                            text = "reps",
                            color = TextTertiary,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                    innerTextField()
                }
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Complete Checkbox Button
        val buttonBg = if (set.isCompleted) GymNeonGreen else GymSurfaceVariant
        val iconColor = if (set.isCompleted) TextPrimary else TextSecondary

        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(buttonBg)
                .clickable { onToggleComplete() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Marcar como completada",
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
