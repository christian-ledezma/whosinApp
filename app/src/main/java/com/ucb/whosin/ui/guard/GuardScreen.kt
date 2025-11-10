package com.ucb.whosin.ui.guard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuardScreen(viewModel: GuardViewModel = koinViewModel()) {
    val guests by viewModel.guests.collectAsState()
    var tabIndex by remember { mutableStateOf(2) } // Staff tab is selected by default
    val tabs = listOf("Event", "Guests", "Staff")

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Manage Event") })
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            TabRow(selectedTabIndex = tabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(text = { Text(title) },
                        selected = tabIndex == index,
                        onClick = { tabIndex = index })
                }
            }
            when (tabIndex) {
                0 -> { /* Event details screen */ }
                1 -> { /* Guests screen - Now empty */ }
                2 -> {
                    GuestList(guests = guests, onCheckIn = {
                        guestId -> viewModel.checkIn(guestId)
                    })
                }
            }
        }
    }
}