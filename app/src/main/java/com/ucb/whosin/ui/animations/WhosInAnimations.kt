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



/**
 * Animación de entrada con fade y scale para elementos
 */
@Composable
fun AnimatedEntrance(
    visible: Boolean = true,
    delayMillis: Int = 0,
    content: @Composable () -> Unit
) {
    var startAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(visible) {
        kotlinx.coroutines.delay(delayMillis.toLong())
        startAnimation = visible
    }

    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 500,
            easing = FastOutSlowInEasing
        ),
        label = "alpha"
    )

    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.92f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    val offsetY by animateDpAsState(
        targetValue = if (startAnimation) 0.dp else 20.dp,
        animationSpec = tween(
            durationMillis = 500,
            easing = FastOutSlowInEasing
        ),
        label = "offsetY"
    )

    Box(
        modifier = Modifier
            .alpha(alpha)
            .scale(scale)
    ) {
        content()
    }
}

/**
 * Animación staggered para listas de elementos
 */
@Composable
fun StaggeredAnimatedItem(
    index: Int,
    baseDelayMillis: Int = 50,
    content: @Composable () -> Unit
) {
    AnimatedEntrance(
        delayMillis = index * baseDelayMillis,
        content = content
    )
}

/**
 * Animación de shake para errores
 */
@Composable
fun ShakeAnimation(
    trigger: Boolean,
    content: @Composable () -> Unit
) {
    val shakeOffset by animateFloatAsState(
        targetValue = 0f,
        animationSpec = if (trigger) {
            keyframes {
                durationMillis = 400
                0f at 0
                -10f at 50
                10f at 100
                -10f at 150
                10f at 200
                -5f at 250
                5f at 300
                0f at 400
            }
        } else {
            snap()
        },
        label = "shake"
    )

    Box(
        modifier = Modifier.offset(x = shakeOffset.dp)
    ) {
        content()
    }
}

/**
 * Animación de pulso para elementos destacados
 */
@Composable
fun PulseAnimation(
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        modifier = Modifier.scale(if (enabled) scale else 1f)
    ) {
        content()
    }
}

/**
 * Animación de carga con puntos
 */
@Composable
fun LoadingDotsAnimation(): String {
    val infiniteTransition = rememberInfiniteTransition(label = "dots")

    val dotsCount by infiniteTransition.animateValue(
        initialValue = 0,
        targetValue = 4,
        typeConverter = Int.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dotsCount"
    )

    return ".".repeat(dotsCount)
}

// Extension para offset en dp
private val Float.dp: androidx.compose.ui.unit.Dp
    get() = androidx.compose.ui.unit.Dp(this)
