package com.ucb.whosin.navigation

sealed class Screen (val route: String) {
    object Login : Screen("Login")
    object Register: Screen ("Register")
    object Home : Screen("home")
    object Guard : Screen("guard/{eventId}") {
        fun createRoute(eventId: String) = "guard/$eventId"
    }
    object Guest : Screen("guest")
    object Event : Screen("event")
    object AcceptInvitation : Screen("accept_invitation")
    object QrScanner : Screen("qr_scanner")
    object Profile : Screen("profile")
    object MapPicker : Screen("map_picker/{latitude}/{longitude}") {
        fun createRoute(latitude: Double?, longitude: Double?): String {
            return if (latitude != null && longitude != null) {
                "map_picker/$latitude/$longitude"
            } else {
                "map_picker/null/null"
            }
        }
    }
}