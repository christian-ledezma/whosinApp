package com.ucb.whosin.features.Guest.presentation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ucb.whosin.features.Guest.domain.model.Guest
import com.ucb.whosin.ui.components.WhosInCard
import com.ucb.whosin.ui.components.WhosInPrimaryButton
import com.ucb.whosin.ui.components.WhosInTextField
import com.ucb.whosin.ui.theme.WhosInColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuestListScreen(
    listViewModel: GuestListViewModel = koinViewModel(),
    addViewModel: AddGuestViewModel = koinViewModel()
) {
    val listUiState by listViewModel.uiState.collectAsState()
    val addUiState by addViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showAddDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedGuest by remember { mutableStateOf<Guest?>(null) }

    // Recargar lista cuando se agrega un invitado exitosamente
    LaunchedEffect(addUiState.isSuccess) {
        if (addUiState.isSuccess) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = "✓ Invitado agregado",
                    duration = SnackbarDuration.Short
                )
                delay(500)
                listViewModel.loadGuests()
                showAddDialog = false
                addViewModel.clearSuccess()
            }
        }
    }

    // Recargar lista cuando se elimina exitosamente
    LaunchedEffect(listUiState.deleteSuccess) {
        if (listUiState.deleteSuccess) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = "Invitado eliminado",
                    duration = SnackbarDuration.Short
                )
                delay(500)
                listViewModel.loadGuests()
                showDeleteDialog = false
                selectedGuest = null
            }
        }
    }

    // Mostrar errores de agregar
    LaunchedEffect(addUiState.errorMessage) {
        addUiState.errorMessage?.let { error ->
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = error,
                    duration = SnackbarDuration.Long
                )
                addViewModel.clearError()
            }
        }
    }

    // Mostrar errores de lista
    LaunchedEffect(listUiState.errorMessage) {
        listUiState.errorMessage?.let { error ->
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = error,
                    duration = SnackbarDuration.Long
                )
                listViewModel.clearError()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Invitados",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.secondary,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar",
                    tint = MaterialTheme.colorScheme.onSecondary
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Barra de búsqueda moderna
            WhosInTextField(
                value = listUiState.searchQuery,
                onValueChange = { listViewModel.onSearchQueryChange(it) },
                placeholder = "Buscar invitados...",
                leadingIcon = Icons.Default.Search,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Stats Cards modernas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ModernStatCard(
                    title = "Total",
                    value = listUiState.guests.size.toString(),
                    color = WhosInColors.MossGreen,
                    modifier = Modifier.weight(1f)
                )
                ModernStatCard(
                    title = "Confirmados",
                    value = listUiState.guests.count { it.inviteStatus == "confirmed" }.toString(),
                    color = WhosInColors.ForestGreen,
                    modifier = Modifier.weight(1f)
                )
                ModernStatCard(
                    title = "Check-in",
                    value = listUiState.guests.count { it.checkedIn }.toString(),
                    color = WhosInColors.CheckedIn,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Lista de invitados
            if (listUiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary)
                }
            } else if (listUiState.filteredGuests.isEmpty()) {
                EmptyState()
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(
                        items = listUiState.filteredGuests,
                        key = { it.guestId }
                    ) { guest ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(animationSpec = tween(300)) +
                                    slideInVertically(initialOffsetY = { it / 4 }),
                            exit = fadeOut() + slideOutVertically()
                        ) {
                            ModernGuestCard(
                                guest = guest,
                                onDelete = {
                                    selectedGuest = guest
                                    showDeleteDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }

        // Diálogo agregar (SIN acompañantes, SIN estado)
        if (showAddDialog) {
            ModernAddGuestDialog(
                isLoading = addUiState.isLoading,
                onDismiss = { showAddDialog = false },
                onConfirm = { name, note ->
                    // SIEMPRE confirmed, plusOnesAllowed = 0
                    addViewModel.addGuest(
                        name = name,
                        plusOnesAllowed = 0,  // No se pregunta en UI
                        inviteStatus = "confirmed",  // Siempre confirmed
                        note = note
                    )
                }
            )
        }

        // Diálogo eliminar
        if (showDeleteDialog && selectedGuest != null) {
            ModernDeleteDialog(
                guestName = selectedGuest!!.name,
                isLoading = listUiState.isLoading,
                onConfirm = {
                    listViewModel.deleteGuest(selectedGuest!!.guestId)
                },
                onDismiss = {
                    showDeleteDialog = false
                    selectedGuest = null
                }
            )
        }
    }
}

@Composable
fun ModernStatCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    WhosInCard(
        modifier = modifier.height(90.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ModernGuestCard(
    guest: Guest,
    onDelete: () -> Unit
) {
    var scale by remember { mutableStateOf(0.8f) }

    LaunchedEffect(Unit) {
        animate(
            initialValue = 0.8f,
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) { value, _ -> scale = value }
    }

    WhosInCard(
        modifier = Modifier.scale(scale)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = guest.name.firstOrNull()?.toString()?.uppercase() ?: "?",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = guest.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Solo mostrar check-in, NO acompañantes, NO estado
                    if (guest.checkedIn) {
                        MinimalChip(
                            text = "✓ Check-in",
                            color = WhosInColors.CheckedIn
                        )
                    } else {
                        MinimalChip(
                            text = "Confirmado",
                            color = WhosInColors.Confirmed
                        )
                    }

                    // Nota si existe
                    if (!guest.note.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = guest.note,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }
                }
            }

            // Botón eliminar
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun MinimalChip(
    text: String,
    color: Color
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.15f)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No hay invitados",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Toca + para agregar uno",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernAddGuestDialog(
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String, String?) -> Unit  // Solo nombre y nota (NO acompañantes, NO estado)
) {
    var name by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text(
                "Agregar Invitado",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Solo nombre (SIN acompañantes, SIN estado)
                WhosInTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Nombre completo",
                    placeholder = "Ej: Juan Pérez",
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth()
                )

                // Solo nota opcional
                WhosInTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = "Nota (opcional)",
                    placeholder = "Añade una nota...",
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            WhosInPrimaryButton(
                text = "Agregar",
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(name, note.ifBlank { null })
                    }
                },
                enabled = !isLoading && name.isNotBlank(),
                isLoading = isLoading,
                modifier = Modifier.fillMaxWidth()
            )
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text(
                    "Cancelar",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    )
}

@Composable
fun ModernDeleteDialog(
    guestName: String,
    isLoading: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        icon = {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(48.dp)
            )
        },
        title = {
            Text(
                "¿Eliminar invitado?",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Text(
                "¿Estás seguro de eliminar a $guestName? Esta acción no se puede deshacer.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Eliminar")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Cancelar",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    )
}