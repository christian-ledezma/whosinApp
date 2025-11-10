package com.ucb.whosin.navigation

sealed class Screen(val route: String) {
    object Guard : Screen("guard")
    object Staff : Screen("staff")

    object Guest : Screen("guest")

    object Event : Screen("event")
}