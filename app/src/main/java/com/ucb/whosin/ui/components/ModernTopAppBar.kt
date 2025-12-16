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
    onGuardModeClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

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
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = WhosInColors.DarkTeal
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

            // Menú de opciones
            Box {
                IconButton(
                    onClick = { showMenu = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Más opciones",
                        tint = WhosInColors.DarkTeal
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier
                        .background(WhosInColors.White)
                        .padding(vertical = 4.dp)
                ) {
                    // Opción Modo Guardia
                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Security,
                                    contentDescription = null,
                                    tint = WhosInColors.DarkTeal,
                                    modifier = Modifier.size(22.dp)
                                )
                                Text(
                                    "Modo Guardia",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = WhosInColors.DarkTeal
                                )
                            }
                        },
                        onClick = {
                            showMenu = false
                            onGuardModeClick()
                        }
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = WhosInColors.GrayBlue.copy(alpha = 0.2f)
                    )

                    // Opción Cerrar Sesión
                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Logout,
                                    contentDescription = null,
                                    tint = WhosInColors.Error,
                                    modifier = Modifier.size(22.dp)
                                )
                                Text(
                                    "Cerrar Sesión",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = WhosInColors.Error
                                )
                            }
                        },
                        onClick = {
                            showMenu = false
                            onLogoutClick()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = WhosInColors.White,
            titleContentColor = WhosInColors.DarkTeal
        ),
        modifier = modifier
    )
}