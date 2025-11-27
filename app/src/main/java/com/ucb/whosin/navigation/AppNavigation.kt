package com.ucb.whosin.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ucb.whosin.features.Guest.presentation.AcceptInvitationScreen
import com.ucb.whosin.features.event.presentation.EventSelectorScreen
import com.ucb.whosin.features.Guest.presentation.GuestListScreen
import com.ucb.whosin.features.login.presentation.HomeScreen
import com.ucb.whosin.features.login.presentation.LoginScreen
import com.ucb.whosin.features.login.presentation.RegisterScreen
import com.ucb.whosin.features.event.presentation.RegisterEventScreen
import com.ucb.whosin.features.login.domain.usecase.CheckSessionUseCase
import com.ucb.whosin.features.login.presentation.ProfileScreen
import com.ucb.whosin.features.Guard.data.presentation.GuardScreen
import com.ucb.whosin.features.event.presentation.LocationViewModel
import com.ucb.whosin.features.event.presentation.MapPickerScreen
import com.ucb.whosin.features.qrscanner.ui.QrScannerScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavigation(
    navigationViewModel: NavigationViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    val checkSessionUseCase: CheckSessionUseCase = org.koin.androidx.compose.get()
    val isLoggedIn by checkSessionUseCase().collectAsState(initial = false)

    LaunchedEffect(Unit) {
        navigationViewModel.navigationCommand.collect { command ->
            when (command) {
                is NavigationViewModel.NavigationCommand.NavigateTo -> {
                    navController.navigate(command.route) {
                        when (command.options) {
                            NavigationOptions.CLEAR_BACK_STACK -> {
                                popUpTo(0) { inclusive = true }
                            }
                            NavigationOptions.REPLACE_HOME -> {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                            else -> {}
                        }
                    }
                }
                is NavigationViewModel.NavigationCommand.PopBackStack -> {
                    navController.popBackStack()
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) Screen.Home.route else Screen.Login.route,
        modifier = modifier
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navigationViewModel.navigateTo(
                        Screen.Home.route,
                        NavigationOptions.CLEAR_BACK_STACK
                    )
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                navigationViewModel = navigationViewModel,
                navController = navController
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.Guard.route,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) {
            GuardScreen(navController)
        }

        // Pantalla "Mis Eventos"
        composable(Screen.Event.route) {
            EventSelectorScreen(
                onEventSelected = { eventId ->
                    navController.navigate("guest/$eventId")
                },
                onManageEventClicked = { eventId ->
                    navController.navigate(Screen.Guard.createRoute(eventId))
                },
                onNavigateToCreateEvent = {
                    navController.navigate("create_event")
                }
            )
        }

        // Ruta para que el invitado vea a qué evento puede unirse
        composable(Screen.Guest.route) {
            EventSelectorScreen(
                onEventSelected = { eventId ->
                    navController.navigate("guest/$eventId")
                },
                onManageEventClicked = { eventId ->
                    navController.navigate(Screen.Guard.createRoute(eventId))
                },
                onNavigateToCreateEvent = {
                    navController.navigate("create_event")
                }
            )
        }

        // Pantalla para crear un evento
        composable("create_event") {
            val locationViewModel = koinViewModel<LocationViewModel>(viewModelStoreOwner = it)

            RegisterEventScreen(
                locationViewModel = locationViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToMapPicker = {
                    navController.navigate("map_picker")
                }
            )
        }

        // Pantalla del selector de mapa
        composable("map_picker") {
            val parentEntry = remember(it) {
                navController.getBackStackEntry("create_event")
            }
            val locationViewModel = koinViewModel<LocationViewModel>(
                viewModelStoreOwner = parentEntry
            )

            MapPickerScreen(
                locationViewModel = locationViewModel,
                onBackPressed = { navController.popBackStack() },
                onLocationSelected = { navController.popBackStack() }
            )
        }

        // Pantalla de invitados con eventId
        composable(
            route = "guest/{eventId}",
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) {
            GuestListScreen()
        }

        // Pantalla para aceptar invitación
        composable(Screen.AcceptInvitation.route) {
            AcceptInvitationScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.QrScanner.route) {
            QrScannerScreen(navController)
        }
    }

}