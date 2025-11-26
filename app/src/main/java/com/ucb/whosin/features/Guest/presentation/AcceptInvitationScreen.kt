package com.ucb.whosin.features.Guest.presentation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Snackbar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.ucb.whosin.features.event.domain.model.EventModel
import com.ucb.whosin.features.login.presentation.AnimatedEntrance
import com.ucb.whosin.features.login.presentation.WhosInModernTheme
import com.ucb.whosin.ui.components.WhosInPrimaryButton
import com.ucb.whosin.ui.components.WhosInTextField
import com.ucb.whosin.ui.theme.WhosInColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcceptInvitationScreen(
    viewModel: AcceptInvitationViewModel = koinViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    WhosInModernTheme {
        AcceptInvitationScreenContent(
            viewModel = viewModel,
            onNavigateBack = onNavigateBack
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AcceptInvitationScreenContent(
    viewModel: AcceptInvitationViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var eventId by remember { mutableStateOf("") }
    var companions by remember { mutableStateOf(0) }
    var startAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        startAnimation = true
    }

    // Mostrar éxito
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = "¡Asistencia confirmada exitosamente!",
                    duration = SnackbarDuration.Short
                )
                delay(1500)
                onNavigateBack()
            }
        }
    }

    // Mostrar errores
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
            AcceptInvitationDecorationCircles()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
                    .statusBarsPadding()
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Header
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
                            text = "Aceptar Invitación",
                            style = MaterialTheme.typography.headlineMedium,
                            color = WhosInColors.LightGray,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Icono decorativo
                AnimatedEntrance(visible = startAnimation, delayMillis = 100) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        WhosInColors.DarkTeal,
                                        WhosInColors.PetrolBlue
                                    )
                                )
                            )
                            .align(Alignment.CenterHorizontally),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = WhosInColors.LimeGreen,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Card principal
                AnimatedEntrance(visible = startAnimation, delayMillis = 200) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        color = WhosInColors.White,
                        shadowElevation = 8.dp
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Text(
                                text = "Confirma tu asistencia",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = WhosInColors.DarkTeal
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Ingresa el ID del evento para confirmar",
                                style = MaterialTheme.typography.bodyMedium,
                                color = WhosInColors.GrayBlue
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // Campo ID del evento
                            WhosInTextField(
                                value = eventId,
                                onValueChange = {
                                    eventId = it
                                    if (it.isNotBlank() && uiState.event == null) {
                                        viewModel.searchEvent(it)
                                    }
                                },
                                label = "ID del Evento",
                                placeholder = "Pega aquí el ID del evento",
                                enabled = !uiState.isLoading && !uiState.isSearching
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Loading búsqueda
                if (uiState.isSearching) {
                    AnimatedEntrance(visible = true, delayMillis = 0) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            color = WhosInColors.White,
                            shadowElevation = 8.dp
                        ) {
                            Column(
                                modifier = Modifier.padding(40.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(40.dp),
                                    color = WhosInColors.LimeGreen,
                                    strokeWidth = 3.dp
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Buscando evento...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = WhosInColors.GrayBlue
                                )
                            }
                        }
                    }
                }

                // Información del evento encontrado
                uiState.event?.let { event ->
                    AnimatedEntrance(visible = true, delayMillis = 0) {
                        Column {
                            ModernEventInfoCard(event = event)

                            Spacer(modifier = Modifier.height(24.dp))

                            // Mapa estático
                            EventLocationMap(
                                latitude = event.latitude,
                                longitude = event.longitude,
                                locationName = event.locationName
                            )

                            Spacer(modifier = Modifier.height(24.dp))


                            if (uiState.isUserAlreadyInvited) {
                                // Usuario YA invitado - mostrar banner
                                AlreadyInvitedBanner()

                                Spacer(modifier = Modifier.height(24.dp))

                                // Botón para regresar
                                WhosInPrimaryButton(
                                    text = "Entendido",
                                    onClick = onNavigateBack
                                )
                            } else {
                                // Usuario NO invitado - flujo normal
                                CompanionsSelector(
                                    companions = companions,
                                    onCompanionsChange = { companions = it },
                                    enabled = !uiState.isLoading
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                WarningBanner()

                                Spacer(modifier = Modifier.height(32.dp))

                                WhosInPrimaryButton(
                                    text = "Confirmar Asistencia",
                                    onClick = {
                                        viewModel.confirmAttendance(
                                            eventId = eventId,
                                            companions = companions
                                        )
                                    },
                                    enabled = !uiState.isLoading,
                                    isLoading = uiState.isLoading
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                TextButton(
                                    onClick = onNavigateBack,
                                    enabled = !uiState.isLoading,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Cancelar",
                                        color = WhosInColors.GrayBlue
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun ModernEventInfoCard(event: EventModel) {
    val dateFormatter = SimpleDateFormat("EEEE, dd 'de' MMMM 'de' yyyy", Locale("es", "ES"))
    val dateStr = dateFormatter.format(event.date.toDate())

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = WhosInColors.White,
        shadowElevation = 8.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            /* Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = WhosInColors.Success.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "Evento Encontrado",
                            style = MaterialTheme.typography.labelMedium,
                            color = WhosInColors.Success,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(WhosInColors.Success.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        tint = WhosInColors.Success,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }*/

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = event.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = WhosInColors.DarkTeal
            )

            Spacer(modifier = Modifier.height(20.dp))

            HorizontalDivider(color = WhosInColors.GrayBlue.copy(alpha = 0.2f))

            Spacer(modifier = Modifier.height(20.dp))

            // Fecha
            EventInfoRow(
                icon = Icons.Outlined.CalendarToday,
                label = "Fecha",
                value = dateStr.replaceFirstChar { it.uppercase() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Ubicación
            EventInfoRow(
                icon = Icons.Outlined.LocationOn,
                label = "Lugar",
                value = event.locationName
            )
        }
    }
}

@Composable
private fun EventInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(WhosInColors.LimeGreen.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = WhosInColors.OliveGreen,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = WhosInColors.GrayBlue
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = WhosInColors.DarkTeal,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun EventLocationMap(
    latitude: Double,
    longitude: Double,
    locationName: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = WhosInColors.White,
        shadowElevation = 8.dp
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = "Ubicación del Evento",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = WhosInColors.DarkTeal
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = locationName,
                style = MaterialTheme.typography.bodyMedium,
                color = WhosInColors.GrayBlue
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Mapa estático
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                val location = LatLng(latitude, longitude)
                val cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(location, 15f)
                }

                val markerState = remember(latitude, longitude) {
                    MarkerState(position = location)
                }

                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(
                        isMyLocationEnabled = false
                    ),
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = false,
                        scrollGesturesEnabled = false,
                        zoomGesturesEnabled = false,
                        tiltGesturesEnabled = false,
                        rotationGesturesEnabled = false,
                        compassEnabled = false,
                        mapToolbarEnabled = false
                    )
                ) {
                    val customIcon = remember {
                        val bitmap = Bitmap.createBitmap(48, 72, Bitmap.Config.ARGB_8888)
                        val canvas = Canvas(bitmap)

                        // Configurar el paint con el color DarkTeal
                        val paint = Paint().apply {
                            color = WhosInColors.DarkTeal.toArgb()
                            style = Paint.Style.FILL
                            isAntiAlias = true
                        }

                        // Dibujar el pin del marcador
                        val path = Path().apply {
                            // Forma de lágrima/gota para el marcador
                            moveTo(24f, 72f) // Punta inferior
                            cubicTo(12f, 60f, 0f, 40f, 0f, 24f) // Curva izquierda
                            cubicTo(0f, 10.75f, 10.75f, 0f, 24f, 0f) // Curva superior izquierda
                            cubicTo(37.25f, 0f, 48f, 10.75f, 48f, 24f) // Curva superior derecha
                            cubicTo(48f, 40f, 36f, 60f, 24f, 72f) // Curva derecha a punta
                            close()
                        }
                        canvas.drawPath(path, paint)

                        // Círculo blanco en el centro
                        val paintWhite = Paint().apply {
                            color = android.graphics.Color.WHITE
                            style = Paint.Style.FILL
                            isAntiAlias = true
                        }
                        canvas.drawCircle(24f, 24f, 10f, paintWhite)

                        BitmapDescriptorFactory.fromBitmap(bitmap)
                    }

                    Marker(
                        state = markerState,
                        title = locationName,
                        icon = customIcon
                    )
                }

                // Overlay con icono personalizado
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(WhosInColors.DarkTeal),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = WhosInColors.LimeGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Coordenadas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = WhosInColors.LightGray
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Latitud",
                            style = MaterialTheme.typography.labelSmall,
                            color = WhosInColors.GrayBlue
                        )
                        Text(
                            text = "%.6f".format(latitude),
                            style = MaterialTheme.typography.bodyMedium,
                            color = WhosInColors.DarkTeal,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = WhosInColors.LightGray
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Longitud",
                            style = MaterialTheme.typography.labelSmall,
                            color = WhosInColors.GrayBlue
                        )
                        Text(
                            text = "%.6f".format(longitude),
                            style = MaterialTheme.typography.bodyMedium,
                            color = WhosInColors.DarkTeal,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CompanionsSelector(
    companions: Int,
    onCompanionsChange: (Int) -> Unit,
    enabled: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = WhosInColors.White,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "¿Cuántas personas te acompañarán?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = WhosInColors.DarkTeal
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { if (companions > 0) onCompanionsChange(companions - 1) },
                    enabled = companions > 0 && enabled,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            if (companions > 0 && enabled)
                                WhosInColors.LimeGreen
                            else
                                WhosInColors.GrayBlue.copy(alpha = 0.2f)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Reducir",
                        tint = WhosInColors.DarkTeal
                    )
                }

                Spacer(modifier = Modifier.width(32.dp))

                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = CircleShape,
                    color = WhosInColors.LimeGreen.copy(alpha = 0.2f)
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = companions.toString(),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = WhosInColors.OliveGreen
                        )
                    }
                }

                Spacer(modifier = Modifier.width(32.dp))

                IconButton(
                    onClick = { onCompanionsChange(companions + 1) },
                    enabled = enabled,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            if (enabled)
                                WhosInColors.LimeGreen
                            else
                                WhosInColors.GrayBlue.copy(alpha = 0.2f)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Aumentar",
                        tint = WhosInColors.DarkTeal
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = when (companions) {
                    0 -> "Solo yo asistiré"
                    1 -> "1 acompañante"
                    else -> "$companions acompañantes"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = WhosInColors.GrayBlue,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun WarningBanner() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = WhosInColors.Warning.copy(alpha = 0.15f)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Outlined.Warning,
                contentDescription = "Advertencia",
                tint = WhosInColors.Warning,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "Importante",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = WhosInColors.Warning
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "El organizador del evento puede modificar o eliminar tu asistencia en cualquier momento.",
                    style = MaterialTheme.typography.bodySmall,
                    color = WhosInColors.Warning
                )
            }
        }
    }
}

@Composable
private fun AlreadyInvitedBanner() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = WhosInColors.LimeGreen.copy(alpha = 0.2f),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(WhosInColors.Success),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = null,
                    tint = WhosInColors.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Ya confirmaste tu asistencia",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = WhosInColors.OliveGreen
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tu asistencia a este evento ya fue registrada anteriormente.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = WhosInColors.GrayBlue
                )
            }
        }
    }
}

@Composable
private fun AcceptInvitationDecorationCircles() {
    val infiniteTransition = rememberInfiniteTransition(label = "acceptDecor")

    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset"
    )

    Box(
        modifier = Modifier
            .offset(x = (290 + offset).dp, y = (80 - offset).dp)
            .size(140.dp)
            .alpha(0.12f)
            .clip(CircleShape)
            .background(WhosInColors.LimeGreen)
    )

    Box(
        modifier = Modifier
            .offset(x = (-50 - offset).dp, y = (450 + offset).dp)
            .size(120.dp)
            .alpha(0.08f)
            .clip(CircleShape)
            .background(WhosInColors.MintGreen)
    )
}