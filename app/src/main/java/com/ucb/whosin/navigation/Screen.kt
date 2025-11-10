package com.ucb.whosin.navigation

sealed class Screen (val route: String) {
    object Login : Screen("Login")
    object Register: Screen ("Register")
    object Home : Screen("home")
    object Guard : Screen("guard")
    object Staff : Screen("staff")
    object Guest : Screen("guest")
    object Event : Screen("event")
}