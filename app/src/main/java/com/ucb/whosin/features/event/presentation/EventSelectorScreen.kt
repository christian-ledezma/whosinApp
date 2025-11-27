package com.ucb.whosin.features.event.presentation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.*

data class EventSummary(
    val eventId: String,
    val name: String,
    val date: Timestamp,
    val locationName: String,
    val totalInvited: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventSelectorScreen(
    onEventSelected: (String) -> Unit,
    onManageEventClicked: (String) -> Unit,
    onNavigateToCreateEvent: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf<String?>(null) }
    var lastDeletedEventId by remember { mutableStateOf<String?>(null) }
    var searchText by remember { mutableStateOf("") }

    val viewModel: EventSelectorViewModel = koinViewModel()
    val deleteResult by viewModel.deleteResult.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // lista de eventos desde el ViewModel
    val events by viewModel.events.collectAsState()

    // Al abrir la pantalla cargamos los eventos desde el UseCase
    LaunchedEffect(Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            viewModel.loadEvents(uid)
        }
    }

    // 🔥 Cuando termina de eliminar
    LaunchedEffect(deleteResult) {
        deleteResult?.let { success ->
            if (success) {
                snackbarHostState.showSnackbar("Evento eliminado")

                val uid = FirebaseAuth.getInstance().currentUser?.uid
                if (uid != null) viewModel.loadEvents(uid)

                lastDeletedEventId = null
            } else {
                snackbarHostState.showSnackbar("Error al eliminar evento")
            }
            viewModel.clearDeleteStatus()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Eventos") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF5E6FA3),
                    titleContentColor = Color.White
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreateEvent,
                containerColor = Color(0xFF5E6FA3)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Crear Evento", tint = Color.White)
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            // 🔍 Buscador
            OutlinedTextField(
                value = searchText,
                onValueChange = { value ->
                    searchText = value

                    if (value.isBlank()) {
                        val uid = FirebaseAuth.getInstance().currentUser?.uid
                        if (uid != null) viewModel.loadEvents(uid)
                    } else {
                        viewModel.searchEvents(value)
                    }
                },
                label = { Text("Buscar evento por nombre") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            // 📌 Lista de eventos
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(events) { event ->
                    EventCard(
                        event = EventSummary(
                            eventId = event.eventId,
                            name = event.name,
                            date = event.date,
                            locationName = event.locationName,
                            totalInvited = event.totalInvited
                        ),
                        onCardClick = { onEventSelected(event.eventId) },
                        onManageClick = { onManageEventClicked(event.eventId) },
                        onDeleteClick = { eventId ->
                            lastDeletedEventId = eventId
                            showDeleteDialog = eventId
                        }
                    )
                }
            }
        }
    }

    // 🔥 Modal de confirmación de eliminación
    if (showDeleteDialog != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Eliminar evento") },
            text = { Text("¿Seguro que deseas eliminar este evento y todos sus invitados?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteEvent(showDeleteDialog!!)
                        showDeleteDialog = null
                    }
                ) { Text("Eliminar", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun EventCard(
    event: EventSummary,
    onCardClick: () -> Unit,
    onManageClick: () -> Unit,
    onDeleteClick: (String) -> Unit
) {
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val dateStr = dateFormatter.format(event.date.toDate())

    val clipboard = LocalContext.current.getSystemService(Context.CLIPBOARD_SERVICE)
            as ClipboardManager

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = event.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("📅 $dateStr", color = Color(0xFF666666))
                    Text("📍 ${event.locationName}", color = Color(0xFF666666))
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF5E6FA3).copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "${event.totalInvited} invitados",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = Color(0xFF5E6FA3),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {

                TextButton(
                    onClick = {
                        val clip = ClipData.newPlainText("Event ID", event.eventId)
                        clipboard.setPrimaryClip(clip)
                    }
                ) { Text("Copiar ID") }

                TextButton(
                    onClick = { onDeleteClick(event.eventId) }
                ) { Text("Eliminar", color = Color.Red) }
            }
        }
    }
}
