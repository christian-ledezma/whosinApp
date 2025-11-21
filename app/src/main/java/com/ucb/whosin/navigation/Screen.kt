package com.ucb.whosin.navigation

sealed class Screen (val route: String) {
    object Login : Screen("Login")
    object Register: Screen ("Register")
    object Home : Screen("home")
    object Guard : Screen("guard/{eventId}") {
        fun createRoute(eventId: String) = "guard/$eventId"
    }
    object Staff : Screen("staff")
    object Guest : Screen("guest")
    object Event : Screen("event")
}