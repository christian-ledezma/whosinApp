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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import java.util.Calendar

@Composable
fun RegisterEventScreen(
    viewModel: RegisterEventViewModel = koinViewModel(),
    locationViewModel: LocationViewModel,
    onRegisterSuccess: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigateToMapPicker: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Campos del formulario
    var name by rememberSaveable { mutableStateOf("") }
    var date by rememberSaveable { mutableStateOf("") } // Podrías usar un DatePicker más adelante
    var locationName by rememberSaveable { mutableStateOf("") }
    var latitude by rememberSaveable { mutableStateOf<Double?>(null) }
    var longitude by rememberSaveable { mutableStateOf<Double?>(null) }
    var capacity by rememberSaveable { mutableStateOf("") }
    var status by rememberSaveable { mutableStateOf("upcoming") }

    // Campos automáticos
    val eventId = remember { UUID.randomUUID().toString() }
    val createdAt = remember { Timestamp.now() }
    val totalCheckedIn = 0
    val totalInvited = 0
    val guardModeEnabled = true
    var showSuccessDialog by remember { mutableStateOf(false) }

    fun clearForm() {
        name = ""
        date = ""
        locationName = ""
        latitude = null
        longitude = null
        capacity = ""
        status = "upcoming"
        locationViewModel.clearLocation()
        viewModel.resetState()
    }

    // Éxito → mostrar snackbar y volver
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = "¡Evento registrado exitosamente!",
                    duration = SnackbarDuration.Short
                )
                showSuccessDialog = true
            }
        }
    }

    // Error → mostrar snackbar
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

    // Observar cambios en la ubicación seleccionada
    LaunchedEffect(Unit) {
        locationViewModel.selectedLocation.collect { location ->
            location?.let { (lat, lng) ->
                latitude = lat
                longitude = lng
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre del evento") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            val context = LocalContext.current
            val calendar = Calendar.getInstance()

            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                context,
                { _, selectedYear, selectedMonth, selectedDay ->
                    date = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                },
                year, month, day
            )

            TextField(
                value = date,
                onValueChange = { },
                label = { Text("Fecha del evento") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading,
                trailingIcon = {
                    IconButton(onClick = { datePickerDialog.show() }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = locationName,
                onValueChange = { locationName = it },
                label = { Text("Ubicación") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para seleccionar ubicación en el mapa
            OutlinedButton(
                onClick = {
                    onNavigateToMapPicker()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    if (latitude != null && longitude != null)
                        "Ubicación seleccionada ✓"
                    else
                        "Seleccionar ubicación en el mapa"
                )
            }

            if (latitude != null && longitude != null) {
                Text(
                    text = "Lat: ${"%.6f".format(latitude)}, Lng: ${"%.6f".format(longitude)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))


            TextField(
                value = capacity,
                onValueChange = { capacity = it },
                label = { Text("Capacidad") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    // Convertir la fecha seleccionada (yyyy-MM-dd) a Timestamp
                    val eventDate = try {
                        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                        val parsedDate = sdf.parse(date)
                        Timestamp(parsedDate!!)
                    } catch (e: Exception) {
                        Timestamp.now() // si algo falla, usa la fecha actual
                    }

                    val capacityInt = capacity.toIntOrNull() ?: 0

                    viewModel.registerEvent(
                        eventId = eventId,
                        name = name,
                        date = eventDate,
                        locationName = locationName,
                        latitude = latitude!!,
                        longitude = longitude!!,
                        capacity = capacityInt,
                        status = status,
                        guardModeEnabled = guardModeEnabled,
                        createdAt = createdAt,
                        totalCheckedIn = totalCheckedIn,
                        totalInvited = totalInvited
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Registrar evento")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            TextButton(
                onClick = onNavigateBack,
                enabled = !uiState.isLoading
            ) {
                Text("Volver atrás")
            }
            if (showSuccessDialog) {
                val clipboard = LocalContext.current.getSystemService(android.content.Context.CLIPBOARD_SERVICE)
                        as android.content.ClipboardManager

                AlertDialog(
                    onDismissRequest = { showSuccessDialog = false },
                    title = { Text("Evento creado") },
                    text = { Text("¿Deseas copiar el ID del evento al portapapeles?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                val clip = android.content.ClipData.newPlainText("Event ID", eventId)
                                clipboard.setPrimaryClip(clip)
                                showSuccessDialog = false
                                clearForm()
                                onRegisterSuccess()
                            }
                        ) {
                            Text("Copiar ID")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                showSuccessDialog = false
                                clearForm()
                                onRegisterSuccess()
                            }
                        ) {
                            Text("Cerrar")
                        }
                    }
                )
            }

        }
    }
}
