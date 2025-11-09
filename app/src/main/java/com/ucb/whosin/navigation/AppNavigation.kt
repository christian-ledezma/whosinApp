package com.ucb.whosin.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ucb.whosin.features.Guest.presentation.GuestScreen

import com.ucb.whosin.ui.guard.GuardScreen

@Composable
fun AppNavigation(modifier: Modifier, navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Guest.route,
        modifier = modifier
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

    }
}