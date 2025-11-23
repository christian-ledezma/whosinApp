package com.ucb.whosin.features.login.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.LockOpen
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ucb.whosin.features.login.domain.model.CountryCode
import com.ucb.whosin.ui.components.WhosInPrimaryButton
import com.ucb.whosin.ui.components.WhosInSecondaryButton
import com.ucb.whosin.ui.components.WhosInTextField
import com.ucb.whosin.ui.theme.WhosInColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = koinViewModel(),
    onRegisterSuccess: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {}
) {
    // Envolver con el tema moderno - AISLADO del resto de la app
    WhosInModernTheme {
        RegisterScreenContent(
            viewModel = viewModel,
            onRegisterSuccess = onRegisterSuccess,
            onNavigateToLogin = onNavigateToLogin
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegisterScreenContent(
    viewModel: RegisterViewModel,
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }
    var startAnimation by remember { mutableStateOf(false) }
    var currentStep by remember { mutableIntStateOf(0) } // 0: datos personales, 1: credenciales

    LaunchedEffect(Unit) {
        delay(100)
        startAnimation = true
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = "¡Registro exitoso! Bienvenido/a",
                    duration = SnackbarDuration.Short
                )
                delay(1000)
                onRegisterSuccess()
            }
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { error ->
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = error,
                    duration = SnackbarDuration.Long
                )
            }
        }
    }

    // Country Picker Dialog
    if (uiState.showCountryPicker) {
        ModernCountryPickerDialog(
            onDismiss = { viewModel.dismissCountryPicker() },
            onCountrySelected = { viewModel.onCountryCodeSelect(it) },
            selectedCountry = uiState.selectedCountryCode
        )
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    shape = RoundedCornerShape(12.dp),
                    containerColor = WhosInColors.DarkTeal,
                    contentColor = WhosInColors.LightGray
                )
            }
        },
        containerColor = WhosInColors.LightGray
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Decoraciones de fondo
            RegisterDecorationCircles()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
                    .statusBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Header con botón atrás
                AnimatedEntrance(visible = startAnimation, delayMillis = 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                if (currentStep > 0) currentStep-- else onNavigateToLogin()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowBack,
                                contentDescription = "Atrás",
                                tint = WhosInColors.DarkTeal
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // Indicador de pasos
                        StepIndicator(
                            currentStep = currentStep,
                            totalSteps = 2
                        )

                        Spacer(modifier = Modifier.weight(1f))
                        Spacer(modifier = Modifier.width(48.dp)) // Balance
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Título dinámico
                AnimatedEntrance(visible = startAnimation, delayMillis = 100) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (currentStep == 0) "Crear Cuenta" else "Seguridad",
                            style = MaterialTheme.typography.displaySmall,
                            color = WhosInColors.DarkTeal,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (currentStep == 0)
                                "Ingresa tus datos personales"
                            else
                                "Configura tu acceso",
                            style = MaterialTheme.typography.bodyLarge,
                            color = WhosInColors.GrayBlue
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Contenido del formulario con animación
                AnimatedEntrance(visible = startAnimation, delayMillis = 200) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        color = WhosInColors.White,
                        shadowElevation = 8.dp
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            AnimatedVisibility(
                                visible = currentStep == 0,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                PersonalDataStep(
                                    uiState = uiState,
                                    viewModel = viewModel
                                )
                            }

                            AnimatedVisibility(
                                visible = currentStep == 1,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                CredentialsStep(
                                    uiState = uiState,
                                    viewModel = viewModel,
                                    showPassword = showPassword,
                                    onShowPasswordChange = { showPassword = it },
                                    showConfirmPassword = showConfirmPassword,
                                    onShowConfirmPasswordChange = { showConfirmPassword = it }
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Botones de navegación
                            if (currentStep == 0) {
                                WhosInPrimaryButton(
                                    text = "Continuar",
                                    onClick = { currentStep = 1 },
                                    enabled = uiState.name.isNotBlank() &&
                                            uiState.lastname.isNotBlank() &&
                                            uiState.phone.isNotBlank()
                                )
                            } else {
                                WhosInPrimaryButton(
                                    text = "Crear Cuenta",
                                    onClick = { viewModel.registerUser() },
                                    enabled = !uiState.isLoading,
                                    isLoading = uiState.isLoading
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                WhosInSecondaryButton(
                                    text = "Atrás",
                                    onClick = { currentStep = 0 }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Link a login
                AnimatedEntrance(visible = startAnimation, delayMillis = 300) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "¿Ya tienes cuenta?",
                            style = MaterialTheme.typography.bodyMedium,
                            color = WhosInColors.GrayBlue
                        )
                        TextButton(onClick = onNavigateToLogin) {
                            Text(
                                text = "Inicia sesión",
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
}

@Composable
private fun StepIndicator(currentStep: Int, totalSteps: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(totalSteps) { index ->
            val isActive = index <= currentStep
            val width by animateDpAsState(
                targetValue = if (index == currentStep) 32.dp else 12.dp,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "stepWidth"
            )

            Box(
                modifier = Modifier
                    .height(8.dp)
                    .width(width)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        if (isActive) WhosInColors.LimeGreen
                        else WhosInColors.GrayBlue.copy(alpha = 0.3f)
                    )
            )
        }
    }
}

@Composable
private fun PersonalDataStep(
    uiState: RegisterUiState,
    viewModel: RegisterViewModel
) {
    Column {
        // Nombre
        WhosInTextField(
            value = uiState.name,
            onValueChange = { viewModel.onNameChange(it) },
            label = "Nombre",
            placeholder = "Tu nombre",
            leadingIcon = Icons.Outlined.Person,
            isError = uiState.nameError != null,
            errorMessage = uiState.nameError,
            enabled = !uiState.isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Apellido Paterno
        WhosInTextField(
            value = uiState.lastname,
            onValueChange = { viewModel.onLastnameChange(it) },
            label = "Apellido Paterno",
            placeholder = "Tu apellido",
            leadingIcon = Icons.Outlined.Badge,
            isError = uiState.lastnameError != null,
            errorMessage = uiState.lastnameError,
            enabled = !uiState.isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Apellido Materno
        WhosInTextField(
            value = uiState.secondLastname,
            onValueChange = { viewModel.onSecondLastnameChange(it) },
            label = "Apellido Materno (opcional)",
            placeholder = "Opcional",
            leadingIcon = Icons.Outlined.Badge,
            enabled = !uiState.isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Teléfono con selector de país
        Text(
            text = "Teléfono",
            style = MaterialTheme.typography.labelLarge,
            color = WhosInColors.DarkTeal,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Country code selector
            Surface(
                modifier = Modifier
                    .height(56.dp)
                    .clickable { viewModel.toggleCountryPicker() },
                shape = RoundedCornerShape(12.dp),
                color = WhosInColors.White,
                border = ButtonDefaults.outlinedButtonBorder
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = uiState.selectedCountryCode.flag,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = uiState.selectedCountryCode.code,
                        style = MaterialTheme.typography.bodyMedium,
                        color = WhosInColors.DarkTeal
                    )
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowDown,
                        contentDescription = null,
                        tint = WhosInColors.GrayBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Phone number
            WhosInTextField(
                value = uiState.phone,
                onValueChange = { viewModel.onPhoneChange(it) },
                placeholder = "12345678",
                leadingIcon = Icons.Outlined.Phone,
                isError = uiState.phoneError != null,
                errorMessage = uiState.phoneError,
                enabled = !uiState.isLoading,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = KeyboardType.Phone
                ),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun CredentialsStep(
    uiState: RegisterUiState,
    viewModel: RegisterViewModel,
    showPassword: Boolean,
    onShowPasswordChange: (Boolean) -> Unit,
    showConfirmPassword: Boolean,
    onShowConfirmPasswordChange: (Boolean) -> Unit
) {
    Column {
        // Email
        WhosInTextField(
            value = uiState.email,
            onValueChange = { viewModel.onEmailChange(it) },
            label = "Correo electrónico",
            placeholder = "tu@email.com",
            leadingIcon = Icons.Outlined.Email,
            isError = uiState.emailError != null,
            errorMessage = uiState.emailError,
            enabled = !uiState.isLoading,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = KeyboardType.Email
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password
        WhosInTextField(
            value = uiState.password,
            onValueChange = { viewModel.onPasswordChange(it) },
            label = "Contraseña",
            placeholder = "Mínimo 6 caracteres",
            leadingIcon = Icons.Outlined.Lock,
            trailingIcon = {
                IconButton(
                    onClick = { onShowPasswordChange(!showPassword) },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (showPassword)
                            Icons.Outlined.VisibilityOff
                        else
                            Icons.Outlined.Visibility,
                        contentDescription = null,
                        tint = WhosInColors.GrayBlue
                    )
                }
            },
            visualTransformation = if (showPassword)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            isError = uiState.passwordError != null,
            errorMessage = uiState.passwordError,
            enabled = !uiState.isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Confirm Password
        WhosInTextField(
            value = uiState.confirmPassword,
            onValueChange = { viewModel.onConfirmPasswordChange(it) },
            label = "Confirmar contraseña",
            placeholder = "Repite tu contraseña",
            leadingIcon = Icons.Outlined.LockOpen,
            trailingIcon = {
                IconButton(
                    onClick = { onShowConfirmPasswordChange(!showConfirmPassword) },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (showConfirmPassword)
                            Icons.Outlined.VisibilityOff
                        else
                            Icons.Outlined.Visibility,
                        contentDescription = null,
                        tint = WhosInColors.GrayBlue
                    )
                }
            },
            visualTransformation = if (showConfirmPassword)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            isError = uiState.confirmPasswordError != null,
            errorMessage = uiState.confirmPasswordError,
            enabled = !uiState.isLoading
        )

        // Password strength indicator
        if (uiState.password.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            PasswordStrengthIndicator(password = uiState.password)
        }
    }
}

@Composable
private fun PasswordStrengthIndicator(password: String) {
    val strength = calculatePasswordStrength(password)

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(4) { index ->
                val isActive = index < strength.level
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            if (isActive) strength.color
                            else WhosInColors.GrayBlue.copy(alpha = 0.2f)
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = strength.label,
            style = MaterialTheme.typography.bodySmall,
            color = strength.color
        )
    }
}

private data class PasswordStrength(
    val level: Int,
    val label: String,
    val color: androidx.compose.ui.graphics.Color
)

private fun calculatePasswordStrength(password: String): PasswordStrength {
    var score = 0
    if (password.length >= 6) score++
    if (password.length >= 8) score++
    if (password.any { it.isDigit() }) score++
    if (password.any { !it.isLetterOrDigit() }) score++

    return when (score) {
        0, 1 -> PasswordStrength(1, "Débil", WhosInColors.Error)
        2 -> PasswordStrength(2, "Regular", WhosInColors.Warning)
        3 -> PasswordStrength(3, "Buena", WhosInColors.OliveGreen)
        else -> PasswordStrength(4, "Excelente", WhosInColors.Success)
    }
}

@Composable
private fun ModernCountryPickerDialog(
    onDismiss: () -> Unit,
    onCountrySelected: (CountryCode) -> Unit,
    selectedCountry: CountryCode
) {
    var searchQuery by remember { mutableStateOf("") }
    val groupedCountries = CountryCode.getAllGrouped()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.75f),
            shape = RoundedCornerShape(28.dp),
            color = WhosInColors.White
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Seleccionar país",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = WhosInColors.DarkTeal
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Cerrar",
                            tint = WhosInColors.GrayBlue
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Search
                WhosInTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = "Buscar país...",
                    leadingIcon = Icons.Outlined.Search
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Lista de países
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    groupedCountries.forEach { (region, countries) ->
                        val filteredCountries = countries.filter {
                            it.country.contains(searchQuery, ignoreCase = true) ||
                                    it.code.contains(searchQuery)
                        }

                        if (filteredCountries.isNotEmpty()) {
                            item {
                                Text(
                                    text = region,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = WhosInColors.OliveGreen,
                                    modifier = Modifier.padding(vertical = 12.dp)
                                )
                            }

                            items(filteredCountries) { country ->
                                ModernCountryItem(
                                    country = country,
                                    isSelected = country == selectedCountry,
                                    onClick = { onCountrySelected(country) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ModernCountryItem(
    country: CountryCode,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        color = if (isSelected)
            WhosInColors.LimeGreen.copy(alpha = 0.2f)
        else
            WhosInColors.White
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = country.flag,
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = country.country,
                style = MaterialTheme.typography.bodyLarge,
                color = WhosInColors.DarkTeal,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = country.code,
                style = MaterialTheme.typography.bodyMedium,
                color = WhosInColors.GrayBlue
            )

            if (isSelected) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = null,
                    tint = WhosInColors.OliveGreen,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun RegisterDecorationCircles() {
    val infiniteTransition = rememberInfiniteTransition(label = "registerDecor")

    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset"
    )

    Box(
        modifier = Modifier
            .offset(x = (300 + offset).dp, y = (100 - offset).dp)
            .size(150.dp)
            .alpha(0.12f)
            .clip(CircleShape)
            .background(WhosInColors.MintGreen)
    )

    Box(
        modifier = Modifier
            .offset(x = (-60 - offset).dp, y = (300 + offset).dp)
            .size(120.dp)
            .alpha(0.08f)
            .clip(CircleShape)
            .background(WhosInColors.OliveGreen)
    )
}