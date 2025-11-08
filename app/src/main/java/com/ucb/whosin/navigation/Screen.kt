package com.ucb.whosin.navigation

sealed class Screen (val route: String) {
    object Register: Screen ("Register")
}