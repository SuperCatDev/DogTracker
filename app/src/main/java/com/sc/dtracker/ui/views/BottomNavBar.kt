package com.sc.dtracker.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList

@Stable
data class BottomNavBarItem(
    val icon: Painter?,
    val selected: Boolean = false,
    val text: String = "",
    val onClick: () -> Unit = {}
)

@Composable
fun BottomNavBarItem(
    itemData: BottomNavBarItem,
    selectedColor: Color = Color(0xFF7980FF),
    nonSelectedColor: Color = Color(0xFF464D61).copy(alpha = 0.7f),
    iconSize: Dp = 24.dp
) {
    IconButton(onClick = { itemData.onClick() }) {
        itemData.icon?.let {
            Icon(
                painter = it,
                contentDescription = itemData.text,
                tint = if (itemData.selected) selectedColor else nonSelectedColor,
                modifier = Modifier.size(iconSize)
            )
        }
    }
}

@Composable
fun BottomNavBar(
    barHeight: Dp = 60.dp,
    fabColor: Color = Color(0xFF7980FF),
    fabSize: Dp = 64.dp,
    fabIconSize: Dp = 32.dp,
    cardTopCornerSize: Dp = 24.dp,
    cardElevation: Dp = 8.dp,
    fabImageVector: ImageVector = Icons.Default.Add,
    fabImageDescription: String = "",
    buttons: ImmutableList<BottomNavBarItem>,
    fabOnClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(barHeight + fabSize / 2)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight)
                .align(Alignment.BottomCenter),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = cardElevation),
            shape = RoundedCornerShape(
                topStart = cardTopCornerSize,
                topEnd = cardTopCornerSize,
                bottomEnd = 0.dp,
                bottomStart = 0.dp
            )
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            )
            {
                val buttonsSize = buttons.size

                repeat(buttonsSize / 2) {
                    BottomNavBarItem(buttons[it])
                }
                Spacer(modifier = Modifier.size(fabSize))
                repeat(buttonsSize / 2) {
                    BottomNavBarItem(buttons[it + (buttonsSize / 2)])
                }
            }
        }
        LargeFloatingActionButton(
            modifier = Modifier
                .size(fabSize)
                .align(Alignment.TopCenter),
            onClick = { fabOnClick() },
            shape = CircleShape,
            containerColor = fabColor,
            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(defaultElevation = 0.dp)
        ) {
            Icon(
                imageVector = fabImageVector,
                contentDescription = fabImageDescription,
                tint = Color.White,
                modifier = Modifier.size(fabIconSize)
            )
        }
    }
}