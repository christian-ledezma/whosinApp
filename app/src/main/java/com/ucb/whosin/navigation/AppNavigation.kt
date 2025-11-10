package com.ucb.whosin.navigation

import androidx.compose.runtime.Composable
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
fun AppNavigation() {
    val navController: NavHostController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Guard.route) {
            GuardScreen()
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

        composable(Screen.Guest.route) {
            GuestScreen()
        }
        composable(Screen.Staff.route) {
            // StaffScreen()
        }
        composable(Screen.Event.route) {
            EventScreen()
        }

        composable(Screen.Home.route) {
            HomeScreen()
        }
    }
}