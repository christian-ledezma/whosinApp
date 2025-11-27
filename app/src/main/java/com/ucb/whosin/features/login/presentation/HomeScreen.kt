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
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigationViewModel: NavigationViewModel,
    navController: NavHostController
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val logoutUseCase: LogoutUseCase = get()
    val scope = rememberCoroutineScope()
    val currentRoute = navController.currentBackStackEntry?.destination?.route ?: ""

    val navigationItems = NavigationDrawer.getAllItems()

    WhosInModernTheme {

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = WhosInColors.White
                ) {
                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = "Who's In",
                        style = MaterialTheme.typography.headlineMedium,
                        color = WhosInColors.DarkTeal,
                        modifier = Modifier.padding(24.dp)
                    )

                    Divider()

                    navigationItems.forEach { item ->
                        NavigationDrawerItem(
                            icon = {
                                Icon(
                                    imageVector = if (currentRoute == item.route)
                                        item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label
                                )
                            },
                            label = { Text(item.label) },
                            selected = currentRoute == item.route,
                            onClick = {
                                scope.launch {
                                    drawerState.close()
                                    if (currentRoute != item.route) {
                                        navigationViewModel.navigateTo(item.route)
                                    }
                                }
                            },
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = WhosInColors.MintGreen.copy(alpha = 0.2f),
                                selectedIconColor = WhosInColors.DarkTeal,
                                selectedTextColor = WhosInColors.DarkTeal
                            ),
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                    }
                }
            }
        ) {

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

                        Text(
                            text = "Bienvenido a Who's In",
                            style = MaterialTheme.typography.headlineLarge,
                            color = WhosInColors.LightGray,
                            fontWeight = FontWeight.Bold
                        )

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

                                Button(
                                    onClick = {
                                        navigationViewModel.navigateTo(NavigationDrawer.Guests.route)
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = WhosInColors.LimeGreen,
                                        contentColor = WhosInColors.DarkTeal
                                    )
                                ) { Text("Ver Invitados") }
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
