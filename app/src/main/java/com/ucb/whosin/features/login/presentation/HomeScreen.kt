package com.ucb.whosin.features.login.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ucb.whosin.navigation.NavigationDrawer
import com.ucb.whosin.navigation.NavigationViewModel
import com.ucb.whosin.navigation.Screen
import kotlinx.coroutines.launch
import com.ucb.whosin.ui.theme.WhosInColors
import com.ucb.whosin.features.login.domain.usecase.LogoutUseCase
import org.koin.androidx.compose.get
import androidx.compose.animation.core.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.ucb.whosin.features.login.domain.usecase.GetCurrentUserUseCase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigationViewModel: NavigationViewModel,
    navController: NavHostController
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val logoutUseCase: LogoutUseCase = get()
    val getCurrentUserUseCase: GetCurrentUserUseCase = get() // ← AGREGAR
    val scope = rememberCoroutineScope()
    val currentRoute = navController.currentBackStackEntry?.destination?.route ?: ""

    val userName by getCurrentUserUseCase().collectAsState(initial = null)
    val navigationItems = NavigationDrawer.getAllItems()

    WhosInModernTheme {

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    modifier = Modifier
                        .fillMaxWidth(0.78f)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    WhosInColors.DarkTeal.copy(alpha = 0.92f),
                                    WhosInColors.PetrolBlue.copy(alpha = 0.96f)
                                )
                            )
                        )
                ) {

                    Spacer(modifier = Modifier.height(32.dp))

                    // Header del Drawer
                    Column(
                        modifier = Modifier.padding(horizontal = 24.dp)
                    ) {
                        Text(
                            text = "Who's In",
                            style = MaterialTheme.typography.headlineMedium,
                            color = WhosInColors.DarkTeal,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        HorizontalDivider(
                            color = WhosInColors.LightGray.copy(alpha = 0.2f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    navigationItems.forEach { item ->

                        val selected = currentRoute == item.route
                        val animatedAlpha by animateFloatAsState(
                            targetValue = if (selected) 1f else 0.55f,
                            animationSpec = tween(300),
                            label = "alphaAnim"
                        )

                        NavigationDrawerItem(
                            icon = {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label,
                                    tint = WhosInColors.DarkTeal,
                                    modifier = Modifier.size(26.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = item.label,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = WhosInColors.DarkTeal,
                                    modifier = Modifier.alpha(animatedAlpha)
                                )
                            },
                            selected = selected,
                            onClick = {
                                scope.launch {
                                    drawerState.close()
                                    if (!selected) navigationViewModel.navigateTo(item.route)
                                }
                            },
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (selected)
                                        WhosInColors.LimeGreen.copy(alpha = 0.15f)
                                    else
                                        Color.Transparent
                                )
                                .padding(horizontal = 8.dp)
                        )
                    }
                }
            }
        )
         {

            Scaffold(
                containerColor = WhosInColors.DarkTeal,
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                "Inicio",
                                color = WhosInColors.DarkTeal
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                scope.launch { drawerState.open() }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Menú",
                                    tint = WhosInColors.DarkTeal
                                )
                            }
                        },
                        actions = {
                            TextButton(
                                onClick = {
                                    scope.launch {
                                        logoutUseCase()
                                        navController.navigate(Screen.Login.route) {
                                            popUpTo(0) { inclusive = true }
                                        }
                                    }
                                }
                            ) {
                                Text("Cerrar Sesión", color = WhosInColors.DarkTeal)
                            }
                        }
                    )
                }
            ) { paddingValues ->

                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    HomeDecorationCircles()

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                    ) {

                        Spacer(Modifier.height(16.dp))

                        Column {
                            Text(
                                text = "Bienvenido",
                                style = MaterialTheme.typography.headlineMedium,
                                color = WhosInColors.GrayBlue,
                                fontWeight = FontWeight.Normal
                            )

                            Text(
                                text = userName ?: "Invitado",
                                style = MaterialTheme.typography.headlineLarge,
                                color = WhosInColors.LightGray,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        Text(
                            text = "Usa el menú lateral para navegar entre las secciones.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = WhosInColors.GrayBlue
                        )

                        Spacer(Modifier.height(32.dp))

                        // Card de accesos rápidos
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = WhosInColors.White,
                            shadowElevation = 6.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp)
                            ) {
                                Text(
                                    text = "Accesos Rápidos",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = WhosInColors.DarkTeal
                                )

                                Spacer(Modifier.height(12.dp))

                                Button(
                                    onClick = {
                                        navigationViewModel.navigateTo(NavigationDrawer.Events.route)
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = WhosInColors.MintGreen,
                                        contentColor = WhosInColors.DarkTeal
                                    )
                                ) { Text("Ver Eventos") }

                                Spacer(Modifier.height(12.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeDecorationCircles() {
    val infiniteTransition = rememberInfiniteTransition()

    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 18f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Circle 1
    Box(
        modifier = Modifier
            .offset(x = (280 + offset).dp, y = (60 - offset).dp)
            .size(120.dp)
            .alpha(0.12f)
            .clip(CircleShape)
            .background(WhosInColors.LimeGreen)
    )

    // Circle 2
    Box(
        modifier = Modifier
            .offset(x = (-30 - offset).dp, y = (450 + offset).dp)
            .size(110.dp)
            .alpha(0.08f)
            .clip(CircleShape)
            .background(WhosInColors.PetrolBlue)
    )
}
