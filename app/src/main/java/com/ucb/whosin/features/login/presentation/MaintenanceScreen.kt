package com.ucb.whosin.features.login.presentation

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.ucb.whosin.ui.theme.WhosInColors
import com.ucb.whosin.ui.animations.AnimatedEntrance
import com.ucb.whosin.ui.animations.PulseAnimation

@Composable
fun MaintenanceScreen(onLogout: () -> Unit) {

    // Animación de entrada reutilizable
    AnimatedEntrance {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(WhosInColors.DarkTeal)
        ) {

            // Fondo decorativo animado
            MaintenanceDecorationCircles()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Título animado con pulso suave
                PulseAnimation {
                    Text(
                        text = "🔧 En Mantenimiento",
                        style = MaterialTheme.typography.headlineLarge,
                        color = WhosInColors.White
                    )
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Estamos realizando mejoras.\nVuelve más tarde 🙏",
                    style = MaterialTheme.typography.bodyLarge,
                    color = WhosInColors.LightGray.copy(alpha = 0.9f)
                )

                Spacer(Modifier.height(32.dp))

                // Botón también animado
                PulseAnimation {
                    Button(
                        onClick = onLogout,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = WhosInColors.LimeGreen,
                            contentColor = WhosInColors.DarkTeal
                        ),
                        modifier = Modifier.graphicsLayer {
                            shadowElevation = 14f
                            shape = RoundedCornerShape(16.dp)
                            clip = true
                        }
                    ) {
                        Text("Cerrar sesión")
                    }
                }
            }
        }
    }
}

@Composable
private fun MaintenanceDecorationCircles() {
    val infinite = rememberInfiniteTransition()

    // Animación de movimiento suave ↑↓
    val offset by infinite.animateFloat(
        initialValue = -10f,
        targetValue = 18f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offsetAnim"
    )

    Box(Modifier.fillMaxSize()) {

        // Círculo superior derecho
        Box(
            modifier = Modifier
                .offset(x = (260 + offset).dp, y = (90 - offset).dp)
                .size(140.dp)
                .alpha(0.13f)
                .clip(CircleShape)
                .background(WhosInColors.LimeGreen)
        )

        // Círculo inferior izquierdo
        Box(
            modifier = Modifier
                .offset(x = (-40 - offset).dp, y = (480 + offset).dp)
                .size(130.dp)
                .alpha(0.12f)
                .clip(CircleShape)
                .background(WhosInColors.PetrolBlue)
        )
    }
}

