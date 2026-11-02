package com.gymtracker.app.ui.screens.onboarding

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gymtracker.app.data.local.UserPreferences
import com.gymtracker.app.domain.model.FitnessGoal
import com.gymtracker.app.domain.model.PresetPlan
import com.gymtracker.app.domain.model.PresetPlanCatalog
import com.gymtracker.app.domain.model.TrainingLocation
import com.gymtracker.app.ui.theme.GymBackground
import com.gymtracker.app.ui.theme.GymCardStroke
import com.gymtracker.app.ui.theme.GymPrimary
import com.gymtracker.app.ui.theme.GymShapes
import com.gymtracker.app.ui.theme.GymSurface
import com.gymtracker.app.ui.theme.GymSurfaceVariant
import com.gymtracker.app.ui.theme.TextPrimary
import com.gymtracker.app.ui.theme.TextSecondary
import com.gymtracker.app.ui.theme.TextTertiary

@Composable
fun PantallaOnboarding(
    userPreferences: UserPreferences,
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    var pasoActual by remember { mutableIntStateOf(1) } // 1: Datos, 2: Objetivo y Lugar, 3: Plan Recomendado

    var nombre by remember { mutableStateOf(userPreferences.getUserName()) }
    var estaturaTexto by remember { mutableStateOf("") }
    var pesoTexto by remember { mutableStateOf("") }

    var objetivoSeleccionado by remember { mutableStateOf(FitnessGoal.BUILD_MUSCLE) }
    var lugarSeleccionado by remember { mutableStateOf(TrainingLocation.GYM) }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GymBackground)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Top Navigation Bar (Back button + Steps indicator + Skip button)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (pasoActual > 1) {
                IconButton(onClick = { pasoActual -= 1 }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Atrás",
                        tint = TextSecondary
                    )
                }
            } else {
                Spacer(modifier = Modifier.size(48.dp))
            }

            // Step dots
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(3) { index ->
                    val isActive = index + 1 == pasoActual
                    val isPast = index + 1 < pasoActual
                    Box(
                        modifier = Modifier
                            .height(6.dp)
                            .width(if (isActive) 24.dp else 10.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(if (isActive || isPast) GymPrimary else GymSurfaceVariant)
                    )
                }
            }

            // Omitir por ahora
            TextButton(
                onClick = {
                    userPreferences.setOnboardingCompleted(true)
                    onNavigateToHome()
                }
            ) {
                Text(
                    text = "Omitir",
                    color = TextTertiary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Content area with safe direct switching (zero unbounded constraint issues)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (pasoActual) {
                1 -> PasoDatosFisicos(
                    nombre = nombre,
                    onNombreChange = { nombre = it },
                    estaturaTexto = estaturaTexto,
                    onEstaturaChange = { input ->
                        if (input.all { it.isDigit() } && input.length <= 3) {
                            estaturaTexto = input
                        }
                    },
                    pesoTexto = pesoTexto,
                    onPesoChange = { input ->
                        if (input.count { it == '.' || it == ',' } <= 1 && input.length <= 5) {
                            pesoTexto = input.replace(',', '.')
                        }
                    },
                    onContinuar = {
                        pasoActual = 2
                    }
                )
                2 -> PasoObjetivoYLugar(
                    objetivo = objetivoSeleccionado,
                    onObjetivoSelect = { objetivoSeleccionado = it },
                    lugar = lugarSeleccionado,
                    onLugarSelect = { lugarSeleccionado = it },
                    onContinuar = {
                        pasoActual = 3
                    }
                )
                3 -> {
                    val planRecomendado = remember(objetivoSeleccionado, lugarSeleccionado) {
                        PresetPlanCatalog.getRecommendedPlan(objetivoSeleccionado, lugarSeleccionado)
                    }

                    PasoPlanRecomendado(
                        plan = planRecomendado,
                        onAceptarPlan = {
                            val estatura = estaturaTexto.toIntOrNull()
                            val peso = pesoTexto.toDoubleOrNull()
                            userPreferences.saveUserProfile(
                                name = nombre.ifBlank { "Atleta" },
                                heightCm = estatura,
                                weightKg = peso,
                                goal = objetivoSeleccionado,
                                location = lugarSeleccionado,
                                planTitle = planRecomendado.title
                            )
                            onNavigateToHome()
                        },
                        onOmitirPlan = {
                            val estatura = estaturaTexto.toIntOrNull()
                            val peso = pesoTexto.toDoubleOrNull()
                            userPreferences.saveUserProfile(
                                name = nombre.ifBlank { "Atleta" },
                                heightCm = estatura,
                                weightKg = peso,
                                goal = objetivoSeleccionado,
                                location = lugarSeleccionado,
                                planTitle = null
                            )
                            onNavigateToHome()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// -------------------------------------------------------------
// PASO 1: DATOS PERSONALES
// -------------------------------------------------------------
@Composable
private fun PasoDatosFisicos(
    nombre: String,
    onNombreChange: (String) -> Unit,
    estaturaTexto: String,
    onEstaturaChange: (String) -> Unit,
    pesoTexto: String,
    onPesoChange: (String) -> Unit,
    onContinuar: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .size(68.dp)
                .background(GymPrimary.copy(alpha = 0.12f), CircleShape)
                .border(1.dp, GymPrimary.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.FitnessCenter,
                contentDescription = null,
                tint = GymPrimary,
                modifier = Modifier.size(34.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Bienvenido a GymControl",
            color = TextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Comencemos con tus datos básicos para personalizar tu experiencia.",
            color = TextSecondary,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(GymSurface, GymShapes.medium)
                .border(1.dp, GymCardStroke, GymShapes.medium)
                .padding(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OnboardingInputField(
                    label = "¿Cómo quieres que te llame?",
                    placeholder = "Ej: Carlos, Sofía, Max...",
                    value = nombre,
                    onValueChange = onNombreChange,
                    icon = Icons.Default.Person,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )

                OnboardingInputField(
                    label = "Estatura",
                    placeholder = "Ej: 178",
                    suffix = "cm",
                    value = estaturaTexto,
                    onValueChange = onEstaturaChange,
                    icon = Icons.Default.Height,
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )

                OnboardingInputField(
                    label = "Peso corporal",
                    placeholder = "Ej: 75.5",
                    suffix = "kg",
                    value = pesoTexto,
                    onValueChange = onPesoChange,
                    icon = Icons.Default.MonitorWeight,
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onContinuar,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = GymPrimary,
                contentColor = TextPrimary
            ),
            shape = GymShapes.medium
        ) {
            Text(
                text = "Siguiente",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// -------------------------------------------------------------
// PASO 2: OBJETIVO & LUGAR DE ENTRENAMIENTO
// -------------------------------------------------------------
@Composable
private fun PasoObjetivoYLugar(
    objetivo: FitnessGoal,
    onObjetivoSelect: (FitnessGoal) -> Unit,
    lugar: TrainingLocation,
    onLugarSelect: (TrainingLocation) -> Unit,
    onContinuar: () -> Unit
) {
    Column {
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "¿Cuál es tu objetivo principal?",
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Elegiremos una rutina con el volumen y la intensidad adecuada.",
            color = TextSecondary,
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Tarjetas de objetivos
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            FitnessGoal.entries.forEach { goalOption ->
                val isSelected = objetivo == goalOption
                val (icon, tint) = when (goalOption) {
                    FitnessGoal.BUILD_MUSCLE -> Icons.Default.TrendingUp to GymPrimary
                    FitnessGoal.GAIN_STRENGTH -> Icons.Default.FitnessCenter to GymPrimary
                    FitnessGoal.LOSE_FAT -> Icons.Default.Speed to GymPrimary
                }

                SelectableOptionCard(
                    title = goalOption.title,
                    subtitle = goalOption.subtitle,
                    isSelected = isSelected,
                    icon = icon,
                    iconTint = tint,
                    onClick = { onObjetivoSelect(goalOption) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "¿Dónde sueles entrenar?",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Adaptaremos los ejercicios a los equipos disponibles.",
            color = TextSecondary,
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TrainingLocation.entries.forEach { locOption ->
                val isSelected = lugar == locOption
                val icon = if (locOption == TrainingLocation.GYM) Icons.Default.FitnessCenter else Icons.Default.Home

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(if (isSelected) GymPrimary.copy(alpha = 0.12f) else GymSurface, GymShapes.medium)
                        .border(
                            1.dp,
                            if (isSelected) GymPrimary else GymCardStroke,
                            GymShapes.medium
                        )
                        .clickable { onLugarSelect(locOption) }
                        .padding(14.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isSelected) GymPrimary else TextSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = locOption.title,
                            color = if (isSelected) TextPrimary else TextSecondary,
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onContinuar,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = GymPrimary,
                contentColor = TextPrimary
            ),
            shape = GymShapes.medium
        ) {
            Text(
                text = "Ver Plan Recomendado",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// -------------------------------------------------------------
// PASO 3: PLAN RECOMENDADO & CONFIRMACIÓN
// -------------------------------------------------------------
@Composable
private fun PasoPlanRecomendado(
    plan: PresetPlan,
    onAceptarPlan: () -> Unit,
    onOmitirPlan: () -> Unit
) {
    Column {
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Tu Rutina Recomendada",
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Diseñada específicamente según tu objetivo y lugar de entrenamiento.",
            color = TextSecondary,
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Tarjeta destacada del Plan
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(GymSurface, GymShapes.large)
                .border(1.dp, GymPrimary.copy(alpha = 0.5f), GymShapes.large)
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = plan.title,
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Box(
                        modifier = Modifier
                            .background(GymPrimary.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${plan.daysPerWeek} DÍAS / SEM",
                            color = GymPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = plan.description,
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "EJERCICIOS INCLUIDOS:",
                    color = TextTertiary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    plan.exerciseNames.forEach { exerciseName ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = GymPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = exerciseName,
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botón: Aceptar este plan
        Button(
            onClick = onAceptarPlan,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = GymPrimary,
                contentColor = TextPrimary
            ),
            shape = GymShapes.medium
        ) {
            Text(
                text = "Aceptar este Plan y Comenzar",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Botón: Omitir plan
        OutlinedButton(
            onClick = onOmitirPlan,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = GymShapes.medium,
            border = androidx.compose.foundation.BorderStroke(1.dp, GymCardStroke)
        ) {
            Text(
                text = "Omitir plan y comenzar libre",
                color = TextSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// -------------------------------------------------------------
// COMPONENTES AUXILIARES
// -------------------------------------------------------------
@Composable
private fun SelectableOptionCard(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    icon: ImageVector,
    iconTint: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    val bg = if (isSelected) GymPrimary.copy(alpha = 0.1f) else GymSurface
    val stroke = if (isSelected) GymPrimary else GymCardStroke

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(bg, GymShapes.medium)
            .border(1.dp, stroke, GymShapes.medium)
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(if (isSelected) GymPrimary.copy(alpha = 0.2f) else GymSurfaceVariant, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isSelected) GymPrimary else TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = title,
                        color = if (isSelected) TextPrimary else TextSecondary,
                        fontSize = 15.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        color = TextTertiary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .background(GymPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = TextPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun OnboardingInputField(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector,
    keyboardType: KeyboardType,
    imeAction: ImeAction,
    suffix: String? = null
) {
    Column {
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(text = placeholder, color = TextTertiary, fontSize = 14.sp)
            },
            leadingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (value.isNotEmpty()) GymPrimary else TextTertiary,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = if (suffix != null) {
                {
                    Text(
                        text = suffix,
                        color = TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                }
            } else null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = GymBackground,
                unfocusedContainerColor = GymBackground,
                focusedBorderColor = GymPrimary,
                unfocusedBorderColor = GymCardStroke,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                cursorColor = GymPrimary
            ),
            shape = GymShapes.small,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = imeAction
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
