package com.ucb.whosin.features.event.presentation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.ucb.whosin.features.login.presentation.WhosInModernTheme
import com.ucb.whosin.ui.components.WhosInTextField
import com.ucb.whosin.ui.theme.WhosInColors
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Locale

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
    onNavigateToCreateEvent: () -> Unit,
    onNavigateBack: () -> Unit // Added for back button
) {
    var showDeleteDialog by remember { mutableStateOf<String?>(null) }
    var lastDeletedEventId by remember { mutableStateOf<String?>(null) }
    var searchText by remember { mutableStateOf("") }

    val viewModel: EventSelectorViewModel = koinViewModel()
    val deleteResult by viewModel.deleteResult.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val events by viewModel.events.collectAsState()

    LaunchedEffect(Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            viewModel.loadEvents(uid)
        }
    }

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

    WhosInModernTheme {
        Scaffold(
            containerColor = WhosInColors.DarkTeal,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onNavigateToCreateEvent,
                    containerColor = WhosInColors.LimeGreen,
                    contentColor = WhosInColors.DarkTeal
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Crear Evento")
                }
            }
        ) { paddingValues ->

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Background decorations
                EventSelectorDecorationCircles()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
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
                            text = "Mis Eventos",
                            style = MaterialTheme.typography.headlineMedium,
                            color = WhosInColors.LightGray,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Search
                    Column(Modifier.padding(horizontal = 24.dp)) {
                        WhosInTextField(
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
                            label = "Buscar evento",
                            placeholder = "Buscar por nombre...",
                            leadingIcon = Icons.Outlined.Search
                        )
                    }


                    Spacer(modifier = Modifier.height(16.dp))

                    // Event List
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
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
                                onDeleteClick = { eventId ->
                                    lastDeletedEventId = eventId
                                    showDeleteDialog = eventId
                                }
                            )
                        }
                    }
                }
            }
        }

        // Deletion confirmation dialog
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
                    ) { Text("Eliminar", color = WhosInColors.Error) }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = null }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
fun EventCard(
    event: EventSummary,
    onCardClick: () -> Unit,
    onDeleteClick: (String) -> Unit
) {
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val dateStr = dateFormatter.format(event.date.toDate())

    val clipboard = LocalContext.current.getSystemService(Context.CLIPBOARD_SERVICE)
            as ClipboardManager

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick),
        color = WhosInColors.White,
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = event.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = WhosInColors.DarkTeal
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "📅 $dateStr",
                        color = WhosInColors.GrayBlue,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "📍 ${event.locationName}",
                        color = WhosInColors.GrayBlue,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = WhosInColors.PetrolBlue.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "${event.totalInvited} invitados",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        color = WhosInColors.PetrolBlue,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {

                TextButton(
                    onClick = {
                        val clip = ClipData.newPlainText("Event ID", event.eventId)
                        clipboard.setPrimaryClip(clip)
                    }
                ) { Text("Copiar ID", color = WhosInColors.GrayBlue) }

                Spacer(modifier = Modifier.width(8.dp))

                TextButton(
                    onClick = { onDeleteClick(event.eventId) }
                ) { Text("Eliminar", color = WhosInColors.Error) }
            }
        }
    }
}

@Composable
private fun EventSelectorDecorationCircles() {
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
