package com.ucb.whosin.features.Guard.data.presentation.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import com.ucb.whosin.features.event.domain.model.EventModel

@Composable
fun EventList(events: List<EventModel>, onEventClick: (String) -> Unit) {
    LazyColumn {
        items(events) { event ->
            EventListItem(event = event, onEventClick = onEventClick)
        }
    }
}
