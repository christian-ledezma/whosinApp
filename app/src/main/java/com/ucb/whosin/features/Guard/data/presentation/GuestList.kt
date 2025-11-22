package com.ucb.whosin.features.Guard.data.presentation

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import com.ucb.whosin.features.Guard.data.model.Guest

@Composable
fun GuestList(guests: List<Guest>, onCheckIn: (String) -> Unit) {
    LazyColumn {
        items(guests, key = { it.userId ?: it.name }) {
            guest ->
            GuestListItem(guest = guest, onCheckIn = onCheckIn)
        }
    }
}