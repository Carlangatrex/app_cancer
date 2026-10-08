package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.local.CompanionProfileEntity
import com.example.ui.CaregiverCatalog
import com.example.ui.MicroRegulationExercise
import com.example.ui.TabooValidationCard
import kotlinx.coroutines.delay
import kotlin.math.min

@Composable
fun MicroRegulacionScreen(
    profile: CompanionProfileEntity,
    activeExercise: MicroRegulationExercise,
    onSelectExercise: (MicroRegulationExercise) -> Unit,
    onSendTabooCardToChat: (String) -> Unit,
    onNavigateBackToChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onNavigateBackToChat)

    var isRunning by rememberSaveable { mutableStateOf(false) }
    var remainingSeconds by rememberSaveable(activeExercise.id) {
        mutableIntStateOf(activeExercise.durationSeconds)
    }
    var cycleTick by remember { mutableIntStateOf(0) }
    val haptic = LocalHapticFeedback.current

    val totalCycle = activeExercise.inhaleSec + activeExercise.holdSec + activeExercise.exhaleSec
    val posInCycle = if (totalCycle > 0) cycleTick % totalCycle else 0

    val currentPhaseLabel = when {
        !isRunning && remainingSeconds == 0 -> "Pausa completada · Respira a tu ritmo"
        !isRunning -> "Toca «Iniciar 60s» para soltar el cuerpo"
        posInCycle < activeExercise.inhaleSec ->
            "Inhala suave por la nariz… (${activeExercise.inhaleSec - posInCycle}s)"
        posInCycle < activeExercise.inhaleSec + activeExercise.holdSec ->
            "Sostén sin apretar el pecho… (${activeExercise.inhaleSec + activeExercise.holdSec - posInCycle}s)"
        else ->
            "Suelta el aire lento y baja los hombros… (${totalCycle - posInCycle}s)"
    }

    val targetScale = when {
        !isRunning -> 0.62f
        posInCycle < activeExercise.inhaleSec -> 0.94f
        posInCycle < activeExercise.inhaleSec + activeExercise.holdSec -> 0.94f
        else -> 0.52f
    }

    val animatedScale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = tween(durationMillis = 950),
        label = "breathingCircleScale"
    )

    LaunchedEffect(isRunning, activeExercise.id) {
        while (isRunning && remainingSeconds > 0) {
            if (posInCycle == 0 || posInCycle == activeExercise.inhaleSec ||
                posInCycle == activeExercise.inhaleSec + activeExercise.holdSec
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            }
            delay(1000L)
            remainingSeconds = (remainingSeconds - 1).coerceAtLeast(0)
            cycleTick += 1
            if (remainingSeconds == 0) {
                isRunning = false
            }
        }
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val containerColor = MaterialTheme.colorScheme.primaryContainer

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Micro-Regulación en la Trinchera",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Pausas silenciosas de 60 segundos para la sala de quimio, el estacionamiento o el pasillo.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Exercise selector chips
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(CaregiverCatalog.microExercises, key = { it.id }) { exercise ->
                    FilterChip(
                        selected = exercise.id == activeExercise.id,
                        onClick = {
                            isRunning = false
                            onSelectExercise(exercise)
                        },
                        label = { Text(exercise.title, style = MaterialTheme.typography.labelLarge) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.SelfImprovement,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }
        }

        // Interactive Breathing & Somatic Canvas Card
        item {
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = activeExercise.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = activeExercise.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    // Responsive Canvas using DrawScope size.width and size.height
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(210.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val minDim = min(size.width, size.height)
                            val maxRadius = minDim * 0.45f
                            val currentRadius = maxRadius * animatedScale
                            val centerOffset = Offset(size.width / 2f, size.height / 2f)

                            // Outer soft sanctuary aura
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        secondaryColor.copy(alpha = 0.25f),
                                        containerColor.copy(alpha = 0.05f)
                                    ),
                                    center = centerOffset,
                                    radius = maxRadius
                                ),
                                radius = maxRadius,
                                center = centerOffset
                            )

                            // Guide ring
                            drawCircle(
                                color = primaryColor.copy(alpha = 0.22f),
                                radius = maxRadius * 0.88f,
                                center = centerOffset,
                                style = Stroke(width = 3f)
                            )

                            // Animated breathing core
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        primaryColor.copy(alpha = 0.85f),
                                        secondaryColor.copy(alpha = 0.65f)
                                    ),
                                    center = centerOffset,
                                    radius = currentRadius.coerceAtLeast(10f)
                                ),
                                radius = currentRadius,
                                center = centerOffset
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${remainingSeconds}s",
                                style = MaterialTheme.typography.displaySmall,
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Ritmo ${activeExercise.inhaleSec}-${activeExercise.holdSec}-${activeExercise.exhaleSec}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                            )
                        }
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = currentPhaseLabel,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                        )
                    }

                    // Step-by-step instructions
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        activeExercise.steps.forEachIndexed { idx, stepText ->
                            Text(
                                text = "${idx + 1}. $stepText",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                if (remainingSeconds == 0) {
                                    remainingSeconds = activeExercise.durationSeconds
                                    cycleTick = 0
                                }
                                isRunning = !isRunning
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("toggle_breathing_button")
                        ) {
                            Icon(
                                imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.size(6.dp))
                            Text(if (isRunning) "Pausar" else "Iniciar 60s")
                        }

                        OutlinedButton(
                            onClick = {
                                isRunning = false
                                remainingSeconds = activeExercise.durationSeconds
                                cycleTick = 0
                            },
                            modifier = Modifier.testTag("reset_breathing_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Replay,
                                contentDescription = "Reiniciar temporizador"
                            )
                        }
                    }

                    Text(
                        text = "«${activeExercise.closingAffirmation}» — ${profile.avatarName}",
                        style = MaterialTheme.typography.bodySmall,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Taboo Emotions Validation Section
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Validación de Emociones Tabú (Sin Clichés)",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Lo que todo cuidador primario llega a sentir en silencio y nadie le permite decir en voz alta.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        items(CaregiverCatalog.tabooCards, key = { it.id }) { card ->
            TabooValidationCardItem(
                card = card,
                avatarName = profile.avatarName,
                onTalkAboutThis = { onSendTabooCardToChat(card.chatStarterPrompt) }
            )
        }
    }
}

@Composable
private fun TabooValidationCardItem(
    card: TabooValidationCard,
    avatarName: String,
    onTalkAboutThis: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = card.tabooTitle,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold
            )
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.65f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = card.rawFeelingQuote,
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(12.dp)
                )
            }
            Text(
                text = "1. Validación sin juicio: ${card.validationBody}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "2. Entidad a tu esfuerzo: ${card.effortRecognition}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "3. Micro-regulación: ${card.microRegulation}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )

            OutlinedButton(
                onClick = onTalkAboutThis,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("taboo_card_talk_${card.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Forum,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text("Soltar esto con $avatarName")
            }
        }
    }
}
