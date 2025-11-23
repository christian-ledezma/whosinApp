package com.ucb.whosin.features.login.presentation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ucb.whosin.ui.components.WhosInPrimaryButton
import com.ucb.whosin.ui.components.WhosInTextField
import com.ucb.whosin.ui.theme.WhosInColors
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun AnimatedEntrance(
    visible: Boolean,
    delayMillis: Int = 0,
    content: @Composable () -> Unit
) {
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 400, delayMillis = delayMillis, easing = FastOutSlowInEasing)
    )

    val offsetY by animateDpAsState(
        targetValue = if (visible) 0.dp else 20.dp,
        animationSpec = tween(durationMillis = 400, delayMillis = delayMillis, easing = FastOutSlowInEasing)
    )

    Box(
        modifier = Modifier
            .offset(y = offsetY)
            .alpha(alpha)
    ) {
        content()
    }
}
@Composable
fun WhosInModernTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        content = content
    )
}
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = koinViewModel(),
    onLoginSuccess: () -> Unit = {},
    onNavigateToRegister: () -> Unit = {}
) {
    WhosInModernTheme {
        LoginScreenContent(
            viewModel = viewModel,
            onLoginSuccess = onLoginSuccess,
            onNavigateToRegister = onNavigateToRegister
        )
    }
}

@Composable
private fun LoginScreenContent(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var startAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        startAnimation = true
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WhosInColors.LightGray)
    ) {
        // Elementos decorativos de fondo
        DecorationCircles()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Logo / Branding
            AnimatedEntrance(visible = startAnimation, delayMillis = 0) {
                LogoSection()
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Título
            AnimatedEntrance(visible = startAnimation, delayMillis = 100) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Bienvenido",
                        style = MaterialTheme.typography.displayMedium,
                        color = WhosInColors.DarkTeal,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Inicia sesión para continuar",
                        style = MaterialTheme.typography.bodyLarge,
                        color = WhosInColors.GrayBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Card con formulario
            AnimatedEntrance(visible = startAnimation, delayMillis = 200) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = WhosInColors.White,
                    shadowElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        // Campo Email
                        WhosInTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = "Correo electrónico",
                            placeholder = "tu@email.com",
                            leadingIcon = Icons.Outlined.Email,
                            enabled = !uiState.isLoading,
                            isError = uiState.errorMessage != null,
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                keyboardType = KeyboardType.Email
                            )
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Campo Contraseña
                        WhosInTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = "Contraseña",
                            placeholder = "••••••••",
                            leadingIcon = Icons.Outlined.Lock,
                            trailingIcon = {
                                IconButton(
                                    onClick = { showPassword = !showPassword },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = if (showPassword)
                                            Icons.Outlined.VisibilityOff
                                        else
                                            Icons.Outlined.Visibility,
                                        contentDescription = if (showPassword) "Ocultar" else "Mostrar",
                                        tint = WhosInColors.GrayBlue
                                    )
                                }
                            },
                            visualTransformation = if (showPassword)
                                VisualTransformation.None
                            else
                                PasswordVisualTransformation(),
                            enabled = !uiState.isLoading,
                            isError = uiState.errorMessage != null
                        )

                        // Mensaje de error
                        uiState.errorMessage?.let { error ->
                            Spacer(modifier = Modifier.height(16.dp))
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                color = WhosInColors.Error.copy(alpha = 0.1f)
                            ) {
                                Text(
                                    text = error,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = WhosInColors.Error,
                                    modifier = Modifier.padding(12.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Olvidaste contraseña
                        TextButton(
                            onClick = { /* TODO */ },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(
                                text = "¿Olvidaste tu contraseña?",
                                style = MaterialTheme.typography.bodyMedium,
                                color = WhosInColors.OliveGreen
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Botón Login
                        WhosInPrimaryButton(
                            text = "Iniciar Sesión",
                            onClick = { viewModel.loginUser(email, password) },
                            enabled = email.isNotBlank() && password.isNotBlank() && !uiState.isLoading,
                            isLoading = uiState.isLoading
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Link a registro
            AnimatedEntrance(visible = startAnimation, delayMillis = 300) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "¿No tienes cuenta?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = WhosInColors.GrayBlue
                    )
                    TextButton(onClick = onNavigateToRegister) {
                        Text(
                            text = "Regístrate aquí",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = WhosInColors.DarkTeal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun LogoSection() {
    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        WhosInColors.DarkTeal,
                        WhosInColors.PetrolBlue
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "W",
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold
            ),
            color = WhosInColors.LimeGreen
        )
    }
}

@Composable
private fun DecorationCircles() {
    val infiniteTransition = rememberInfiniteTransition(label = "decoration")

    val offset1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset1"
    )

    val offset2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset2"
    )

    // Círculo superior derecho
    Box(
        modifier = Modifier
            .offset(x = (280 + offset1).dp, y = (-50 + offset2).dp)
            .size(200.dp)
            .alpha(0.15f)
            .clip(CircleShape)
            .background(WhosInColors.LimeGreen)
    )

    // Círculo inferior izquierdo
    Box(
        modifier = Modifier
            .offset(x = (-80 + offset2).dp, y = (600 + offset1).dp)
            .size(180.dp)
            .alpha(0.1f)
            .clip(CircleShape)
            .background(WhosInColors.OliveGreen)
    )

    // Círculo pequeño decorativo
    Box(
        modifier = Modifier
            .offset(x = (320 + offset2).dp, y = (400 + offset1).dp)
            .size(80.dp)
            .alpha(0.08f)
            .clip(CircleShape)
            .background(WhosInColors.DarkTeal)
    )
}