package com.ucb.whosin.features.Guard.data.presentation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ucb.whosin.features.login.presentation.WhosInModernTheme
import com.ucb.whosin.navigation.Screen
import com.ucb.whosin.ui.components.WhosInTextField
import com.ucb.whosin.ui.theme.WhosInColors
import org.koin.androidx.compose.koinViewModel

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

    WhosInModernTheme {
        Scaffold(
            containerColor = WhosInColors.DarkTeal,
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navController.navigate(Screen.QrScanner.route) },
                    containerColor = WhosInColors.LimeGreen,
                    contentColor = WhosInColors.DarkTeal
                ) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan QR Code")
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                RegisterEventDecorationCircles() // Background decorations

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(WhosInColors.White)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowBack,
                                contentDescription = "Volver",
                                tint = WhosInColors.DarkTeal
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Text(
                            text = "Modo Guardia",
                            style = MaterialTheme.typography.headlineMedium,
                            color = WhosInColors.LightGray,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Content
                    Column(
                        modifier = Modifier.padding(horizontal = 24.dp)
                    ) {
                        // --- Search Bar ---
                        WhosInTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.onSearchQueryChange(it) },
                            label = "Buscar invitado",
                            placeholder = "Buscar por nombre...",
                            leadingIcon = Icons.Outlined.Search,
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // --- Stats ---
                        Row {
                            Text(
                                "Invitados Dentro: ",
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.bodyLarge,
                                color = WhosInColors.LightGray
                            )
                            Text(
                                "${stats.checkedIn} / ${stats.total}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = WhosInColors.LimeGreen
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // --- Guest List ---
                    GuestList(guests = filteredGuests, onCheckIn = { guestId ->
                        viewModel.checkIn(guestId)
                    })

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

// Copied from RegisterEventScreen.kt
@Composable
private fun RegisterEventDecorationCircles() {
    val infiniteTransition = rememberInfiniteTransition(label = "guardDecor")

    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset"
    )

    Box(
        modifier = Modifier
            .offset(x = (290 + offset).dp, y = (70 - offset).dp)
            .size(130.dp)
            .alpha(0.1f)
            .clip(CircleShape)
            .background(WhosInColors.LimeGreen)
    )

    Box(
        modifier = Modifier
            .offset(x = (-40 - offset).dp, y = (450 + offset).dp)
            .size(110.dp)
            .alpha(0.08f)
            .clip(CircleShape)
            .background(WhosInColors.OliveGreen)
    )
}
