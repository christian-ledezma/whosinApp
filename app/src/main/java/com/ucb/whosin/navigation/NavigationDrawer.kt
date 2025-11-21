package com.ucb.whosin.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavigationDrawer(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: String) {
    object Home : NavigationDrawer(
        label = "Inicio",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        route = Screen.Home.route
    )

    object Events : NavigationDrawer(
        label = "Eventos",
        selectedIcon = Icons.Filled.DateRange,
        unselectedIcon = Icons.Outlined.DateRange,
        route = Screen.Event.route // <- Esta es la ruta que corregimos
    )

    object Guests : NavigationDrawer(
        label = "Invitados",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person,
        route = Screen.Guest.route
    )

    object Guard : NavigationDrawer(
        label = "Guardia",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person,
        route = Screen.Guard.route
    )

    object Staff : NavigationDrawer(
        label = "Staff",
        selectedIcon = Icons.Filled.AccountCircle,
        unselectedIcon = Icons.Outlined.AccountCircle,
        route = Screen.Staff.route
    )

    companion object {
        fun getAllItems() = listOf(
            Home,
            Events,
            Guests,
            // Ocultamos Guardia y Staff del menú principal por ahora
            // Guard,
            // Staff
        )
    }
}