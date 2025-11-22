package com.ucb.whosin.features.Guard.data.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ucb.whosin.navigation.Screen
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuardScreen(navController: NavController, viewModel: GuardViewModel = koinViewModel()) {
    val filteredGuests by viewModel.filteredGuests.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val stats by viewModel.stats.collectAsState()

    val qrCodeResult = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.get<String>("qr_code_result")

    LaunchedEffect(qrCodeResult) {
        if (qrCodeResult != null) {
            viewModel.checkIn(qrCodeResult)
            navController.currentBackStackEntry?.savedStateHandle?.remove<String>("qr_code_result")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Modo Guardia") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Screen.QrScanner.route) }) {
                Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan QR Code")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Spacer(modifier = Modifier.height(16.dp))

                // --- Search Bar ---
                TextField(
                    value = searchQuery,
                    onValueChange = { viewModel.onSearchQueryChange(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Buscar invitado por nombre...") },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // --- Stats ---
                Row {
                    Text("Invitados Dentro: ", fontWeight = FontWeight.SemiBold)
                    Text("${stats.checkedIn} / ${stats.total}")
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            // --- Guest List ---
            GuestList(guests = filteredGuests, onCheckIn = { guestId ->
                viewModel.checkIn(guestId)
            })
        }
    }
}