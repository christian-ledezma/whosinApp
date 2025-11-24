package com.ucb.whosin.features.event.presentation

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapPickerScreen(
    locationViewModel: LocationViewModel = koinViewModel(),
    onLocationSelected: () -> Unit = {},
    onBackPressed: () -> Unit
) {
    // Ubicación por defecto (Cochabamba)
    val defaultLocation = LatLng(-17.38950, -66.15680)

    var selectedLocation by remember { mutableStateOf(defaultLocation)}
    var isInitialized by remember { mutableStateOf(false) }

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
        if (!isInitialized) {
            savedLocation?.let { (lat, lng) ->
                // Si hay ubicación guardada, usarla
                val location = LatLng(lat, lng)
                selectedLocation = location
                cameraPositionState.position = CameraPosition.fromLatLngZoom(location, 15f)
                isInitialized = true
            } ?: run {
                // Si no hay ubicación guardada, intentar obtener ubicación actual
                if (hasLocationPermission) {
                    getCurrentLocation(context) { location ->
                        selectedLocation = location
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(location, 15f)
                        isInitialized = true
                    }
                } else {
                    // Si no hay permisos, usar Cochabamba
                    selectedLocation = defaultLocation
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(defaultLocation, 15f)
                    isInitialized = true
                }
            }
        }
    }

    LaunchedEffect(savedLocation) {
        if (isInitialized) {
            savedLocation?.let { (lat, lng) ->
                val location = LatLng(lat, lng)
                selectedLocation = location
                cameraPositionState.position = CameraPosition.fromLatLngZoom(location, 15f)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Seleccionar ubicación") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF5E6FA3),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Botón para ir a mi ubicación
                    FloatingActionButton(
                        onClick = {
                            if (hasLocationPermission) {
                                getCurrentLocation(context) { location ->
                                    selectedLocation = location
                                    cameraPositionState.position = CameraPosition.fromLatLngZoom(location, 15f)
                                }
                            } else {
                                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                            }
                        },
                        containerColor = Color.White,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Icon(
                            Icons.Default.MyLocation,
                            contentDescription = "Mi ubicación",
                            tint = Color(0xFF5E6FA3)
                        )
                    }

                    // Botón de confirmar (ahora centrado)
                    ExtendedFloatingActionButton(
                        onClick = {
                            locationViewModel.setLocation(
                                selectedLocation.latitude,
                                selectedLocation.longitude
                            )
                            onLocationSelected()
                        },
                        containerColor = Color(0xFF5E6FA3),
                        contentColor = Color.White,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Text("Confirmar ubicación")
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMapClick = { latLng ->
                    selectedLocation = latLng
                }
            ) {
                Marker(
                    state = remember(selectedLocation) { MarkerState(position = selectedLocation) },
                    title = "Ubicación seleccionada"
                )
            }

            // Info card
            Card(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.9f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Toca el mapa para seleccionar",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Lat: ${"%.6f".format(selectedLocation.latitude)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        text = "Lng: ${"%.6f".format(selectedLocation.longitude)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
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
        // Handle permission error
    }
}