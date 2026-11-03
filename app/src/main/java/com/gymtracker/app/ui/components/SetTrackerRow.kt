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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    onRepsDelta: (Int) -> Unit,
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
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Número de serie
        Box(
            modifier = Modifier
                .size(26.dp)
                .background(GymSurfaceVariant, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${set.setNumber}",
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(6.dp))

        // Peso (kg)
        WeightField(
            setId = set.id,
            weightKg = set.weightKg,
            onWeightChange = onWeightChange,
            modifier = Modifier.width(66.dp)
        )

        Text(
            text = "kg",
            color = TextTertiary,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.width(6.dp))

        // Repeticiones: contador libre con mínimo 1
        RepsStepper(
            reps = set.reps,
            onRepsDelta = onRepsDelta
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Marcar serie como completada
        val buttonBg = if (set.isCompleted) GymNeonGreen else GymSurfaceVariant
        val iconColor = if (set.isCompleted) TextPrimary else TextSecondary

        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(buttonBg)
                .clickable { onToggleComplete() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Marcar como completada",
                tint = iconColor,
                modifier = Modifier.size(19.dp)
            )
        }
    }
}

@Composable
private fun RepsStepper(
    reps: Int,
    onRepsDelta: (Int) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        StepperButton(
            icon = Icons.Default.Remove,
            contentDescription = "Quitar repetición",
            enabled = reps > MIN_REPS,
            onClick = { onRepsDelta(-1) }
        )

        Text(
            text = "$reps",
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(28.dp)
        )

        StepperButton(
            icon = Icons.Default.Add,
            contentDescription = "Añadir repetición",
            enabled = reps < MAX_REPS,
            onClick = { onRepsDelta(1) }
        )
    }
}

@Composable
private fun StepperButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(26.dp)
            .clip(CircleShape)
            .background(GymSurfaceVariant.copy(alpha = if (enabled) 1f else 0.45f))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (enabled) TextPrimary else TextTertiary.copy(alpha = 0.5f),
            modifier = Modifier.size(15.dp)
        )
    }
}

@Composable
private fun WeightField(
    setId: String,
    weightKg: Double,
    onWeightChange: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    var text by remember(setId) { mutableStateOf(formatWeight(weightKg)) }

    LaunchedEffect(weightKg) {
        val parsed = text.replace(',', '.').toDoubleOrNull() ?: 0.0
        if (parsed != weightKg) {
            text = formatWeight(weightKg)
        }
    }

    Box(
        modifier = modifier
            .height(36.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(GymBackground)
            .padding(horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = text,
            onValueChange = { input ->
                val normalized = input.replace(',', '.')
                if (normalized.isEmpty() || WEIGHT_REGEX.matches(normalized)) {
                    text = normalized
                    onWeightChange(normalized.toDoubleOrNull() ?: 0.0)
                }
            },
            textStyle = TextStyle(
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            cursorBrush = SolidColor(GymNeonGreen),
            singleLine = true,
            decorationBox = { innerTextField ->
                if (text.isEmpty()) {
                    Text(
                        text = "0",
                        color = TextTertiary,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
                innerTextField()
            }
        )
    }
}

private fun formatWeight(weightKg: Double): String {
    if (weightKg <= 0.0) return ""
    return if (weightKg % 1.0 == 0.0) {
        weightKg.toInt().toString()
    } else {
        weightKg.toString()
    }
}

private val WEIGHT_REGEX = Regex("^\\d{0,3}(\\.\\d{0,2})?$")
private const val MIN_REPS = 1
private const val MAX_REPS = 999
