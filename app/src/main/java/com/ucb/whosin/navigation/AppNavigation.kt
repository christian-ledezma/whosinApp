package com.ucb.whosin.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ucb.whosin.features.login.presentation.HomeScreen
import com.ucb.whosin.features.login.presentation.LoginScreen
import com.ucb.whosin.features.login.presentation.RegisterScreen
import com.ucb.whosin.features.Guest.presentation.GuestScreen
import com.ucb.whosin.features.event.presentation.EventScreen

import com.ucb.whosin.ui.guard.GuardScreen

@Composable
fun AppNavigation(
    navigationViewModel: NavigationViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    LaunchedEffect(Unit) {
        navigationViewModel.navigationCommand.collect { command ->
            when (command) {
                is NavigationViewModel.NavigationCommand.NavigateTo -> {
                    navController.navigate(command.route) {
                        // Configuración del back stack según sea necesario
                        when (command.options) {
                            NavigationOptions.CLEAR_BACK_STACK -> {
                                popUpTo(0) { inclusive = true }
                            }
                            NavigationOptions.REPLACE_HOME -> {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                            else -> {
                                // Navegación normal
                            }
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
        startDestination = Screen.Login.route,
        modifier = modifier
    ) {
        // Pantallas de autenticación (sin drawer)
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    // Navegar al Home y limpiar el back stack
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
                    // Navegar al login después de registrarse
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // Pantallas con navegación (después del login)
        composable(Screen.Home.route) {
            HomeScreen(
                navigationViewModel = navigationViewModel,
                navController = navController
            )
        }

        composable(Screen.Guard.route) {
            GuardScreen()
        }

        composable(Screen.Guest.route) {
            GuestScreen()
        }

        composable(Screen.Event.route) {
            EventScreen()
        }

        composable(Screen.Staff.route) {
            // StaffScreen()
        }
    }
}