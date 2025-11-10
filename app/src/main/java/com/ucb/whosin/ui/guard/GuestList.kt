package com.ucb.whosin.ui.guard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ucb.whosin.features.Guard.data.model.Guest

@Composable
fun GuestList(guests: List<Guest>, onCheckIn: (String) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }

    Column {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search Guests") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        val filteredGuests = guests.filter {
            it.name.contains(searchQuery, ignoreCase = true)
        }

        LazyColumn {
            items(filteredGuests, key = { it.userId ?: it.name }) {
                guest -> GuestListItem(guest = guest, onCheckIn = onCheckIn)
            }
        }
    }
}