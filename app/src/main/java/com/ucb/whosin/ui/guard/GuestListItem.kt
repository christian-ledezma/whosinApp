package com.ucb.whosin.ui.guard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ucb.whosin.features.Guard.data.model.Guest

@Composable
fun GuestListItem(guest: Guest, onCheckIn: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = guest.name, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))

                // Lógica de estado corregida
                val statusText: String
                val statusColor: Color

                if (guest.checkedIn) {
                    statusText = "Checked In"
                    statusColor = Color(0xFF34A853) // Verde
                } else {
                    statusText = (guest.inviteStatus ?: "Pending").replaceFirstChar { it.uppercase() }
                    statusColor = Color.Gray
                }

                Text(
                    text = statusText,
                    color = statusColor,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Usar el guestId (ID del documento) que ahora obtenemos del repositorio
            guest.guestId?.let {
                Button(
                    onClick = { onCheckIn(it) },
                    enabled = !guest.checkedIn, // El botón se deshabilita si ya se hizo check-in
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (guest.checkedIn) Color.LightGray else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(text = if (guest.checkedIn) "Hecho" else "Check-in")
                }
            }
        }
    }
}