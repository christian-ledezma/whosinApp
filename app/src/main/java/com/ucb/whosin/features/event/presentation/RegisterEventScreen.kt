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
import androidx.compose.ui.platform.LocalContext
import java.util.Calendar

@Composable
fun RegisterEventScreen(
    viewModel: RegisterEventViewModel = koinViewModel(),
    onRegisterSuccess: () -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Campos del formulario
    var name by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") } // Podrías usar un DatePicker más adelante
    var locationName by remember { mutableStateOf("") }
    var capacity by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("upcoming") }
    var totalInvited by remember { mutableStateOf("0") }

    // Campos automáticos
    val eventId = remember { UUID.randomUUID().toString() }
    val createdAt = remember { Timestamp.now() }
    val totalCheckedIn = 0
    val guardModeEnabled = true

    // Éxito → mostrar snackbar y volver
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = "¡Evento registrado exitosamente!",
                    duration = SnackbarDuration.Short
                )
                delay(1000)
                onRegisterSuccess()
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

            TextField(
                value = capacity,
                onValueChange = { capacity = it },
                label = { Text("Capacidad") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = totalInvited,
                onValueChange = { totalInvited = it },
                label = { Text("Invitados totales") },
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
                    val totalInvitedInt = totalInvited.toIntOrNull() ?: 0

                    viewModel.registerEvent(
                        eventId = eventId,
                        name = name,
                        date = eventDate, // 👈 ahora se envía correctamente
                        locationName = locationName,
                        capacity = capacityInt,
                        status = status,
                        guardModeEnabled = guardModeEnabled,
                        createdAt = createdAt,
                        totalCheckedIn = totalCheckedIn,
                        totalInvited = totalInvitedInt
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
        }
    }
}
