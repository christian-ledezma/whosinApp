package com.ucb.whosin.features.event.presentation

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.ucb.whosin.features.login.presentation.AnimatedEntrance
import com.ucb.whosin.features.login.presentation.WhosInModernTheme
import com.ucb.whosin.ui.components.WhosInPrimaryButton
import com.ucb.whosin.ui.components.WhosInPrimaryButton2
import com.ucb.whosin.ui.theme.WhosInColors
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapPickerScreen(
    locationViewModel: LocationViewModel = koinViewModel(),
    onLocationSelected: () -> Unit = {},
    onBackPressed: () -> Unit
) {
    WhosInModernTheme {
        MapPickerScreenContent(
            locationViewModel = locationViewModel,
            onLocationSelected = onLocationSelected,
            onBackPressed = onBackPressed
        )
    }
}

@Composable
private fun MapPickerScreenContent(
    locationViewModel: LocationViewModel,
    onLocationSelected: () -> Unit,
    onBackPressed: () -> Unit
) {
    // Ubicación por defecto (Cochabamba, Bolivia)
    val defaultLocation = LatLng(-17.38950, -66.15680)

    var selectedLocation by remember { mutableStateOf(defaultLocation) }
    var isInitialized by remember { mutableStateOf(false) }
    var showInfoCard by remember { mutableStateOf(true) }
    var startAnimation by remember { mutableStateOf(false) }

    val context = LocalContext.current

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val savedLocation by locationViewModel.selectedLocation.collectAsState()

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(selectedLocation, 15f)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasLocationPermission = isGranted
        if (isGranted) {
            getCurrentLocation(context) { location ->
                selectedLocation = location
                cameraPositionState.position = CameraPosition.fromLatLngZoom(location, 15f)
            }
        }
    }

    LaunchedEffect(Unit) {
        delay(100)
        startAnimation = true

        if (!isInitialized) {
            savedLocation?.let { (lat, lng) ->
                val location = LatLng(lat, lng)
                selectedLocation = location
                cameraPositionState.position = CameraPosition.fromLatLngZoom(location, 15f)
                isInitialized = true
            } ?: run {
                if (hasLocationPermission) {
                    getCurrentLocation(context) { location ->
                        selectedLocation = location
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(location, 15f)
                        isInitialized = true
                    }
                } else {
                    selectedLocation = defaultLocation
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(defaultLocation, 15f)
                    isInitialized = true
                }
            }
        }
    }

    // Ocultar info card después de 5 segundos
    LaunchedEffect(showInfoCard) {
        if (showInfoCard) {
            delay(5000)
            showInfoCard = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WhosInColors.LightGray)
    ) {
        // Mapa
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = hasLocationPermission
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = false,
                compassEnabled = true
            ),
            onMapClick = { latLng ->
                selectedLocation = latLng
                showInfoCard = true
            }
        ) {
            Marker(
                state = remember(selectedLocation) { MarkerState(position = selectedLocation) },
                title = "Ubicación del evento"
            )
        }

        // Header con botón atrás
        AnimatedEntrance(visible = startAnimation, delayMillis = 0) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                color = WhosInColors.White.copy(alpha = 0.95f),
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBackPressed,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(WhosInColors.LightGray)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "Volver",
                            tint = WhosInColors.DarkTeal
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Seleccionar Ubicación",
                            style = MaterialTheme.typography.titleMedium,
                            color = WhosInColors.DarkTeal,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Toca el mapa para elegir",
                            style = MaterialTheme.typography.bodySmall,
                            color = WhosInColors.GrayBlue
                        )
                    }
                }
            }
        }

        // Info Card con coordenadas (aparece/desaparece)
        AnimatedVisibility(
            visible = showInfoCard,
            enter = fadeIn() + slideInVertically(initialOffsetY = { -it / 2 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { -it / 2 }),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 140.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = WhosInColors.PetrolBlue.copy(alpha = 0.95f),
                shadowElevation = 12.dp,
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(WhosInColors.LimeGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.LocationOn,
                            contentDescription = null,
                            tint = WhosInColors.DarkTeal,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Ubicación seleccionada",
                            style = MaterialTheme.typography.labelMedium,
                            color = WhosInColors.LightGray.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Lat: ${"%.6f".format(selectedLocation.latitude)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = WhosInColors.LightGray,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Lng: ${"%.6f".format(selectedLocation.longitude)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = WhosInColors.LightGray,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Botones flotantes (derecha)
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Botón "Mi ubicación"
            AnimatedEntrance(visible = startAnimation, delayMillis = 100) {
                FloatingActionButton(
                    onClick = {
                        if (hasLocationPermission) {
                            getCurrentLocation(context) { location ->
                                selectedLocation = location
                                cameraPositionState.position = CameraPosition.fromLatLngZoom(location, 15f)
                                showInfoCard = true
                            }
                        } else {
                            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        }
                    },
                    containerColor = WhosInColors.White,
                    contentColor = WhosInColors.DarkTeal,
                    modifier = Modifier
                        .size(56.dp)
                        .shadow(8.dp, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.MyLocation,
                        contentDescription = "Mi ubicación",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Botón de confirmar (abajo)
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(24.dp)
                .navigationBarsPadding()
        ) {
            AnimatedEntrance(visible = startAnimation, delayMillis = 200) {
                WhosInPrimaryButton2(
                    text = "Confirmar Ubicación",
                    onClick = {
                        locationViewModel.setLocation(
                            selectedLocation.latitude,
                            selectedLocation.longitude
                        )
                        onLocationSelected()
                    }
                )
            }
        }

        // Indicador de pulso en el centro del mapa
        PulsingLocationIndicator(
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun PulsingLocationIndicator(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Círculo exterior pulsante
        Box(
            modifier = Modifier
                .size(60.dp)
                .scale(scale)
                .alpha(alpha)
                .clip(CircleShape)
                .background(WhosInColors.DarkTealLight.copy(alpha = 0.3f))
        )

        // Círculo interior fijo
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(WhosInColors.DarkTealLight)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(WhosInColors.LimeGreen)
            )
        }
    }
}

private fun getCurrentLocation(
    context: android.content.Context,
    onLocationReceived: (LatLng) -> Unit
) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    try {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                onLocationReceived(LatLng(location.latitude, location.longitude))
            }
        }
    } catch (e: SecurityException) {
        // Manejar error de permisos
    }
}