package com.ucb.whosin.features.Guard.data.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ucb.whosin.features.Guard.data.model.Guest
import com.ucb.whosin.ui.theme.WhosInColors

@Composable
fun GuestListItem(guest: Guest, onCheckIn: (String) -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        color = WhosInColors.White,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = guest.name,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge,
                    color = WhosInColors.DarkTeal
                )
                Spacer(modifier = Modifier.height(4.dp))

                val statusText: String
                val statusColor = if (guest.checkedIn) {
                    statusText = "Verificado"
                    WhosInColors.Success
                } else {
                    statusText = (guest.inviteStatus ?: "Pendiente").replaceFirstChar { it.uppercase() }
                    WhosInColors.GrayBlue
                }

                Text(
                    text = statusText,
                    color = statusColor,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            guest.guestId?.let {
                Button(
                    onClick = { onCheckIn(it) },
                    enabled = !guest.checkedIn,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WhosInColors.LimeGreen,
                        contentColor = WhosInColors.DarkTeal,
                        disabledContainerColor = WhosInColors.LightGray,
                        disabledContentColor = WhosInColors.GrayBlue
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = if (guest.checkedIn) "Hecho" else "Verificar",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
