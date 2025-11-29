
package com.ucb.whosin.features.Guest.presentation

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ucb.whosin.features.Guest.domain.model.Guest
import com.ucb.whosin.features.login.presentation.AnimatedEntrance
import com.ucb.whosin.features.login.presentation.WhosInModernTheme
import com.ucb.whosin.ui.components.WhosInPrimaryButton
import com.ucb.whosin.ui.components.WhosInTextField
import com.ucb.whosin.ui.theme.WhosInColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuestListScreen(
    viewModel: GuestListViewModel = koinViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    WhosInModernTheme {
        GuestListScreenContent(
            viewModel = viewModel,
            onNavigateBack = onNavigateBack
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GuestListScreenContent(
    viewModel: GuestListViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showAddGuestDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf<Guest?>(null) }
    var showDeleteDialog by remember { mutableStateOf<Guest?>(null) }
    var startAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        startAnimation = true
        viewModel.loadGuests()
    }

    // Mostrar snackbar para éxito en actualización
    LaunchedEffect(uiState.updateSuccess) {
        if (uiState.updateSuccess) {
            scope.launch {
                snackbarHostState.showSnackbar("Invitado actualizado correctamente")
                viewModel.resetSuccessFlags()
                viewModel.loadGuests()
            }
        }
    }

    // Mostrar snackbar para éxito en eliminación
    LaunchedEffect(uiState.deleteSuccess) {
        if (uiState.deleteSuccess) {
            scope.launch {
                snackbarHostState.showSnackbar("Invitado eliminado")
                viewModel.resetSuccessFlags()
                viewModel.loadGuests()
            }
        }
    }

    // Mostrar errores
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { error ->
            scope.launch {
                snackbarHostState.showSnackbar(error)
                viewModel.clearError()
            }
        }
    }

    // Diálogo para agregar invitado
    if (showAddGuestDialog) {
        AddGuestDialog(
            onDismiss = { showAddGuestDialog = false },
            onConfirm = { name, companions ->
                viewModel.addGuest(name, companions)
                showAddGuestDialog = false
            }
        )
    }

    // Diálogo para editar invitado
    showEditDialog?.let { guest ->
        EditGuestDialog(
            guest = guest,
            onDismiss = { showEditDialog = null },
            onConfirm = { newName ->
                viewModel.updateGuest(guest.guestId, newName, guest.plusOnesAllowed)
                showEditDialog = null
            }
        )
    }

    // Diálogo para confirmar eliminación
    showDeleteDialog?.let { guest ->
        DeleteConfirmDialog(
            guestName = guest.name,
            onDismiss = { showDeleteDialog = null },
            onConfirm = {
                viewModel.deleteGuest(guest.guestId)
                showDeleteDialog = null
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
        containerColor = WhosInColors.DarkTeal,
        floatingActionButton = {
            AnimatedEntrance(visible = startAnimation, delayMillis = 400) {
                FloatingActionButton(
                    onClick = { showAddGuestDialog = true },
                    containerColor = WhosInColors.LimeGreen,
                    contentColor = WhosInColors.DarkTeal,
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PersonAdd,
                        contentDescription = "Agregar invitado",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Decoraciones de fondo
            GuestListDecorationCircles()

            Column(
                modifier = Modifier
                    .fillMaxSize()
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
                            text = "Invitados",
                            style = MaterialTheme.typography.headlineMedium,
                            color = WhosInColors.LightGray,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Estadísticas
                AnimatedEntrance(visible = startAnimation, delayMillis = 100) {
                    GuestStatsCard(
                        totalGuests = uiState.guests.size,
                        confirmedGuests = uiState.guests.count { it.inviteStatus == "confirmed" },
                        checkedInGuests = uiState.guests.count { it.checkedIn }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Buscador
                AnimatedEntrance(visible = startAnimation, delayMillis = 200) {
                    WhosInTextField(
                        value = uiState.searchQuery,
                        onValueChange = { viewModel.onSearchQueryChange(it) },
                        placeholder = "Buscar invitado...",
                        leadingIcon = Icons.Outlined.Search,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Lista de invitados
                AnimatedEntrance(visible = startAnimation, delayMillis = 300) {
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
                    } else if (uiState.filteredGuests.isEmpty()) {
                        EmptyGuestsState(
                            searchQuery = uiState.searchQuery
                        )
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 80.dp)
                        ) {
                            items(
                                items = uiState.filteredGuests,
                                key = { it.guestId }
                            ) { guest ->
                                ModernGuestCard(
                                    guest = guest,
                                    onEditClick = { showEditDialog = guest },
                                    onDeleteClick = { showDeleteDialog = guest }
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
private fun GuestStatsCard(
    totalGuests: Int,
    confirmedGuests: Int,
    checkedInGuests: Int
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = WhosInColors.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                icon = Icons.Outlined.Groups,
                label = "Total",
                value = totalGuests.toString(),
                color = WhosInColors.MossGreen
            )

            Divider(
                modifier = Modifier
                    .width(1.dp)
                    .height(60.dp),
                color = WhosInColors.GrayBlue.copy(alpha = 0.3f)
            )

            StatItem(
                icon = Icons.Outlined.People,
                label = "Confirmados",
                value = confirmedGuests.toString(),
                color = WhosInColors.Confirmed
            )

            Divider(
                modifier = Modifier
                    .width(1.dp)
                    .height(60.dp),
                color = WhosInColors.GrayBlue.copy(alpha = 0.3f)
            )

            StatItem(
                icon = Icons.Rounded.CheckCircle,
                label = "Check-in",
                value = checkedInGuests.toString(),
                color = WhosInColors.CheckedIn
            )
        }
    }
}

@Composable
private fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }

        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = WhosInColors.DarkTeal
        )

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = WhosInColors.GrayBlue
        )
    }
}

@Composable
private fun ModernGuestCard(
    guest: Guest,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = WhosInColors.White,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Nombre del invitado
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = guest.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = WhosInColors.DarkTeal
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Estado
                        StatusChip(
                            status = if (guest.checkedIn) "checked_in"
                            else guest.inviteStatus
                        )

                        // Acompañantes si tiene
                        if (guest.plusOnesAllowed > 0) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = WhosInColors.GrayBlue.copy(alpha = 0.15f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Person,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                        tint = WhosInColors.GrayBlue
                                    )
                                    Text(
                                        text = "+${guest.plusOnesAllowed}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = WhosInColors.GrayBlue
                                    )
                                }
                            }
                        }
                    }
                }

                // Botones de acción
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Botón editar
                    IconButton(
                        onClick = onEditClick,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(WhosInColors.LimeGreen.copy(alpha = 0.2f))
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "Editar",
                            tint = WhosInColors.OliveGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Botón eliminar
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(WhosInColors.Error.copy(alpha = 0.15f))
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Eliminar",
                            tint = WhosInColors.Error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusChip(status: String) {
    val (label, color) = when (status) {
        "checked_in" -> "Check-in" to WhosInColors.CheckedIn
        "confirmed" -> "Confirmado" to WhosInColors.Confirmed
        "pending" -> "Pendiente" to WhosInColors.Warning
        "declined" -> "Rechazado" to WhosInColors.Error
        else -> "Desconocido" to WhosInColors.GrayBlue
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.15f)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = color,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun EmptyGuestsState(searchQuery: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(WhosInColors.GrayBlue.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (searchQuery.isBlank()) Icons.Outlined.PersonAdd
                else Icons.Outlined.Search,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = WhosInColors.GrayBlue
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (searchQuery.isBlank()) "No hay invitados aún"
            else "No se encontraron invitados",
            style = MaterialTheme.typography.titleMedium,
            color = WhosInColors.GrayBlue,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (searchQuery.isBlank())
                "Agrega el primer invitado usando el botón +"
            else "Intenta con otro término de búsqueda",
            style = MaterialTheme.typography.bodyMedium,
            color = WhosInColors.GrayBlue.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun AddGuestDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Int) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var companions by remember { mutableStateOf(0) }

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
                modifier = Modifier.padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Agregar Invitado",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
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

                Spacer(modifier = Modifier.height(24.dp))

                WhosInTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Nombre completo",
                    placeholder = "Ej: Juan Pérez",
                    leadingIcon = Icons.Outlined.Person
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Acompañantes",
                    style = MaterialTheme.typography.labelLarge,
                    color = WhosInColors.DarkTeal
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { if (companions > 0) companions-- },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                if (companions > 0) WhosInColors.LimeGreen
                                else WhosInColors.GrayBlue.copy(alpha = 0.2f)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Reducir",
                            tint = WhosInColors.DarkTeal
                        )
                    }

                    Spacer(modifier = Modifier.width(24.dp))

                    Surface(
                        modifier = Modifier.size(64.dp),
                        shape = CircleShape,
                        color = WhosInColors.LimeGreen.copy(alpha = 0.2f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = companions.toString(),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = WhosInColors.OliveGreen
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(24.dp))

                    IconButton(
                        onClick = { companions++ },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(WhosInColors.LimeGreen)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Aumentar",
                            tint = WhosInColors.DarkTeal
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                WhosInPrimaryButton(
                    text = "Agregar",
                    onClick = {
                        if (name.isNotBlank()) {
                            onConfirm(name, companions)
                        }
                    },
                    enabled = name.isNotBlank()
                )
            }
        }
    }
}

@Composable
private fun EditGuestDialog(
    guest: Guest,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf(guest.name) }

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
                modifier = Modifier.padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Editar Invitado",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
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

                Spacer(modifier = Modifier.height(24.dp))

                WhosInTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Nombre completo",
                    placeholder = "Ej: Juan Pérez",
                    leadingIcon = Icons.Outlined.Person
                )

                Spacer(modifier = Modifier.height(24.dp))

                WhosInPrimaryButton(
                    text = "Guardar",
                    onClick = {
                        if (name.isNotBlank()) {
                            onConfirm(name)
                        }
                    },
                    enabled = name.isNotBlank() && name != guest.name
                )
            }
        }
    }
}

@Composable
private fun DeleteConfirmDialog(
    guestName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = WhosInColors.White,
        title = {
            Text(
                text = "Eliminar Invitado",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = WhosInColors.DarkTeal
            )
        },
        text = {
            Text(
                text = "¿Estás seguro de que deseas eliminar a $guestName de la lista de invitados?",
                style = MaterialTheme.typography.bodyMedium,
                color = WhosInColors.GrayBlue
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = WhosInColors.Error
                )
            ) {
                Text("Eliminar", fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = WhosInColors.GrayBlue)
            }
        }
    )
}

@Composable
private fun GuestListDecorationCircles() {
    val infiniteTransition = rememberInfiniteTransition(label = "guestDecor")
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
            .background(WhosInColors.MintGreen)
    )
}