package com.ucb.whosin

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.ucb.whosin.ui.theme.WhosInColors

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                SplashScreen()
            }
        }

        // Navegar a MainActivity después del splash
        lifecycleScope.launch {
            delay(3000) // 3 segundos de duración
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }
    }
}

@Composable
fun SplashScreen() {
    // Animación de fade in para toda la pantalla
    var startAnimation by remember { mutableStateOf(false) }

    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 1000,
            easing = FastOutSlowInEasing
        ),
        label = "alpha"
    )

    // Animación de escala para la primera imagen
    val scaleAnim1 = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.3f,
        animationSpec = tween(
            durationMillis = 800,
            easing = FastOutSlowInEasing
        ),
        label = "scale1"
    )

    // Animación de escala para la segunda imagen (con delay)
    val scaleAnim2 = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.3f,
        animationSpec = tween(
            durationMillis = 800,
            delayMillis = 300, // Empieza 300ms después
            easing = FastOutSlowInEasing
        ),
        label = "scale2"
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WhosInColors.DarkTeal),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.graphicsLayer(alpha = alphaAnim.value)
        ) {
            // Primera imagen
            Image(
                painter = painterResource(id = R.drawable.ic_splash_logo), // CAMBIA el nombre si es diferente
                contentDescription = "Logo",
                modifier = Modifier
                    .size(150.dp)
                    .graphicsLayer(
                        scaleX = scaleAnim1.value,
                        scaleY = scaleAnim1.value
                    )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Segunda imagen
            Image(
                painter = painterResource(id = R.drawable.ic_splash_text), // CAMBIA el nombre si es diferente
                contentDescription = "Texto",
                modifier = Modifier
                    .size(100.dp)
                    .graphicsLayer(
                        scaleX = scaleAnim2.value,
                        scaleY = scaleAnim2.value
                    )
            )
        }
    }
}