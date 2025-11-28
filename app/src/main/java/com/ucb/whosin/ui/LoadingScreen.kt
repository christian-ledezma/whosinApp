package com.ucb.whosin.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.ucb.whosin.ui.animations.LoadingDotsAnimation
import com.ucb.whosin.ui.theme.WhosInColors

@Composable
fun LoadingScreen() {

    // Animación de entrada (fade + scale)
    var startAnim by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        startAnim = true
    }

    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnim) 1f else 0f,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = ""
    )

    val scaleAnim by animateFloatAsState(
        targetValue = if (startAnim) 1f else 0.95f,
        animationSpec = spring(
            stiffness = Spring.StiffnessLow,
            dampingRatio = Spring.DampingRatioLowBouncy
        ),
        label = ""
    )

    // Fondo oscuro + círculos decorativos animados
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WhosInColors.DarkTeal)   // ← AQUI EL FONDO OSCURO
    ) {
        LoadingDecorationCircles()              // ← CÍRCULOS ENCIMA DEL FONDO

        val dots = LoadingDotsAnimation()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    alpha = alphaAnim
                    scaleX = scaleAnim
                    scaleY = scaleAnim
                },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                Text(
                    text = "Cargando$dots",
                    color = WhosInColors.LightGray,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(12.dp))

                // Texto con animación de parpadeo suave
                val fadeAnim by rememberInfiniteTransition().animateFloat(
                    initialValue = 0.4f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(800, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = ""
                )

                Text(
                    text = "Por favor espera...",
                    color = WhosInColors.GrayBlue,
                    modifier = Modifier.alpha(fadeAnim)
                )
            }
        }
    }
}

@Composable
private fun LoadingDecorationCircles() {
    val infinite = rememberInfiniteTransition()

    val offset by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    // Fondos animados
    Box(Modifier.fillMaxSize()) {

        // Círculo superior animado
        Box(
            modifier = Modifier
                .offset(x = (260 + offset).dp, y = (90 - offset).dp)
                .size(110.dp)
                .alpha(0.12f)
                .clip(CircleShape)
                .background(WhosInColors.LimeGreen)
        )

        // Círculo inferior animado
        Box(
            modifier = Modifier
                .offset(x = (-30 - offset).dp, y = (520 + offset).dp)
                .size(130.dp)
                .alpha(0.10f)
                .clip(CircleShape)
                .background(WhosInColors.PetrolBlue)
        )
    }
}

