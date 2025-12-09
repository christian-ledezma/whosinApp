package com.ucb.whosin.features.event.presentation

import android.util.Log
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
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ucb.whosin.features.event.domain.model.EventModel
import com.ucb.whosin.features.login.presentation.AnimatedEntrance
import com.ucb.whosin.features.login.presentation.WhosInModernTheme
import com.ucb.whosin.ui.theme.WhosInColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.text.get

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuardEventsScreen(
    viewModel: GuardEventsViewModel = koinViewModel(), // ← MOVIDO AQUÍ
    onNavigateBack: () -> Unit = {},
    onEventSelected: (String) -> Unit = {}
) {
    Log.d("SCREEN_TEST", "========================================")
    Log.d("SCREEN_TEST", "GuardEventsScreen composable llamado")
    Log.d("SCREEN_TEST", "ViewModel: $viewModel")
    Log.d("SCREEN_TEST", "========================================")

    WhosInModernTheme {
        GuardEventsScreenContent(
            viewModel = viewModel, // ← PASADO COMO PARÁMETRO
            onNavigateBack = onNavigateBack,
            onEventSelected = onEventSelected
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GuardEventsScreenContent(
    viewModel: GuardEventsViewModel, // ← RECIBIDO COMO PARÁMETRO
    onNavigateBack: () -> Unit,
    onEventSelected: (String) -> Unit
) {
    Log.d("SCREEN_TEST", "GuardEventsScreenContent renderizado")

    val uiState = viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(viewModel) {
        Log.d("SCREEN_TEST", "LaunchedEffect ejecutado")
        viewModel.loadGuardEvents()
    }

    var startAnimation = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        startAnimation.value = true
    }

    LaunchedEffect(uiState.value.errorMessage) {
        uiState.value.errorMessage?.let { error ->
            Log.e("SCREEN_TEST", "Error mostrado: $error")
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
            GuardEventsDecorationCircles()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .statusBarsPadding()
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Header
                AnimatedEntrance(visible = startAnimation.value, delayMillis = 0) {
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

                        Column {
                            Text(
                                text = "Modo Guardia",
                                style = MaterialTheme.typography.headlineMedium,
                                color = WhosInColors.LightGray,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${uiState.value.events.size} evento${if (uiState.value.events.size != 1) "s" else ""} asignado${if (uiState.value.events.size != 1) "s" else ""}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = WhosInColors.GrayBlue
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Contenido
                if (uiState.value.isLoading) {
                    Log.d("SCREEN_TEST", "Mostrando loading...")
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = WhosInColors.LimeGreen,
                            strokeWidth = 3.dp
                        )
                    }
                } else if (uiState.value.events.isEmpty()) {
                    Log.d("SCREEN_TEST", "Mostrando estado vacío...")
                    AnimatedEntrance(visible = startAnimation.value, delayMillis = 100) {
                        EmptyGuardEventsState()
                    }
                } else {
                    Log.d("SCREEN_TEST", "Mostrando ${uiState.value.events.size} eventos...")
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        items(uiState.value.events) { event ->
                            GuardEventCard(
                                event = event,
                                totalInvited = uiState.value.totalInvitedByEvent[event.eventId] ?: 0,
                                onClick = { onEventSelected(event.eventId) }
                            )
                        }
                    }
                }
            }
        }
    }
}
@Composable
private fun GuardEventCard(
    event: EventModel,
    totalInvited: Int,
    onClick: () -> Unit
) {
    val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale("es", "ES"))
    val dateStr = dateFormatter.format(event.date.toDate())

    Surface(
        modifier = Modifier.Companion
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
        color = WhosInColors.White,
        shadowElevation = 6.dp
    ) {
        Column(
            modifier = Modifier.Companion.padding(20.dp)
        ) {
            // Header con badge de guardia
            Row(
                modifier = Modifier.Companion.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Companion.Top
            ) {
                Column(modifier = Modifier.Companion.weight(1f)) {
                    Text(
                        text = event.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Companion.Bold,
                        color = WhosInColors.DarkTeal,
                        maxLines = 2,
                        overflow = TextOverflow.Companion.Ellipsis
                    )
                }

                // Badge de guardia
                Surface(
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                    color = WhosInColors.LimeGreen.copy(alpha = 0.2f)
                ) {
                    Row(
                        modifier = Modifier.Companion.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.Companion.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Security,
                            contentDescription = null,
                            modifier = Modifier.Companion.size(16.dp),
                            tint = WhosInColors.OliveGreen
                        )
                        Text(
                            text = "Guardia",
                            style = MaterialTheme.typography.labelMedium,
                            color = WhosInColors.OliveGreen,
                            fontWeight = FontWeight.Companion.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.Companion.height(12.dp))

            // Fecha
            Row(
                verticalAlignment = Alignment.Companion.CenterVertically
            ) {
                Box(
                    modifier = Modifier.Companion
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(WhosInColors.PetrolBlue.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Companion.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarToday,
                        contentDescription = null,
                        tint = WhosInColors.PetrolBlue,
                        modifier = Modifier.Companion.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.Companion.width(8.dp))
                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.bodyMedium,
                    color = WhosInColors.GrayBlue
                )
            }

            Spacer(modifier = Modifier.Companion.height(8.dp))

            // Ubicación
            Row(
                verticalAlignment = Alignment.Companion.CenterVertically
            ) {
                Box(
                    modifier = Modifier.Companion
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(WhosInColors.MintGreen.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Companion.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = WhosInColors.OliveGreen,
                        modifier = Modifier.Companion.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.Companion.width(8.dp))
                Text(
                    text = event.locationName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = WhosInColors.GrayBlue,
                    maxLines = 1,
                    overflow = TextOverflow.Companion.Ellipsis
                )
            }

            Spacer(modifier = Modifier.Companion.height(12.dp))

            HorizontalDivider(color = WhosInColors.GrayBlue.copy(alpha = 0.2f))

            Spacer(modifier = Modifier.Companion.height(12.dp))

            // Estadísticas en el pie
            Row(
                modifier = Modifier.Companion.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.Companion.CenterVertically
            ) {
                // Check-in
                Column(
                    horizontalAlignment = Alignment.Companion.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.Companion.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.Companion.size(18.dp),
                            tint = WhosInColors.Success
                        )
                        Text(
                            text = event.totalCheckedIn.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Companion.Bold,
                            color = WhosInColors.DarkTeal
                        )
                    }
                    Text(
                        text = "Check-in",
                        style = MaterialTheme.typography.bodySmall,
                        color = WhosInColors.GrayBlue
                    )
                }

                // Divider vertical
                Box(
                    modifier = Modifier.Companion
                        .width(1.dp)
                        .height(40.dp)
                        .background(WhosInColors.GrayBlue.copy(alpha = 0.3f))
                )

                // Total invitados
                Column(
                    horizontalAlignment = Alignment.Companion.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.Companion.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.People,
                            contentDescription = null,
                            modifier = Modifier.Companion.size(18.dp),
                            tint = WhosInColors.OliveGreen
                        )
                        Text(
                            text = event.calculatedTotalInvited.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Companion.Bold,
                            color = WhosInColors.DarkTeal
                        )
                    }
                    Text(
                        text = "Invitados",
                        style = MaterialTheme.typography.bodySmall,
                        color = WhosInColors.GrayBlue
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyGuardEventsState() {
    Column(
        modifier = Modifier.Companion
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.Companion.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.Companion
                .size(120.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.Companion.linearGradient(
                        colors = listOf(
                            WhosInColors.DarkTeal.copy(alpha = 0.1f),
                            WhosInColors.PetrolBlue.copy(alpha = 0.1f)
                        )
                    )
                ),
            contentAlignment = Alignment.Companion.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Security,
                contentDescription = null,
                tint = WhosInColors.GrayBlue,
                modifier = Modifier.Companion.size(60.dp)
            )
        }

        Spacer(modifier = Modifier.Companion.height(24.dp))

        Text(
            text = "No tienes eventos asignados",
            style = MaterialTheme.typography.headlineSmall,
            color = WhosInColors.DarkTeal,
            fontWeight = FontWeight.Companion.Bold,
            textAlign = TextAlign.Companion.Center
        )

        Spacer(modifier = Modifier.Companion.height(8.dp))

        Text(
            text = "Actualmente no estás asignado como guardia en ningún evento activo o próximo.",
            style = MaterialTheme.typography.bodyMedium,
            color = WhosInColors.GrayBlue,
            textAlign = TextAlign.Companion.Center
        )
    }
}

@Composable
private fun GuardEventsDecorationCircles() {
    val infiniteTransition = rememberInfiniteTransition(label = "guardEventsDecor")

    val offset = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 18f,
        animationSpec = infiniteRepeatable(
            animation = tween(2700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset"
    ).value

    Box(
        modifier = Modifier.Companion
            .offset(x = (270 + offset).dp, y = (90 - offset).dp)
            .size(160.dp)
            .alpha(0.08f)
            .clip(CircleShape)
            .background(WhosInColors.LimeGreen)
    )

    Box(
        modifier = Modifier.Companion
            .offset(x = (-70 - offset).dp, y = (400 + offset).dp)
            .size(140.dp)
            .alpha(0.06f)
            .clip(CircleShape)
            .background(WhosInColors.OliveGreen)
    )
}