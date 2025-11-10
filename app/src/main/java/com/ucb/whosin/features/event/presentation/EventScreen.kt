    package com.ucb.whosin.features.event.presentation

    import androidx.compose.foundation.background
    import androidx.compose.foundation.border
    import androidx.compose.foundation.layout.*
    import androidx.compose.foundation.shape.CircleShape
    import androidx.compose.foundation.shape.RoundedCornerShape
    import androidx.compose.material.icons.Icons
    import androidx.compose.material.icons.filled.Add
    import androidx.compose.material.icons.filled.Search
    import androidx.compose.material3.*
    import androidx.compose.runtime.*
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.draw.clip
    import androidx.compose.ui.draw.shadow
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.text.font.FontWeight
    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.unit.sp
    import org.koin.androidx.compose.koinViewModel
    import java.text.SimpleDateFormat
    import java.util.*

    @Composable
    fun EventScreen(
        modifier: Modifier = Modifier,
        vm: EventViewModel = koinViewModel()
    ) {
        var searchEvent by remember { mutableStateOf("") }
        val state by vm.state.collectAsState()

        // Paleta de colores según el ejemplo
        val backgroundColor = Color(0xFF1C1F26)   // Fondo general oscuro
        val cardColor = Color(0xFF2B3038)         // Fondo de las cards
        val accentColor = Color(0xFF69A3A1)       // Azul grisáceo (Search button)
        val textColor = Color(0xFFE0E0E0)         // Texto claro
        val white = Color.White

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(16.dp)
        ) {

            Column(
                modifier = modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // 🔹 Campo de búsqueda blanco con ícono azul
                OutlinedTextField(
                    value = searchEvent,
                    onValueChange = { searchEvent = it },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = accentColor
                        )
                    },
                    placeholder = { Text("Buscar evento", color = Color.Gray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(white), // Fondo blanco completo
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        cursorColor = accentColor
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 🔹 Botón de búsqueda centrado y más estrecho
                Button(
                    onClick = {
                        if (searchEvent.isBlank()) {
                            vm.setEmptySearchMessage()
                        } else {
                            vm.fetchEventByName(searchEvent)
                        }
                    },
                    modifier = Modifier
                        .width(150.dp)
                        .height(38.dp),
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                ) {
                    Text(
                        "Search",
                        color = backgroundColor,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                when (val st = state) {
                    is EventViewModel.EventStateUI.Error -> {
                        Text("Error: ${st.message}", color = Color.White)
                    }

                    EventViewModel.EventStateUI.Init -> {
                        Text("Ingrese un nombre de evento para buscar.", color = textColor)
                    }

                    EventViewModel.EventStateUI.Loading -> {
                        Text("Cargando...", color = textColor)
                    }

                    EventViewModel.EventStateUI.EmptyResult -> {
                        EmptyEventListMessage(white, backgroundColor)
                    }

                    is EventViewModel.EventStateUI.Success -> {
                        val event = st.event

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                                .shadow(8.dp, RoundedCornerShape(12.dp)),
                            colors = CardDefaults.cardColors(containerColor = cardColor),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Encabezado con título y fecha
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "🎉 ${event.name}",
                                        color = accentColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                    Text(
                                        text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(event.date.toDate()),
                                        color = Color.LightGray,
                                        fontSize = 13.sp
                                    )
                                }

                                Divider(color = accentColor.copy(alpha = 0.3f), thickness = 1.dp)

                                // Detalles con íconos y espaciado
                                EventInfoRow("📍 Location", event.locationName, textColor)
                                EventInfoRow("📅 Status", event.status, textColor)
                                EventInfoRow("👥 Capacity", "${event.capacity}", textColor)
                                EventInfoRow("✅ Checked In", "${event.totalCheckedIn}", textColor)
                                EventInfoRow("✉️ Total Invited", "${event.totalInvited}", textColor)
                                EventInfoRow("🛡️ Guard Mode", if (event.guardModeEnabled) "Enabled" else "Disabled", textColor)
                                EventInfoRow(
                                    "🕒 Created At",
                                    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(event.createdAt.toDate()),
                                    textColor
                                )
                            }
                        }
                    }

                }
            }

            // 🔹 Botón flotante "+"
            FloatingActionButton(
                onClick = { /* TODO: acción para crear evento */ },
                containerColor = accentColor,
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar evento",
                    tint = Color.White
                )
            }
        }
    }

    @Composable
    fun EmptyEventListMessage(textColor: Color, borderColor: Color) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, textColor, RoundedCornerShape(8.dp))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Press '+' icon to add your first event",
                color = textColor,
                fontSize = 14.sp
            )
        }
    }

    @Composable
    fun EventInfoRow(label: String, value: String, textColor: Color) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label, color = textColor.copy(alpha = 0.8f), fontSize = 13.sp)
            Text(text = value, color = Color.White, fontWeight = FontWeight.Medium, fontSize = 13.sp)
        }
    }
