package com.ucb.whosin.features.Guest.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ucb.whosin.features.Guest.domain.model.Guest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuestListScreen(
    listViewModel: GuestListViewModel = koinViewModel(),
    addViewModel: AddGuestViewModel = koinViewModel(),
    onNavigateToAddGuest: () -> Unit = {}
) {
    val listUiState by listViewModel.uiState.collectAsState()
    val addUiState by addViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedGuest by remember { mutableStateOf<Guest?>(null) }

    // Recargar lista cuando se agrega un invitado exitosamente
    LaunchedEffect(addUiState.isSuccess) {
        if (addUiState.isSuccess) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = "¡Invitado agregado exitosamente!",
                    duration = SnackbarDuration.Short
                )
                delay(500)
                listViewModel.loadGuests()
                showAddDialog = false
            }
        }
    }

    // Recargar lista cuando se edita exitosamente
    LaunchedEffect(listUiState.updateSuccess) {
        if (listUiState.updateSuccess) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = "¡Invitado actualizado exitosamente!",
                    duration = SnackbarDuration.Short
                )
                delay(500)
                listViewModel.loadGuests()
                showEditDialog = false
                selectedGuest = null
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
        topBar = {
            TopAppBar(
                title = { Text("Invitados") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF5E6FA3),
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color(0xFF5E6FA3)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar invitado",
                    tint = Color.White
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Barra de búsqueda
            OutlinedTextField(
                value = listUiState.searchQuery,
                onValueChange = { listViewModel.onSearchQueryChange(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar invitados...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Buscar")
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF5E6FA3),
                    focusedLabelColor = Color(0xFF5E6FA3)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Estadísticas rápidas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatCard(
                    title = "Total",
                    value = listUiState.guests.size.toString(),
                    color = Color(0xFF5E6FA3)
                )
                StatCard(
                    title = "Confirmados",
                    value = listUiState.guests.count { it.inviteStatus == "confirmed" }.toString(),
                    color = Color(0xFF4CAF50)
                )
                StatCard(
                    title = "Check-in",
                    value = listUiState.guests.count { it.checkedIn }.toString(),
                    color = Color(0xFF2196F3)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Lista de invitados
            if (listUiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF5E6FA3))
                }
            } else if (listUiState.filteredGuests.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (listUiState.searchQuery.isNotEmpty())
                            "No se encontraron invitados"
                        else
                            "No hay invitados aún.\nToca + para agregar uno.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(listUiState.filteredGuests) { guest ->
                        GuestCardWithActions(
                            guest = guest,
                            onEdit = {
                                selectedGuest = guest
                                showEditDialog = true
                            },
                            onDelete = {
                                selectedGuest = guest
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
        }

        // Diálogo para agregar invitado
        if (showAddDialog) {
            AddGuestDialog(
                isLoading = addUiState.isLoading,
                onDismiss = { showAddDialog = false },
                onConfirm = { name, plusOnes, status, note ->
                    addViewModel.addGuest(name, plusOnes, status, note)
                }
            )
        }

        // Diálogo para editar invitado
        if (showEditDialog && selectedGuest != null) {
            EditGuestDialog(
                guest = selectedGuest!!,
                isLoading = listUiState.isLoading,
                onDismiss = {
                    showEditDialog = false
                    selectedGuest = null
                },
                onConfirm = { companions ->
                    listViewModel.updateGuestCompanions(selectedGuest!!.guestId, companions)
                }
            )
        }

        // Diálogo para confirmar eliminación
        if (showDeleteDialog && selectedGuest != null) {
            AlertDialog(
                onDismissRequest = {
                    showDeleteDialog = false
                    selectedGuest = null
                },
                title = {
                    Text(
                        "Eliminar Invitado",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD32F2F)
                    )
                },
                text = {
                    Text("¿Estás seguro de que deseas eliminar a ${selectedGuest!!.name}? Esta acción no se puede deshacer.")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            listViewModel.deleteGuest(selectedGuest!!.guestId)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFD32F2F)
                        ),
                        enabled = !listUiState.isLoading
                    ) {
                        if (listUiState.isLoading) {
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
                        onClick = {
                            showDeleteDialog = false
                            selectedGuest = null
                        },
                        enabled = !listUiState.isLoading
                    ) {
                        Text("Cancelar", color = Color(0xFF5E6FA3))
                    }
                }
            )
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    color: Color
) {
    Card(
        modifier = Modifier
            .width(110.dp)
            .height(80.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = color.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun GuestCardWithActions(
    guest: Guest,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Avatar con inicial
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFF5E6FA3).copy(alpha = 0.2f)
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = guest.name.firstOrNull()?.toString()?.uppercase() ?: "?",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF5E6FA3)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = guest.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Estado de invitación
                        StatusChip(
                            text = when (guest.inviteStatus) {
                                "confirmed" -> "Confirmado"
                                "pending" -> "Pendiente"
                                "declined" -> "Rechazado"
                                else -> "Sin estado"
                            },
                            color = when (guest.inviteStatus) {
                                "confirmed" -> Color(0xFF4CAF50)
                                "pending" -> Color(0xFFFFA726)
                                "declined" -> Color(0xFFE57373)
                                else -> Color.Gray
                            }
                        )

                        // Check-in status
                        if (guest.checkedIn) {
                            StatusChip(
                                text = "✓ Check-in",
                                color = Color(0xFF2196F3)
                            )
                        }
                    }

                    // Información adicional
                    if (guest.plusOnesAllowed > 0) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "+${guest.plusOnesAllowed} acompañante${if (guest.plusOnesAllowed > 1) "s" else ""}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF666666)
                        )
                    }

                    // Nota
                    if (!guest.note.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "📝 ${guest.note}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF888888),
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }
                }
            }

            // Botones de acción
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = Color(0xFF5E6FA3),
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = Color(0xFFD32F2F),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun StatusChip(
    text: String,
    color: Color
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.15f)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditGuestDialog(
    guest: Guest,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var companions by remember { mutableStateOf(guest.plusOnesAllowed) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Editar Invitado",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5E6FA3)
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Invitado: ${guest.name}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Ajustar acompañantes:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF666666)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { if (companions > 0) companions-- },
                        enabled = companions > 0 && !isLoading
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Reducir",
                            tint = Color(0xFF5E6FA3)
                        )
                    }

                    Surface(
                        modifier = Modifier.size(60.dp),
                        shape = RoundedCornerShape(30.dp),
                        color = Color(0xFF5E6FA3).copy(alpha = 0.1f)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = companions.toString(),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF5E6FA3)
                            )
                        }
                    }

                    IconButton(
                        onClick = { companions++ },
                        enabled = !isLoading
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Aumentar",
                            tint = Color(0xFF5E6FA3)
                        )
                    }
                }

                Text(
                    text = if (companions == 0) "Sin acompañantes"
                    else if (companions == 1) "1 acompañante"
                    else "$companions acompañantes",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF666666),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(companions) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF5E6FA3)
                ),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Guardar")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text("Cancelar", color = Color(0xFF5E6FA3))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGuestDialog(
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String, Int, String, String?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var plusOnes by remember { mutableStateOf("0") }
    var note by remember { mutableStateOf("") }
    var inviteStatus by remember { mutableStateOf("pending") }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Agregar Invitado",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5E6FA3)
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre completo") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isLoading,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF5E6FA3),
                        focusedLabelColor = Color(0xFF5E6FA3)
                    )
                )

                OutlinedTextField(
                    value = plusOnes,
                    onValueChange = { if (it.all { char -> char.isDigit() }) plusOnes = it },
                    label = { Text("Acompañantes permitidos") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isLoading,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF5E6FA3),
                        focusedLabelColor = Color(0xFF5E6FA3)
                    )
                )

                // Dropdown para estado de invitación
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { if (!isLoading) expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = when (inviteStatus) {
                            "pending" -> "Pendiente"
                            "confirmed" -> "Confirmado"
                            "declined" -> "Rechazado"
                            else -> "Pendiente"
                        },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Estado") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        enabled = !isLoading,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF5E6FA3),
                            focusedLabelColor = Color(0xFF5E6FA3)
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Pendiente") },
                            onClick = {
                                inviteStatus = "pending"
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Confirmado") },
                            onClick = {
                                inviteStatus = "confirmed"
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Rechazado") },
                            onClick = {
                                inviteStatus = "declined"
                                expanded = false
                            }
                        )
                    }
                }

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Nota (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    enabled = !isLoading,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF5E6FA3),
                        focusedLabelColor = Color(0xFF5E6FA3)
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(
                            name,
                            plusOnes.toIntOrNull() ?: 0,
                            inviteStatus,
                            note.ifBlank { null }
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF5E6FA3)
                ),
                enabled = !isLoading && name.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Agregar")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text("Cancelar", color = Color(0xFF5E6FA3))
            }
        }
    )
}