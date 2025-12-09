package com.ucb.whosin.features.event.presentation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.ucb.whosin.features.login.presentation.WhosInModernTheme
import com.ucb.whosin.ui.animations.AnimatedEntrance
import com.ucb.whosin.ui.animations.StaggeredAnimatedItem
import com.ucb.whosin.ui.components.WhosInPrimaryButton
import com.ucb.whosin.ui.components.WhosInSecondaryButton
import com.ucb.whosin.ui.components.WhosInTextField
import com.ucb.whosin.ui.theme.WhosInColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.*

data class EventSummary(
    val eventId: String,
    val name: String,
    val date: Timestamp,
    val locationName: String,
    val totalInvited: Int,
    val calculatedTotalInvited: Int = 0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventSelectorScreen(
    onEventSelected: (String) -> Unit,
    onManageEventClicked: (String) -> Unit,
    onNavigateToCreateEvent: () -> Unit,
    onEditEventClicked: (String) -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    WhosInModernTheme {
        EventSelectorScreenContent(
            onEventSelected = onEventSelected,
            onManageEventClicked = onManageEventClicked,
            onNavigateToCreateEvent = onNavigateToCreateEvent,
            onEditEventClicked = onEditEventClicked,
            onNavigateBack = onNavigateBack
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EventSelectorScreenContent(
    onEventSelected: (String) -> Unit,
    onManageEventClicked: (String) -> Unit,
    onNavigateToCreateEvent: () -> Unit,
    onEditEventClicked: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val viewModel: EventSelectorViewModel = koinViewModel()
    val events by viewModel.events.collectAsState()
    val deleteResult by viewModel.deleteResult.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showDeleteDialog by remember { mutableStateOf<String?>(null) }
    var searchText by remember { mutableStateOf("") }
    var startAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        startAnimation = true

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            viewModel.loadEvents(uid)
        }
    }

    LaunchedEffect(deleteResult) {
        deleteResult?.let { success ->
            scope.launch {
                if (success) {
                    snackbarHostState.showSnackbar("Evento eliminado correctamente")
                    val uid = FirebaseAuth.getInstance().currentUser?.uid
                    if (uid != null) viewModel.loadEvents(uid)
                } else {
                    snackbarHostState.showSnackbar("Error al eliminar el evento")
                }
                viewModel.clearDeleteStatus()
            }
        }
    }

    // Diálogo de confirmación de eliminación
    if (showDeleteDialog != null) {
        DeleteConfirmationDialog(
            eventName = events.find { it.eventId == showDeleteDialog }?.name ?: "",
            onConfirm = {
                viewModel.deleteEvent(showDeleteDialog!!)
                showDeleteDialog = null
            },
            onDismiss = { showDeleteDialog = null }
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
            EventSelectorDecorationCircles()

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
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
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

                            Column {
                                Text(
                                    text = "Mis Eventos",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = WhosInColors.LightGray,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${events.size} evento${if (events.size != 1) "s" else ""}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = WhosInColors.GrayBlue
                                )
                            }
                        }

                        // Botón crear evento (arriba a la derecha)
                        FloatingActionButton(
                            onClick = onNavigateToCreateEvent,
                            containerColor = WhosInColors.LimeGreen,
                            contentColor = WhosInColors.DarkTeal,
                            modifier = Modifier.size(56.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = "Crear evento",
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Buscador
                AnimatedEntrance(visible = startAnimation, delayMillis = 100) {
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
                        placeholder = "Buscar eventos...",
                        leadingIcon = Icons.Outlined.Search,
                        trailingIcon = if (searchText.isNotEmpty()) {
                            {
                                IconButton(
                                    onClick = {
                                        searchText = ""
                                        val uid = FirebaseAuth.getInstance().currentUser?.uid
                                        if (uid != null) viewModel.loadEvents(uid)
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Close,
                                        contentDescription = "Limpiar búsqueda",
                                        tint = WhosInColors.GrayBlue
                                    )
                                }
                            }
                        } else null
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Lista de eventos
                if (events.isEmpty()) {
                    AnimatedEntrance(visible = startAnimation, delayMillis = 200) {
                        EmptyEventsState(onCreateClick = onNavigateToCreateEvent)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        items(events.size) { index ->
                            val event = events[index]
                            StaggeredAnimatedItem(index = index, baseDelayMillis = 50) {
                                ModernEventCard(
                                    event = EventSummary(
                                        eventId = event.eventId,
                                        name = event.name,
                                        date = event.date,
                                        locationName = event.locationName,
                                        totalInvited = event.totalInvited,
                                        calculatedTotalInvited = event.calculatedTotalInvited
                                    ),
                                    onCardClick = { onEventSelected(event.eventId) },
                                    onManageClick = { onManageEventClicked(event.eventId) },
                                    onDeleteClick = { showDeleteDialog = event.eventId },
                                    onEditClick = { onEditEventClicked(event.eventId) }
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
private fun ModernEventCard(
    event: EventSummary,
    onCardClick: () -> Unit,
    onManageClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit
) {
    val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale("es", "ES"))
    val dateStr = dateFormatter.format(event.date.toDate())

    val clipboard = LocalContext.current.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    var showMenu by remember { mutableStateOf(false) }
    var showCopySuccess by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick),
        shape = RoundedCornerShape(20.dp),
        color = WhosInColors.White,
        shadowElevation = 6.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header con nombre y menú
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = event.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = WhosInColors.DarkTeal,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Menú de opciones
                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(WhosInColors.LightGray)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.MoreVert,
                            contentDescription = "Más opciones",
                            tint = WhosInColors.DarkTeal,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier.background(WhosInColors.White)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Editar evento") },
                            onClick = {
                                showMenu = false
                                onEditClick()
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.Edit,
                                    contentDescription = null,
                                    tint = WhosInColors.DarkTeal
                                )
                            }
                        )
                        HorizontalDivider(color = WhosInColors.GrayBlue.copy(alpha = 0.2f))
                        DropdownMenuItem(
                            text = { Text("Eliminar", color = WhosInColors.Error) },
                            onClick = {
                                showMenu = false
                                onDeleteClick()
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.Delete,
                                    contentDescription = null,
                                    tint = WhosInColors.Error
                                )
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(9.dp))

            // Información del evento
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Fecha
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(WhosInColors.PetrolBlue.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CalendarToday,
                            contentDescription = null,
                            tint = WhosInColors.PetrolBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = dateStr,
                        style = MaterialTheme.typography.bodyMedium,
                        color = WhosInColors.GrayBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Ubicación
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(WhosInColors.MintGreen.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = WhosInColors.OliveGreen,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = event.locationName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = WhosInColors.GrayBlue,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(color = WhosInColors.GrayBlue.copy(alpha = 0.2f))

            Spacer(modifier = Modifier.height(12.dp))

            // Botón copiar ID
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Invitados badge
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = WhosInColors.LimeGreen.copy(alpha = 0.2f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.People,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = WhosInColors.OliveGreen
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${event.calculatedTotalInvited}",
                            fontSize = 16.sp,
                            style = MaterialTheme.typography.labelMedium,
                            color = WhosInColors.OliveGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedButton(
                    onClick = {
                        val clip = ClipData.newPlainText("Event ID", event.eventId)
                        clipboard.setPrimaryClip(clip)
                        showCopySuccess = true
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (showCopySuccess)
                            WhosInColors.Success.copy(alpha = 0.15f)
                        else
                            WhosInColors.White,
                        contentColor = if (showCopySuccess)
                            WhosInColors.Success
                        else
                            WhosInColors.DarkTeal
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = if (showCopySuccess) Icons.Rounded.Check else Icons.Outlined.ContentCopy,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (showCopySuccess) "Evento compartido" else "Compartir evento",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            // Reset del estado de copiado
            LaunchedEffect(showCopySuccess) {
                if (showCopySuccess) {
                    delay(2000)
                    showCopySuccess = false
                }
            }
        }
    }
}

@Composable
private fun EmptyEventsState(onCreateClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            WhosInColors.DarkTeal.copy(alpha = 0.1f),
                            WhosInColors.PetrolBlue.copy(alpha = 0.1f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.EventNote,
                contentDescription = null,
                tint = WhosInColors.GrayBlue,
                modifier = Modifier.size(60.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "No tienes eventos",
            style = MaterialTheme.typography.headlineSmall,
            color = WhosInColors.DarkTeal,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Crea tu primer evento para empezar\na gestionar invitados",
            style = MaterialTheme.typography.bodyMedium,
            color = WhosInColors.GrayBlue,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        WhosInPrimaryButton(
            text = "Crear Evento",
            onClick = onCreateClick,
            modifier = Modifier.fillMaxWidth(0.7f)
        )
    }
}

@Composable
private fun DeleteConfirmationDialog(
    eventName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = WhosInColors.White
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icono de advertencia
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(WhosInColors.Error.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Warning,
                        contentDescription = null,
                        tint = WhosInColors.Error,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "¿Eliminar evento?",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = WhosInColors.DarkTeal
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Se eliminará \"$eventName\" y todos sus invitados. Esta acción no se puede deshacer.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = WhosInColors.GrayBlue,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Botones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    WhosInSecondaryButton(
                        text = "Cancelar",
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    )

                    Button(
                        onClick = onConfirm,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = WhosInColors.Error
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Eliminar")
                    }
                }
            }
        }
    }
}

@Composable
private fun EventSelectorDecorationCircles() {
    val infiniteTransition = rememberInfiniteTransition(label = "eventSelectorDecor")

    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 18f,
        animationSpec = infiniteRepeatable(
            animation = tween(2700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset"
    )

    Box(
        modifier = Modifier
            .offset(x = (270 + offset).dp, y = (90 - offset).dp)
            .size(160.dp)
            .alpha(0.08f)
            .clip(CircleShape)
            .background(WhosInColors.LimeGreen)
    )

    Box(
        modifier = Modifier
            .offset(x = (-70 - offset).dp, y = (400 + offset).dp)
            .size(140.dp)
            .alpha(0.06f)
            .clip(CircleShape)
            .background(WhosInColors.OliveGreen)
    )
}