package com.ucb.whosin.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ucb.whosin.features.Guest.presentation.GuestListScreen
import com.ucb.whosin.features.login.presentation.HomeScreen
import com.ucb.whosin.features.login.presentation.LoginScreen
import com.ucb.whosin.features.login.presentation.RegisterScreen

import com.ucb.whosin.features.event.presentation.RegisterEventScreen
import com.ucb.whosin.features.login.domain.usecase.CheckSessionUseCase

import com.ucb.whosin.ui.guard.GuardScreen

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
                        // Configuración del back stack según sea necesario
                        when (command.options) {
                            NavigationOptions.CLEAR_BACK_STACK -> {
                                popUpTo(0) { inclusive = true }
                            }
                            NavigationOptions.REPLACE_HOME -> {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                            else -> {

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
        startDestination = if (isLoggedIn) Screen.Home.route else Screen.Login.route,
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

        composable(Screen.Home.route) {
            HomeScreen(
                navigationViewModel = navigationViewModel,
                navController = navController
            )
        }

        composable(Screen.Guest.route) {
            GuestListScreen()
        }



        composable(Screen.Event.route) {
            RegisterEventScreen()
        }

        composable(Screen.Staff.route) {
            // StaffScreen()
        }
    }
}