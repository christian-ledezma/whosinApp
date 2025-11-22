package com.ucb.whosin.features.Guest.presentation

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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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
    var events by remember { mutableStateOf<List<EventSummary>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val firestore = FirebaseFirestore.getInstance()
    val firebaseAuth = FirebaseAuth.getInstance()

    LaunchedEffect(Unit) {
        try {
            val currentUser = firebaseAuth.currentUser
            if (currentUser == null) {
                errorMessage = "No hay usuario autenticado"
                isLoading = false
                return@LaunchedEffect
            }

            val snapshot = firestore.collection("events")
                .whereEqualTo("userId", currentUser.uid)
                .get()
                .await()

            events = snapshot.documents.mapNotNull { doc ->
                try {
                    EventSummary(
                        eventId = doc.id,
                        name = doc.getString("name") ?: "Sin nombre",
                        date = doc.getTimestamp("date") ?: Timestamp.now(),
                        locationName = doc.getString("locationName") ?: "Sin ubicación",
                        totalInvited = doc.getLong("totalInvited")?.toInt() ?: 0
                    )
                } catch (e: Exception) {
                    null
                }
            }

            isLoading = false
        } catch (e: Exception) {
            errorMessage = "Error al cargar eventos: ${e.message}"
            isLoading = false
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreateEvent,
                containerColor = Color(0xFF5E6FA3)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Crear Evento", tint = Color.White)
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF5E6FA3)
                    )
                }
                errorMessage != null -> {
                    Text(
                        text = errorMessage ?: "",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        color = MaterialTheme.colorScheme.error
                    )
                }
                events.isEmpty() -> {
                    Text(
                        text = "No tienes eventos creados.\nUsa el botón ‘+’ para crear uno.",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        color = Color.Gray
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(events) { event ->
                            EventCard(
                                event = event,
                                onCardClick = { onEventSelected(event.eventId) },
                                onManageClick = { onManageEventClicked(event.eventId) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EventCard(
    event: EventSummary,
    onCardClick: () -> Unit,
    onManageClick: () -> Unit
) {
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val dateStr = dateFormatter.format(event.date.toDate())

    val clipboard = LocalContext.current.getSystemService(android.content.Context.CLIPBOARD_SERVICE)
            as android.content.ClipboardManager

    var showCopiedSnackbar by remember { mutableStateOf(false) }

    if (showCopiedSnackbar) {
        SnackbarHost(
            hostState = remember { SnackbarHostState() }.apply {
                CoroutineScope(Dispatchers.Main).launch {
                    showSnackbar("ID copiado al portapapeles")
                    showCopiedSnackbar = false
                }
            }
        )
    }

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
                    Text(
                        text = "📅 $dateStr",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF666666)
                    )
                    Text(
                        text = "📍 ${event.locationName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF666666)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF5E6FA3).copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "${event.totalInvited} invitados",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
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

                // 🔹 Botón Copiar ID
                TextButton(
                    onClick = {
                        val clip = android.content.ClipData.newPlainText("Event ID", event.eventId)
                        clipboard.setPrimaryClip(clip)
                        showCopiedSnackbar = true
                    }
                ) {
                    Text("Copiar ID")
                }

                Spacer(modifier = Modifier.width(8.dp))

                // 🔹 Botón Modo Guardia
                Button(
                    onClick = onManageClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5E6FA3))
                ) {
                    Text("Modo Guardia")
                }
            }
        }
    }
}
