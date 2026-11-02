package com.gymtracker.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gymtracker.app.domain.model.MuscleGroup
import com.gymtracker.app.ui.theme.BadgeArms
import com.gymtracker.app.ui.theme.BadgeBack
import com.gymtracker.app.ui.theme.BadgeChest
import com.gymtracker.app.ui.theme.BadgeCore
import com.gymtracker.app.ui.theme.BadgeLegs
import com.gymtracker.app.ui.theme.BadgeShoulders

@Composable
fun MuscleGroupBadge(
    muscleGroup: MuscleGroup,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (muscleGroup) {
        MuscleGroup.CHEST -> BadgeChest.copy(alpha = 0.2f) to BadgeChest
        MuscleGroup.BACK -> BadgeBack.copy(alpha = 0.2f) to BadgeBack
        MuscleGroup.LEGS -> BadgeLegs.copy(alpha = 0.2f) to BadgeLegs
        MuscleGroup.SHOULDERS -> BadgeShoulders.copy(alpha = 0.2f) to BadgeShoulders
        MuscleGroup.ARMS -> BadgeArms.copy(alpha = 0.2f) to BadgeArms
        MuscleGroup.CORE -> BadgeCore.copy(alpha = 0.2f) to BadgeCore
    }

    Box(
        modifier = modifier
            .background(bgColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = muscleGroup.displayName.uppercase(),
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
