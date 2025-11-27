package com.ucb.whosin.features.event.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.util.UUID
import android.app.DatePickerDialog
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Title
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ucb.whosin.features.login.presentation.AnimatedEntrance
import com.ucb.whosin.features.login.presentation.WhosInModernTheme
import com.ucb.whosin.ui.components.WhosInPrimaryButton
import com.ucb.whosin.ui.components.WhosInTextField
import com.ucb.whosin.ui.theme.WhosInColors
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun RegisterEventScreen(
    viewModel: RegisterEventViewModel = koinViewModel(),
    locationViewModel: LocationViewModel,
    onRegisterSuccess: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigateToMapPicker: () -> Unit = {}
) {
    WhosInModernTheme {
        RegisterEventScreenContent(
            viewModel = viewModel,
            locationViewModel = locationViewModel,
            onRegisterSuccess = onRegisterSuccess,
            onNavigateBack = onNavigateBack,
            onNavigateToMapPicker = onNavigateToMapPicker
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegisterEventScreenContent(
    viewModel: RegisterEventViewModel,
    locationViewModel: LocationViewModel,
    onRegisterSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToMapPicker: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Campos del formulario
    var name by rememberSaveable { mutableStateOf("") }
    var date by rememberSaveable { mutableStateOf("") }
    var locationName by rememberSaveable { mutableStateOf("") }
    var latitude by rememberSaveable { mutableStateOf<Double?>(null) }
    var longitude by rememberSaveable { mutableStateOf<Double?>(null) }
    var capacity by rememberSaveable { mutableStateOf("") }
    var status by rememberSaveable { mutableStateOf("upcoming") }

    // Estados UI
    var startAnimation by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var generatedEventId by remember { mutableStateOf("") }

    // Validaciones
    var nameError by remember { mutableStateOf<String?>(null) }
    var dateError by remember { mutableStateOf<String?>(null) }
    var locationError by remember { mutableStateOf<String?>(null) }
    var capacityError by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        delay(100)
        startAnimation = true
    }

    fun clearForm() {
        name = ""
        date = ""
        locationName = ""
        latitude = null
        longitude = null
        capacity = ""
        status = "upcoming"
        nameError = null
        dateError = null
        locationError = null
        capacityError = null
        locationViewModel.clearLocation()
        viewModel.resetState()
    }

    fun validateForm(): Boolean {
        var isValid = true

        if (name.isBlank()) {
            nameError = "El nombre es obligatorio"
            isValid = false
        } else {
            nameError = null
        }

        if (date.isBlank()) {
            dateError = "Selecciona una fecha"
            isValid = false
        } else {
            dateError = null
        }

        if (locationName.isBlank()) {
            locationError = "El nombre de ubicación es obligatorio"
            isValid = false
        } else {
            locationError = null
        }

        if (latitude == null || longitude == null) {
            locationError = "Debes seleccionar la ubicación en el mapa"
            isValid = false
        }

        if (capacity.isBlank() || capacity.toIntOrNull() == null || capacity.toInt() <= 0) {
            capacityError = "Capacidad inválida"
            isValid = false
        } else {
            capacityError = null
        }

        return isValid
    }

    // Éxito
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            showSuccessDialog = true
        }
    }

    // Error
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { error ->
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = error,
                    duration = SnackbarDuration.Long
                )
                viewModel.clearError()
            }
        }
    }

    // Observar ubicación seleccionada
    LaunchedEffect(Unit) {
        locationViewModel.selectedLocation.collect { location ->
            location?.let { (lat, lng) ->
                latitude = lat
                longitude = lng
            }
        }
    }

    // Diálogo de éxito
    if (showSuccessDialog) {
        EventSuccessDialog(
            eventId = generatedEventId,
            eventName = name,
            onDismiss = {
                showSuccessDialog = false
                clearForm()
                onRegisterSuccess()
            }
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
            RegisterEventDecorationCircles()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
                    .statusBarsPadding()
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Header
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
                            text = "Crear Evento",
                            style = MaterialTheme.typography.headlineMedium,
                            color = WhosInColors.LightGray,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Icono de evento
                AnimatedEntrance(visible = startAnimation, delayMillis = 100) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        WhosInColors.DarkTeal,
                                        WhosInColors.PetrolBlue
                                    )
                                )
                            )
                            .align(Alignment.CenterHorizontally),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Event,
                            contentDescription = null,
                            tint = WhosInColors.LimeGreen,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Card del formulario
                AnimatedEntrance(visible = startAnimation, delayMillis = 200) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        color = WhosInColors.White,
                        shadowElevation = 8.dp
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            // Nombre del evento
                            WhosInTextField(
                                value = name,
                                onValueChange = {
                                    name = it
                                    nameError = null
                                },
                                label = "Nombre del evento",
                                placeholder = "Ej: Graduación Semestre",
                                leadingIcon = Icons.Outlined.Title,
                                enabled = !uiState.isLoading,
                                isError = nameError != null,
                                errorMessage = nameError
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Fecha
                            DatePickerField(
                                value = date,
                                onDateSelected = { selectedDate ->
                                    date = selectedDate
                                    dateError = null
                                },
                                context = context,
                                enabled = !uiState.isLoading,
                                isError = dateError != null,
                                errorMessage = dateError
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Nombre de ubicación
                            WhosInTextField(
                                value = locationName,
                                onValueChange = {
                                    locationName = it
                                    locationError = null
                                },
                                label = "Nombre de la ubicación",
                                placeholder = "Ej: Salón Principal",
                                leadingIcon = Icons.Outlined.Place,
                                enabled = !uiState.isLoading,
                                isError = locationError != null,
                                errorMessage = locationError
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Botón seleccionar en mapa
                            MapPickerButton(
                                hasLocation = latitude != null && longitude != null,
                                latitude = latitude,
                                longitude = longitude,
                                onClick = onNavigateToMapPicker,
                                enabled = !uiState.isLoading
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Capacidad
                            WhosInTextField(
                                value = capacity,
                                onValueChange = {
                                    if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                                        capacity = it
                                        capacityError = null
                                    }
                                },
                                label = "Capacidad",
                                placeholder = "Número de invitados",
                                leadingIcon = Icons.Outlined.People,
                                enabled = !uiState.isLoading,
                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                    keyboardType = KeyboardType.Number
                                ),
                                isError = capacityError != null,
                                errorMessage = capacityError
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // Botón crear
                            WhosInPrimaryButton(
                                text = "Crear Evento",
                                onClick = {
                                    if (validateForm()) {
                                        val eventId = UUID.randomUUID().toString()
                                        generatedEventId = eventId

                                        val eventDate = try {
                                            val sdf =
                                                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                            val parsedDate = sdf.parse(date)
                                            Timestamp(parsedDate!!)
                                        } catch (e: Exception) {
                                            Timestamp.now()
                                        }

                                        viewModel.registerEvent(
                                            eventId = eventId,
                                            name = name,
                                            date = eventDate,
                                            locationName = locationName,
                                            latitude = latitude!!,
                                            longitude = longitude!!,
                                            capacity = capacity.toInt(),
                                            status = status,
                                            guardModeEnabled = true,
                                            createdAt = Timestamp.now(),
                                            totalCheckedIn = 0,
                                            totalInvited = 0
                                        )
                                    }
                                },
                                enabled = !uiState.isLoading,
                                isLoading = uiState.isLoading
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
private fun DatePickerField(
    value: String,
    onDateSelected: (String) -> Unit,
    context: android.content.Context,
    enabled: Boolean,
    isError: Boolean,
    errorMessage: String?
) {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, selectedDay ->
                onDateSelected(
                    String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                )
            },
            year, month, day
        )
    }

    WhosInTextField(
        value = value.ifEmpty { "Seleccionar fecha" },
        onValueChange = {},
        label = "Fecha del evento",
        placeholder = "YYYY-MM-DD",
        leadingIcon = Icons.Outlined.CalendarToday,
        trailingIcon = {
            IconButton(
                onClick = { if (enabled) datePickerDialog.show() },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.DateRange,
                    contentDescription = "Seleccionar fecha",
                    tint = WhosInColors.GrayBlue
                )
            }
        },
        enabled = false, // Siempre deshabilitado para evitar teclado
        modifier = Modifier.clickable(enabled = enabled) {
            datePickerDialog.show()
        },
        isError = isError,
        errorMessage = errorMessage
    )
}

@Composable
private fun MapPickerButton(
    hasLocation: Boolean,
    latitude: Double?,
    longitude: Double?,
    onClick: () -> Unit,
    enabled: Boolean
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = enabled, onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = if (hasLocation) WhosInColors.MintGreen.copy(alpha = 0.2f) else WhosInColors.LightGray,
        border = ButtonDefaults.outlinedButtonBorder.copy(
            width = 1.dp,
            brush = Brush.linearGradient(
                colors = if (hasLocation)
                    listOf(WhosInColors.OliveGreen, WhosInColors.OliveGreen)
                else
                    listOf(WhosInColors.GrayBlue.copy(alpha = 0.3f), WhosInColors.GrayBlue.copy(alpha = 0.3f))
            )
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (hasLocation) WhosInColors.OliveGreen else WhosInColors.GrayBlue.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (hasLocation) Icons.Rounded.CheckCircle else Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = if (hasLocation) WhosInColors.White else WhosInColors.GrayBlue,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (hasLocation) "Ubicación seleccionada" else "Seleccionar en el mapa",
                    style = MaterialTheme.typography.bodyLarge,
                    color = WhosInColors.DarkTeal,
                    fontWeight = if (hasLocation) FontWeight.SemiBold else FontWeight.Normal
                )

                if (hasLocation && latitude != null && longitude != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Lat: ${"%.4f".format(latitude)}, Lng: ${"%.4f".format(longitude)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = WhosInColors.GrayBlue
                    )
                }
            }

            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = WhosInColors.GrayBlue
            )
        }
    }
}

@Composable
private fun EventSuccessDialog(
    eventId: String,
    eventName: String,
    onDismiss: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(28.dp),
            color = WhosInColors.White
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icono de éxito animado
                SuccessAnimatedIcon()

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "¡Evento Creado!",
                    style = MaterialTheme.typography.headlineSmall,
                    color = WhosInColors.DarkTeal,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = eventName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = WhosInColors.GrayBlue,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Card con el ID
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = WhosInColors.LightGray,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "ID del Evento",
                            style = MaterialTheme.typography.labelMedium,
                            color = WhosInColors.GrayBlue
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = eventId.take(16) + "...",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = WhosInColors.DarkTeal,
                                modifier = Modifier.weight(1f)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            // Botón copiar
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(eventId))
                                    copied = true
                                    scope.launch {
                                        delay(2000)
                                        copied = false
                                    }
                                },
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (copied) WhosInColors.Success.copy(alpha = 0.2f)
                                        else WhosInColors.LimeGreen
                                    )
                            ) {
                                Icon(
                                    imageVector = if (copied) Icons.Rounded.Check else Icons.Outlined.ContentCopy,
                                    contentDescription = "Copiar ID",
                                    tint = if (copied) WhosInColors.Success else WhosInColors.DarkTeal,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        if (copied) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "✓ ID copiado al portapapeles",
                                style = MaterialTheme.typography.bodySmall,
                                color = WhosInColors.Success
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Comparte este ID con tus invitados para que puedan unirse al evento.",
                    style = MaterialTheme.typography.bodySmall,
                    color = WhosInColors.GrayBlue,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                WhosInPrimaryButton(
                    text = "Entendido",
                    onClick = onDismiss
                )
            }
        }
    }
}

@Composable
private fun SuccessAnimatedIcon() {
    val infiniteTransition = rememberInfiniteTransition(label = "success")

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "successScale"
    )

    Box(
        modifier = Modifier
            .size(80.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(WhosInColors.Success.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.CheckCircle,
            contentDescription = null,
            tint = WhosInColors.Success,
            modifier = Modifier.size(48.dp)
        )
    }
}

@Composable
private fun RegisterEventDecorationCircles() {
    val infiniteTransition = rememberInfiniteTransition(label = "eventDecor")

    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset"
    )

    Box(
        modifier = Modifier
            .offset(x = (290 + offset).dp, y = (70 - offset).dp)
            .size(130.dp)
            .alpha(0.1f)
            .clip(CircleShape)
            .background(WhosInColors.LimeGreen)
    )

    Box(
        modifier = Modifier
            .offset(x = (-40 - offset).dp, y = (450 + offset).dp)
            .size(110.dp)
            .alpha(0.08f)
            .clip(CircleShape)
            .background(WhosInColors.OliveGreen)
    )
}