package com.ucb.whosin.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ucb.whosin.features.Guest.presentation.GuestScreen
import com.ucb.whosin.features.event.presentation.EventScreen

import com.ucb.whosin.ui.guard.GuardScreen

@Composable
fun AppNavigation() {
    val navController: NavHostController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Event.route,
    ) {
        composable(Screen.Guard.route) {
            GuardScreen()
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

    }
}