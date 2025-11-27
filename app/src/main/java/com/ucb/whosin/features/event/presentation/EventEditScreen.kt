package com.ucb.whosin.features.event.presentation

import android.app.DatePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import com.ucb.whosin.features.event.domain.model.AssignedGuard
import com.ucb.whosin.features.login.presentation.AnimatedEntrance
import com.ucb.whosin.features.login.presentation.WhosInModernTheme
import com.ucb.whosin.ui.components.WhosInPrimaryButton
import com.ucb.whosin.ui.components.WhosInTextField
import com.ucb.whosin.ui.theme.WhosInColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventEditScreen(
    viewModel: EventEditViewModel = koinViewModel(),
    locationViewModel: LocationViewModel,
    onNavigateBack: () -> Unit = {},
    onNavigateToMapPicker: (String) -> Unit = {}
) {
    WhosInModernTheme {
        EventEditScreenContent(
            viewModel = viewModel,
            locationViewModel = locationViewModel,
            onNavigateBack = onNavigateBack,
            onNavigateToMapPicker = onNavigateToMapPicker
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EventEditScreenContent(
    viewModel: EventEditViewModel,
    locationViewModel: LocationViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToMapPicker: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var startAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        startAnimation = true
    }

    // Mensajes
    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let { message ->
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Short
                )
                viewModel.clearMessages()
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
                viewModel.clearMessages()
            }
        }
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
            // Decoraciones
            EditEventDecorationCircles()

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = WhosInColors.LimeGreen,
                        strokeWidth = 3.dp
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
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
                                text = "Editar Evento",
                                style = MaterialTheme.typography.headlineMedium,
                                color = WhosInColors.LightGray,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Tabs
                    AnimatedEntrance(visible = startAnimation, delayMillis = 100) {
                        TabSelector(
                            selectedTab = uiState.selectedTab,
                            onTabSelected = { viewModel.selectTab(it) }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Contenido según tab
                    when (uiState.selectedTab) {
                        0 -> EventDataTab(
                            event = uiState.event,
                            viewModel = viewModel,
                            locationViewModel = locationViewModel,
                            onNavigateToMapPicker = onNavigateToMapPicker,
                            isSaving = uiState.isSaving,
                            startAnimation = startAnimation
                        )
                        1 -> GuardsTab(
                            guards = uiState.guards,
                            viewModel = viewModel,
                            isAddingGuard = uiState.isAddingGuard,
                            startAnimation = startAnimation
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TabSelector(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TabButton(
            text = "Datos del Evento",
            icon = Icons.Outlined.Edit,
            isSelected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            modifier = Modifier.weight(1f)
        )

        TabButton(
            text = "Guardias",
            icon = Icons.Outlined.Security,
            isSelected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun TabButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        color = if (isSelected) WhosInColors.White else WhosInColors.White.copy(alpha = 0.3f),
        shadowElevation = if (isSelected) 8.dp else 0.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) WhosInColors.DarkTeal else WhosInColors.LightGray,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isSelected) WhosInColors.DarkTeal else WhosInColors.LightGray,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}

@Composable
private fun EventDataTab(
    event: com.ucb.whosin.features.event.domain.model.EventModel?,
    viewModel: EventEditViewModel,
    locationViewModel: LocationViewModel,
    onNavigateToMapPicker: (String) -> Unit,
    isSaving: Boolean,
    startAnimation: Boolean
) {
    var name by rememberSaveable { mutableStateOf(event?.name ?: "") }
    var date by rememberSaveable { mutableStateOf("") }
    var locationName by rememberSaveable { mutableStateOf(event?.locationName ?: "") }
    var latitude by rememberSaveable { mutableStateOf(event?.latitude) }
    var longitude by rememberSaveable { mutableStateOf(event?.longitude) }
    var capacity by rememberSaveable { mutableStateOf(event?.capacity?.toString() ?: "") }

    val context = LocalContext.current

    // Inicializar valores
    LaunchedEffect(event) {
        event?.let {
            name = it.name
            locationName = it.locationName
            capacity = it.capacity.toString()
            latitude = it.latitude
            longitude = it.longitude

            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            date = sdf.format(it.date.toDate())
        }
    }

    // Observar cambios de ubicación
    LaunchedEffect(Unit) {
        locationViewModel.selectedLocation.collect { location ->
            location?.let { (lat, lng) ->
                latitude = lat
                longitude = lng
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        AnimatedEntrance(visible = startAnimation, delayMillis = 200) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = WhosInColors.White,
                shadowElevation = 8.dp
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    // Nombre
                    WhosInTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = "Nombre del evento",
                        placeholder = "Ej: Graduación 2024",
                        leadingIcon = Icons.Outlined.Title,
                        enabled = !isSaving
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Fecha
                    DatePickerField(
                        value = date,
                        onDateSelected = { date = it },
                        context = context,
                        enabled = !isSaving
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Ubicación
                    WhosInTextField(
                        value = locationName,
                        onValueChange = { locationName = it },
                        label = "Nombre de la ubicación",
                        placeholder = "Ej: Salón Principal",
                        leadingIcon = Icons.Outlined.Place,
                        enabled = !isSaving
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botón mapa
                    MapPickerButton(
                        hasLocation = latitude != null && longitude != null,
                        latitude = latitude,
                        longitude = longitude,
                        onClick = {
                            viewModel.uiState.value.event?.let {
                                onNavigateToMapPicker(viewModel.eventId)
                            }
                        },
                        enabled = !isSaving
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Capacidad
                    WhosInTextField(
                        value = capacity,
                        onValueChange = {
                            if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                                capacity = it
                            }
                        },
                        label = "Capacidad",
                        placeholder = "Número de invitados",
                        leadingIcon = Icons.Outlined.People,
                        enabled = !isSaving,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Botón guardar
                    WhosInPrimaryButton(
                        text = "Guardar Cambios",
                        onClick = {
                            if (latitude != null && longitude != null) {
                                val eventDate = try {
                                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                    val parsedDate = sdf.parse(date)
                                    Timestamp(parsedDate!!)
                                } catch (e: Exception) {
                                    Timestamp.now()
                                }

                                viewModel.updateEvent(
                                    name = name,
                                    date = eventDate,
                                    locationName = locationName,
                                    latitude = latitude!!,
                                    longitude = longitude!!,
                                    capacity = capacity.toIntOrNull() ?: 0
                                )
                            }
                        },
                        enabled = !isSaving && name.isNotBlank() && date.isNotBlank() &&
                                locationName.isNotBlank() && latitude != null &&
                                longitude != null && capacity.isNotBlank(),
                        isLoading = isSaving
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun GuardsTab(
    guards: List<AssignedGuard>,
    viewModel: EventEditViewModel,
    isAddingGuard: Boolean,
    startAnimation: Boolean
) {
    var guardEmail by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf<AssignedGuard?>(null) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Card para agregar guardia
        AnimatedEntrance(visible = startAnimation, delayMillis = 200) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = WhosInColors.White,
                shadowElevation = 8.dp
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.PersonAdd,
                            contentDescription = null,
                            tint = WhosInColors.OliveGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Agregar Guardia",
                            style = MaterialTheme.typography.titleMedium,
                            color = WhosInColors.DarkTeal,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    WhosInTextField(
                        value = guardEmail,
                        onValueChange = { guardEmail = it },
                        label = "Email del usuario",
                        placeholder = "usuario@email.com",
                        leadingIcon = Icons.Outlined.Email,
                        enabled = !isAddingGuard,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = KeyboardType.Email
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    WhosInPrimaryButton(
                        text = "Agregar",
                        onClick = {
                            viewModel.addGuard(guardEmail)
                            guardEmail = ""
                        },
                        enabled = guardEmail.isNotBlank() && !isAddingGuard,
                        isLoading = isAddingGuard
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Lista de guardias
        if (guards.isEmpty()) {
            AnimatedEntrance(visible = startAnimation, delayMillis = 300) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = WhosInColors.White.copy(alpha = 0.5f)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Security,
                            contentDescription = null,
                            tint = WhosInColors.GrayBlue.copy(alpha = 0.5f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No hay guardias asignados",
                            style = MaterialTheme.typography.bodyLarge,
                            color = WhosInColors.GrayBlue,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            AnimatedEntrance(visible = startAnimation, delayMillis = 300) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = WhosInColors.White,
                    shadowElevation = 8.dp
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 400.dp)
                            .padding(vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(guards) { guard ->
                            GuardListItem(
                                guard = guard,
                                onDelete = { showDeleteDialog = guard }
                            )
                        }
                    }
                }
            }
        }
    }

    // Diálogo de confirmación
    if (showDeleteDialog != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = null,
                    tint = WhosInColors.Error,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    "¿Remover guardia?",
                    fontWeight = FontWeight.Bold,
                    color = WhosInColors.DarkTeal
                )
            },
            text = {
                Text(
                    "¿Estás seguro de remover a ${showDeleteDialog!!.fullName} como guardia?",
                    color = WhosInColors.GrayBlue
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.removeGuard(showDeleteDialog!!.guardId)
                        showDeleteDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WhosInColors.Error
                    )
                ) {
                    Text("Remover")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancelar", color = WhosInColors.GrayBlue)
                }
            }
        )
    }
}

@Composable
private fun GuardListItem(
    guard: AssignedGuard,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        color = WhosInColors.LightGray
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(WhosInColors.DarkTeal),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = guard.fullName.firstOrNull()?.uppercase() ?: "G",
                    style = MaterialTheme.typography.titleLarge,
                    color = WhosInColors.LimeGreen,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = guard.fullName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = WhosInColors.DarkTeal,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = guard.email,
                    style = MaterialTheme.typography.bodySmall,
                    color = WhosInColors.GrayBlue
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Eliminar",
                    tint = WhosInColors.Error
                )
            }
        }
    }
}

@Composable
private fun DatePickerField(
    value: String,
    onDateSelected: (String) -> Unit,
    context: android.content.Context,
    enabled: Boolean
) {
    val calendar = Calendar.getInstance()

    // Si hay un valor, parsearlo
    if (value.isNotBlank()) {
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            calendar.time = sdf.parse(value)!!
        } catch (e: Exception) {
            // Usar fecha actual si hay error
        }
    }

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
        ).apply {
            // No permitir fechas pasadas o de hoy
            datePicker.minDate = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        }
    }

    WhosInTextField(
        value = if (value.isBlank()) "Seleccionar fecha" else value,
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
        enabled = false,
        modifier = Modifier.clickable(enabled = enabled) {
            datePickerDialog.show()
        }
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
private fun EditEventDecorationCircles() {
    val infiniteTransition = rememberInfiniteTransition(label = "editDecor")

    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset"
    )

    Box(
        modifier = Modifier
            .offset(x = (290 + offset).dp, y = (80 - offset).dp)
            .size(140.dp)
            .alpha(0.1f)
            .clip(CircleShape)
            .background(WhosInColors.LimeGreen)
    )

    Box(
        modifier = Modifier
            .offset(x = (-50 - offset).dp, y = (500 + offset).dp)
            .size(110.dp)
            .alpha(0.08f)
            .clip(CircleShape)
            .background(WhosInColors.OliveGreen)
    )
}