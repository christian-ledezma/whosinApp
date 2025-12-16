package com.ucb.whosin.features.login.presentation

import androidx.compose.animation.core.*
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
import com.ucb.whosin.ui.components.BottomNavItem
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import com.ucb.whosin.ui.components.ModernTopAppBar
import com.ucb.whosin.ui.components.WhosInBottomNavigation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigationViewModel: NavigationViewModel,
    navController: NavHostController
) {
    val logoutUseCase: LogoutUseCase = get()
    val getCurrentUserUseCase: GetCurrentUserUseCase = get()
    val scope = rememberCoroutineScope()
    val currentRoute = navController.currentBackStackEntry?.destination?.route ?: ""

    val userName by getCurrentUserUseCase().collectAsState(initial = null)

    val bottomNavItems = remember {
        listOf(
            BottomNavItem(
                route = Screen.Home.route,
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Outlined.Home,
                label = "Inicio"
            ),
            BottomNavItem(
                route = Screen.Event.route,
                selectedIcon = Icons.Filled.DateRange,
                unselectedIcon = Icons.Outlined.DateRange,
                label = "Eventos"
            ),
            BottomNavItem(
                route = Screen.AcceptInvitation.route,
                selectedIcon = Icons.Filled.CheckCircle,
                unselectedIcon = Icons.Outlined.CheckCircle,
                label = "Invitación"
            ),
            BottomNavItem(
                route = Screen.GuardEvents.route,
                selectedIcon = Icons.Filled.Security,
                unselectedIcon = Icons.Outlined.Security,
                label = "Guardia"
            )
        )
    }

    WhosInModernTheme {
            Scaffold(
                containerColor = WhosInColors.DarkTeal,
                topBar = {
                    ModernTopAppBar(
                        title = "Inicio",
                        userName = userName,
                        onProfileClick = {
                            navController.navigate(Screen.Profile.route)
                        },
                        onLogoutClick = {
                            scope.launch {
                                logoutUseCase()
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }
                    )
                },
                bottomBar = {
                    WhosInBottomNavigation(
                        items = bottomNavItems,
                        currentRoute = currentRoute,
                        onNavigate = { route ->
                            if (route != currentRoute) {
                                navigationViewModel.navigateTo(route)
                            }
                        }
                    )
                })
            { paddingValues ->
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
                                text = "Bienvenid@",
                                style = MaterialTheme.typography.headlineMedium,
                                color = WhosInColors.GrayBlue,
                                fontWeight = FontWeight.Normal
                            )

                            Spacer(Modifier.height(12.dp))

                            Text(
                                text = userName ?: "Invitado",
                                style = MaterialTheme.typography.headlineLarge,
                                color = WhosInColors.LightGray,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(Modifier.height(24.dp))

                        // Card de otros accesos
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
                                    text = "Otros Accesos",
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
                                ) { Text("Ver Eventos Aceptados") }

                                Spacer(Modifier.height(12.dp))
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
