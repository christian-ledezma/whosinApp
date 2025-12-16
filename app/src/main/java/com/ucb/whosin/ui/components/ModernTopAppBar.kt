package com.ucb.whosin.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ucb.whosin.ui.theme.WhosInColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernTopAppBar(
    title: String,
    userName: String?,
    onProfileClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    var showLogoutDialog by remember { mutableStateOf(false) }

    // Animación del avatar al hacer hover (simulado con scale)
    var isAvatarPressed by remember { mutableStateOf(false) }
    val avatarScale by animateFloatAsState(
        targetValue = if (isAvatarPressed) 0.9f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "avatarScale"
    )

    TopAppBar(
        title = {
            Text(
                text = title,
                fontSize = 24.sp,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = WhosInColors.LightGray
            )
        },
        actions = {
            // Avatar del usuario
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .scale(avatarScale)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                WhosInColors.DarkTeal,
                                WhosInColors.PetrolBlue
                            )
                        )
                    )
                    .clickable {
                        isAvatarPressed = true
                        onProfileClick()
                    }
                    .padding(2.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userName?.firstOrNull()?.uppercase() ?: "?",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = WhosInColors.LimeGreen
                )
            }

            // Reset animation
            LaunchedEffect(isAvatarPressed) {
                if (isAvatarPressed) {
                    kotlinx.coroutines.delay(100)
                    isAvatarPressed = false
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Botón de cerrar sesión
            IconButton(
                onClick = { showLogoutDialog = true }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Logout,
                    contentDescription = "Cerrar Sesión",
                    tint = WhosInColors.LightGray
                )
            }

            // Modal de confirmación
            if (showLogoutDialog) {
                AlertDialog(
                    onDismissRequest = { showLogoutDialog = false },
                    title = {
                        Text(
                            text = "Cerrar Sesión",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    text = {
                        Text(
                            text = "¿Estás seguro de que deseas cerrar sesión?",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    confirmButton = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { showLogoutDialog = false },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = WhosInColors.LightGray,
                                    contentColor = WhosInColors.DarkTeal
                                ),
                                modifier = Modifier.weight(1f).padding(end = 4.dp)
                            ) {
                                Text(
                                    "Cancelar",
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Button(
                                onClick = {
                                    showLogoutDialog = false
                                    onLogoutClick()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = WhosInColors.Error,
                                    contentColor = WhosInColors.White
                                ),
                                modifier = Modifier.weight(1f).padding(start = 4.dp)
                            ) {
                                Text(
                                    "Cerrar Sesión",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    },
                    dismissButton = null,

                    containerColor = WhosInColors.LightGray
                )
            }

            Spacer(modifier = Modifier.width(8.dp))
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = WhosInColors.DarkTealLight,
            titleContentColor = WhosInColors.LightGray
        ),
        modifier = modifier
    )
}