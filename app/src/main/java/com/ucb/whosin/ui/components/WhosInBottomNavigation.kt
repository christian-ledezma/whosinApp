package com.ucb.whosin.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ucb.whosin.ui.theme.WhosInColors

data class BottomNavItem(
    val route: String,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val label: String
)

@Composable
fun WhosInBottomNavigation (
    items: List<BottomNavItem>,
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
    modifier = modifier.fillMaxWidth(),
    color = WhosInColors.White,
    shadowElevation = 12.dp,
    tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = currentRoute == item.route

                BottomNavItemView(
                    item = item,
                    isSelected = isSelected,
                    onClick = { onNavigate(item.route) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun BottomNavItemView(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    // Animación de escala del ícono
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "iconScale"
    )

    // Animación del indicador superior
    val indicatorWidth by animateDpAsState(
        targetValue = if (isSelected) 32.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "indicatorWidth"
    )

    Column(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isSelected)
                    WhosInColors.LimeGreen.copy(alpha = 0.12f)
                else
                    Color.Transparent
            )
            .padding(horizontal = 12.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Indicador superior (línea)
        Box(
            modifier = Modifier
                .width(indicatorWidth)
                .height(3.dp)
                .clip(CircleShape)
                .background(WhosInColors.DarkTeal)
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Ícono con animación
        Icon(
            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
            contentDescription = item.label,
            tint = if (isSelected) WhosInColors.DarkTeal else WhosInColors.GrayBlue,
            modifier = Modifier
                .size(26.dp)
                .scale(scale)
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Label
        Text(
            text = item.label,
            fontSize = if (isSelected) 12.sp else 11.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) WhosInColors.DarkTeal else WhosInColors.GrayBlue,
            maxLines = 1
        )
    }
}
