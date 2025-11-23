package com.ucb.whosin.features.login.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ucb.whosin.features.login.domain.model.CountryCode
import com.ucb.whosin.ui.components.WhosInPrimaryButton
import com.ucb.whosin.ui.components.WhosInTextField
import com.ucb.whosin.ui.theme.WhosInColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import kotlin.collections.component1
import kotlin.collections.component2

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = koinViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    WhosInModernTheme {
        ProfileScreenContent(
            viewModel = viewModel,
            onNavigateBack = onNavigateBack
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileScreenContent(
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showPasswordSection by remember { mutableStateOf(false) }
    var showCurrentPassword by remember { mutableStateOf(false) }
    var showNewPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }
    var startAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        startAnimation = true
    }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let { message ->
            scope.launch {
                snackbarHostState.showSnackbar(message)
                viewModel.clearMessages()
            }
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { error ->
            scope.launch {
                snackbarHostState.showSnackbar(error)
                viewModel.clearMessages()
            }
        }
    }

    // Diálogo selector de país
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
        containerColor = WhosInColors.DarkTeal
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Decoraciones de fondo
            ProfileDecorationCircles()

            if (uiState.isLoading) {
                // Loading state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(
                            color = WhosInColors.DarkTeal,
                            strokeWidth = 3.dp
                        )
                        Text(
                            text = "Cargando perfil...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = WhosInColors.GrayBlue
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp)
                        .statusBarsPadding()
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Header con botón atrás
                    AnimatedEntrance(visible = startAnimation, delayMillis = 0) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = onNavigateBack,
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(WhosInColors.White)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.ArrowBack,
                                    contentDescription = "Volver",
                                    tint = WhosInColors.DarkTeal
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Text(
                                text = "Mi Perfil",
                                style = MaterialTheme.typography.headlineMedium,
                                color = WhosInColors.DarkTeal,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Avatar y nombre
                    AnimatedEntrance(visible = startAnimation, delayMillis = 100) {
                        ProfileHeader(
                            name = uiState.name,
                            lastname = uiState.lastname,
                            email = uiState.email
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Card de información personal
                    AnimatedEntrance(visible = startAnimation, delayMillis = 200) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            color = WhosInColors.White,
                            shadowElevation = 8.dp
                        ) {
                            Column(modifier = Modifier.padding(24.dp)) {
                                // Título de sección
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Person,
                                        contentDescription = null,
                                        tint = WhosInColors.OliveGreen,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = "Información Personal",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = WhosInColors.DarkTeal,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                // Email (solo lectura)
                                WhosInTextField(
                                    value = uiState.email,
                                    onValueChange = {},
                                    label = "Correo electrónico",
                                    leadingIcon = Icons.Outlined.Email,
                                    enabled = false
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Nombre
                                WhosInTextField(
                                    value = uiState.name,
                                    onValueChange = { viewModel.onNameChange(it) },
                                    label = "Nombre",
                                    placeholder = "Tu nombre",
                                    leadingIcon = Icons.Outlined.Badge,
                                    enabled = !uiState.isSaving
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Apellido Paterno
                                WhosInTextField(
                                    value = uiState.lastname,
                                    onValueChange = { viewModel.onLastnameChange(it) },
                                    label = "Apellido Paterno",
                                    placeholder = "Tu apellido",
                                    leadingIcon = Icons.Outlined.Badge,
                                    enabled = !uiState.isSaving
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Apellido Materno
                                WhosInTextField(
                                    value = uiState.secondLastname,
                                    onValueChange = { viewModel.onSecondLastnameChange(it) },
                                    label = "Apellido Materno (opcional)",
                                    placeholder = "Opcional",
                                    leadingIcon = Icons.Outlined.Badge,
                                    enabled = !uiState.isSaving
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Teléfono
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
                                    // Country selector
                                    Surface(
                                        modifier = Modifier
                                            .height(56.dp)
                                            .clickable(enabled = !uiState.isSaving) {
                                                viewModel.toggleCountryPicker()
                                            },
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

                                    WhosInTextField(
                                        value = uiState.phone,
                                        onValueChange = { viewModel.onPhoneChange(it) },
                                        placeholder = "12345678",
                                        leadingIcon = Icons.Outlined.Phone,
                                        enabled = !uiState.isSaving,
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Phone
                                        ),
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                // Botón guardar
                                WhosInPrimaryButton(
                                    text = "Guardar Cambios",
                                    onClick = { viewModel.saveProfile() },
                                    enabled = !uiState.isSaving,
                                    isLoading = uiState.isSaving
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Card de seguridad
                    AnimatedEntrance(visible = startAnimation, delayMillis = 300) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            color = WhosInColors.White,
                            shadowElevation = 8.dp
                        ) {
                            Column(modifier = Modifier.padding(24.dp)) {
                                // Header de seguridad
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { showPasswordSection = !showPasswordSection },
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Outlined.Lock,
                                            contentDescription = null,
                                            tint = WhosInColors.OliveGreen,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = "Seguridad",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = WhosInColors.DarkTeal,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    Icon(
                                        imageVector = if (showPasswordSection)
                                            Icons.Rounded.KeyboardArrowUp
                                        else
                                            Icons.Rounded.KeyboardArrowDown,
                                        contentDescription = null,
                                        tint = WhosInColors.GrayBlue
                                    )
                                }

                                // Contenido expandible
                                AnimatedVisibility(
                                    visible = showPasswordSection,
                                    enter = fadeIn() + expandVertically(),
                                    exit = fadeOut() + shrinkVertically()
                                ) {
                                    Column {
                                        Spacer(modifier = Modifier.height(20.dp))

                                        HorizontalDivider(
                                            color = WhosInColors.GrayBlue.copy(alpha = 0.2f)
                                        )

                                        Spacer(modifier = Modifier.height(20.dp))

                                        // Contraseña actual
                                        WhosInTextField(
                                            value = uiState.currentPassword,
                                            onValueChange = { viewModel.onCurrentPasswordChange(it) },
                                            label = "Contraseña actual",
                                            placeholder = "••••••••",
                                            leadingIcon = Icons.Outlined.Lock,
                                            trailingIcon = {
                                                IconButton(
                                                    onClick = { showCurrentPassword = !showCurrentPassword },
                                                    modifier = Modifier.size(24.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = if (showCurrentPassword)
                                                            Icons.Outlined.VisibilityOff
                                                        else
                                                            Icons.Outlined.Visibility,
                                                        contentDescription = null,
                                                        tint = WhosInColors.GrayBlue
                                                    )
                                                }
                                            },
                                            visualTransformation = if (showCurrentPassword)
                                                VisualTransformation.None
                                            else
                                                PasswordVisualTransformation(),
                                            enabled = !uiState.isChangingPassword
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))

                                        // Nueva contraseña
                                        WhosInTextField(
                                            value = uiState.newPassword,
                                            onValueChange = { viewModel.onNewPasswordChange(it) },
                                            label = "Nueva contraseña",
                                            placeholder = "Mínimo 6 caracteres",
                                            leadingIcon = Icons.Outlined.LockOpen,
                                            trailingIcon = {
                                                IconButton(
                                                    onClick = { showNewPassword = !showNewPassword },
                                                    modifier = Modifier.size(24.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = if (showNewPassword)
                                                            Icons.Outlined.VisibilityOff
                                                        else
                                                            Icons.Outlined.Visibility,
                                                        contentDescription = null,
                                                        tint = WhosInColors.GrayBlue
                                                    )
                                                }
                                            },
                                            visualTransformation = if (showNewPassword)
                                                VisualTransformation.None
                                            else
                                                PasswordVisualTransformation(),
                                            enabled = !uiState.isChangingPassword
                                        )

                                        // Indicador de fuerza
                                        if (uiState.newPassword.isNotEmpty()) {
                                            Spacer(modifier = Modifier.height(12.dp))
                                            PasswordStrengthIndicator(password = uiState.newPassword)
                                        }

                                        Spacer(modifier = Modifier.height(16.dp))

                                        // Confirmar contraseña
                                        WhosInTextField(
                                            value = uiState.confirmPassword,
                                            onValueChange = { viewModel.onConfirmPasswordChange(it) },
                                            label = "Confirmar nueva contraseña",
                                            placeholder = "Repite la contraseña",
                                            leadingIcon = Icons.Outlined.LockOpen,
                                            trailingIcon = {
                                                IconButton(
                                                    onClick = { showConfirmPassword = !showConfirmPassword },
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
                                            enabled = !uiState.isChangingPassword,
                                            isError = uiState.confirmPassword.isNotEmpty() &&
                                                    uiState.confirmPassword != uiState.newPassword,
                                            errorMessage = if (uiState.confirmPassword.isNotEmpty() &&
                                                uiState.confirmPassword != uiState.newPassword)
                                                "Las contraseñas no coinciden" else null
                                        )

                                        Spacer(modifier = Modifier.height(24.dp))

                                        WhosInPrimaryButton(
                                            text = "Cambiar Contraseña",
                                            onClick = { viewModel.changePassword() },
                                            enabled = !uiState.isChangingPassword &&
                                                    uiState.currentPassword.isNotEmpty() &&
                                                    uiState.newPassword.length >= 6 &&
                                                    uiState.newPassword == uiState.confirmPassword,
                                            isLoading = uiState.isChangingPassword
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    name: String,
    lastname: String,
    email: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar con iniciales
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
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
                text = "${name.firstOrNull()?.uppercase() ?: ""}${lastname.firstOrNull()?.uppercase() ?: ""}",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = WhosInColors.LimeGreen
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "$name $lastname",
            style = MaterialTheme.typography.headlineSmall,
            color = WhosInColors.DarkTeal,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = email,
            style = MaterialTheme.typography.bodyMedium,
            color = WhosInColors.GrayBlue
        )
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

private data class profilePasswordStrength(
    val level: Int,
    val label: String,
    val color: androidx.compose.ui.graphics.Color
)

private fun calculatePasswordStrength(password: String): profilePasswordStrength {
    var score = 0
    if (password.length >= 6) score++
    if (password.length >= 8) score++
    if (password.any { it.isDigit() }) score++
    if (password.any { !it.isLetterOrDigit() }) score++

    return when (score) {
        0, 1 -> profilePasswordStrength(1, "Débil", WhosInColors.Error)
        2 -> profilePasswordStrength(2, "Regular", WhosInColors.Warning)
        3 -> profilePasswordStrength(3, "Buena", WhosInColors.OliveGreen)
        else -> profilePasswordStrength(4, "Excelente", WhosInColors.Success)
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
private fun ProfileDecorationCircles() {
    val infiniteTransition = rememberInfiniteTransition(label = "profileDecor")

    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset"
    )

    // Círculo superior derecho
    Box(
        modifier = Modifier
            .offset(x = (280 + offset).dp, y = (80 - offset).dp)
            .size(140.dp)
            .alpha(0.1f)
            .clip(CircleShape)
            .background(WhosInColors.LimeGreen)
    )

    // Círculo inferior izquierdo
    Box(
        modifier = Modifier
            .offset(x = (-50 - offset).dp, y = (500 + offset).dp)
            .size(100.dp)
            .alpha(0.08f)
            .clip(CircleShape)
            .background(WhosInColors.OliveGreen)
    )
}