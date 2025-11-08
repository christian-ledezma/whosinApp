package com.ucb.whosin.navigation

sealed class Screen(val route: String) {
    object Guard : Screen("guard")
}