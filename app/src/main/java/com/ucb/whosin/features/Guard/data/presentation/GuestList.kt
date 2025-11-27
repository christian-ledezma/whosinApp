package com.ucb.whosin.features.Guard.data.presentation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ucb.whosin.features.Guard.data.model.Guest

@Composable
fun GuestList(guests: List<Guest>, onCheckIn: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier,
        contentPadding = PaddingValues(horizontal = 24.dp)
    ) {
        items(guests, key = { it.userId ?: it.name }) {
            guest ->
            GuestListItem(guest = guest, onCheckIn = onCheckIn)
        }
    }
}
