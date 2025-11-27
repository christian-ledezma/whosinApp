package com.ucb.whosin.ui.animations

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ucb.whosin.features.Guard.data.presentation.GuardScreen
import com.ucb.whosin.features.Guest.presentation.AcceptInvitationScreen
import com.ucb.whosin.features.Guest.presentation.GuestListScreen
import com.ucb.whosin.features.event.presentation.EventSelectorScreen
import com.ucb.whosin.features.event.presentation.RegisterEventScreen
import com.ucb.whosin.features.login.domain.usecase.CheckSessionUseCase
import com.ucb.whosin.features.login.presentation.HomeScreen
import com.ucb.whosin.features.login.presentation.LoginScreen
import com.ucb.whosin.features.login.presentation.ProfileScreen
import com.ucb.whosin.features.login.presentation.RegisterScreen
import com.ucb.whosin.navigation.NavigationOptions
import com.ucb.whosin.navigation.NavigationViewModel
import com.ucb.whosin.navigation.Screen


/**
 * Transiciones de navegación personalizadas
 */
private const val ANIMATION_DURATION = 400

private val enterTransition: AnimatedContentTransitionScope<*>.() -> EnterTransition = {
    slideInHorizontally(
        initialOffsetX = { fullWidth -> fullWidth / 3 },
        animationSpec = tween(ANIMATION_DURATION, easing = FastOutSlowInEasing)
    ) + fadeIn(animationSpec = tween(ANIMATION_DURATION))
}

private val exitTransition: AnimatedContentTransitionScope<*>.() -> ExitTransition = {
    slideOutHorizontally(
        targetOffsetX = { fullWidth -> -fullWidth / 3 },
        animationSpec = tween(ANIMATION_DURATION, easing = FastOutSlowInEasing)
    ) + fadeOut(animationSpec = tween(ANIMATION_DURATION))
}

private val popEnterTransition: AnimatedContentTransitionScope<*>.() -> EnterTransition = {
    slideInHorizontally(
        initialOffsetX = { fullWidth -> -fullWidth / 3 },
        animationSpec = tween(ANIMATION_DURATION, easing = FastOutSlowInEasing)
    ) + fadeIn(animationSpec = tween(ANIMATION_DURATION))
}

private val popExitTransition: AnimatedContentTransitionScope<*>.() -> ExitTransition = {
    slideOutHorizontally(
        targetOffsetX = { fullWidth -> fullWidth / 3 },
        animationSpec = tween(ANIMATION_DURATION, easing = FastOutSlowInEasing)
    ) + fadeOut(animationSpec = tween(ANIMATION_DURATION))
}

@Composable
fun AppNavigation(
    navigationViewModel: NavigationViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    val checkSessionUseCase: CheckSessionUseCase = org.koin.androidx.compose.get()
    val isLoggedIn by checkSessionUseCase().collectAsState(initial = false)

    LaunchedEffect(Unit) {
        navigationViewModel.navigationCommand.collect { command ->
            when (command) {
                is NavigationViewModel.NavigationCommand.NavigateTo -> {
                    navController.navigate(command.route) {
                        when (command.options) {
                            NavigationOptions.CLEAR_BACK_STACK -> {
                                popUpTo(0) { inclusive = true }
                            }
                            NavigationOptions.REPLACE_HOME -> {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                            else -> {}
                        }
                    }
                }
                is NavigationViewModel.NavigationCommand.PopBackStack -> {
                    navController.popBackStack()
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) Screen.Home.route else Screen.Login.route,
        modifier = modifier,
        // Transiciones globales por defecto
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { it / 3 },
                animationSpec = tween(ANIMATION_DURATION)
            ) + fadeIn(tween(ANIMATION_DURATION))
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { -it / 3 },
                animationSpec = tween(ANIMATION_DURATION)
            ) + fadeOut(tween(ANIMATION_DURATION))
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { -it / 3 },
                animationSpec = tween(ANIMATION_DURATION)
            ) + fadeIn(tween(ANIMATION_DURATION))
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { it / 3 },
                animationSpec = tween(ANIMATION_DURATION)
            ) + fadeOut(tween(ANIMATION_DURATION))
        }
    ) {
        // Login con transición especial (fade suave)
        composable(
            route = Screen.Login.route,
            enterTransition = {
                fadeIn(animationSpec = tween(500))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(300)) +
                        slideOutHorizontally(targetOffsetX = { -it / 4 })
            }
        ) {
            LoginScreen(
                onLoginSuccess = {
                    navigationViewModel.navigateTo(
                        Screen.Home.route,
                        NavigationOptions.CLEAR_BACK_STACK
                    )
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        // Register con slide desde la derecha
        composable(
            route = Screen.Register.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(ANIMATION_DURATION, easing = FastOutSlowInEasing)
                ) + fadeIn(tween(ANIMATION_DURATION))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(ANIMATION_DURATION, easing = FastOutSlowInEasing)
                ) + fadeOut(tween(ANIMATION_DURATION))
            }
        ) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // Home con scale transition (entrada especial después de login)
        composable(
            route = Screen.Home.route,
            enterTransition = {
                scaleIn(
                    initialScale = 0.9f,
                    animationSpec = tween(400)
                ) + fadeIn(tween(400))
            }
        ) {
            HomeScreen(
                navigationViewModel = navigationViewModel,
                navController = navController
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Event.route) {
            EventSelectorScreen(
                onEventSelected = { eventId ->
                    navController.navigate("guest/$eventId")
                },
                onManageEventClicked = { eventId ->
                    navController.navigate(Screen.Guard.createRoute(eventId))
                },
                onNavigateToCreateEvent = {
                    navController.navigate("create_event")
                }
            )
        }

        composable(Screen.Guest.route) {
            EventSelectorScreen(
                onEventSelected = { eventId ->
                    navController.navigate("guest/$eventId")
                },
                onManageEventClicked = { eventId ->
                    navController.navigate(Screen.Guard.createRoute(eventId))
                },
                onNavigateToCreateEvent = {
                    navController.navigate("create_event")
                }
            )
        }

        // Create event con slide desde abajo
        composable(
            route = "create_event",
            enterTransition = {
                slideInVertically(
                    initialOffsetY = { it / 2 },
                    animationSpec = tween(ANIMATION_DURATION)
                ) + fadeIn(tween(ANIMATION_DURATION))
            },
            popExitTransition = {
                slideOutVertically(
                    targetOffsetY = { it / 2 },
                    animationSpec = tween(ANIMATION_DURATION)
                ) + fadeOut(tween(ANIMATION_DURATION))
            }
        ) { backStackEntry ->
            // AÑADIR: Obtener ViewModel desde este NavBackStackEntry
            val locationViewModel = org.koin.androidx.compose.koinViewModel<com.ucb.whosin.features.event.presentation.LocationViewModel>(
                viewModelStoreOwner = backStackEntry
            )
            RegisterEventScreen(
                locationViewModel = locationViewModel,  // AÑADIR este parámetro
                onNavigateToMapPicker = { navController.navigate("map_picker") },  // AÑADIR este parámetro
                onRegisterSuccess = {
                    navController.popBackStack()
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "map_picker",
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(ANIMATION_DURATION)
                ) + fadeIn(tween(ANIMATION_DURATION))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(ANIMATION_DURATION)
                ) + fadeOut(tween(ANIMATION_DURATION))
            }
        ) { backStackEntry ->
            // Obtener el MISMO ViewModel desde el parent (create_event)
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("create_event")
            }
            val locationViewModel = org.koin.androidx.compose.koinViewModel<com.ucb.whosin.features.event.presentation.LocationViewModel>(
                viewModelStoreOwner = parentEntry
            )

            com.ucb.whosin.features.event.presentation.MapPickerScreen(
                locationViewModel = locationViewModel,
                onBackPressed = { navController.popBackStack() },
                onLocationSelected = { navController.popBackStack() }
            )
        }

        composable(
            route = "guest/{eventId}",
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) {
            GuestListScreen()
        }

        composable(Screen.AcceptInvitation.route) {
            AcceptInvitationScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}