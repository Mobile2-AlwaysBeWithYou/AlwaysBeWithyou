package com.example.alwaysbewithyou.presentation.main.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun RowScope.MainBottomBar(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    label: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    colors: NavigationBarItemColors = NavigationBarItemDefaults.colors()
) {
    val interactionSource = remember { MutableInteractionSource() }

    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = icon,
        label = label,
        interactionSource = interactionSource,
        modifier = modifier.selectable(
            selected = selected,
            onClick = onClick,
            indication = null,
            interactionSource = interactionSource
        ),
        colors = colors
    )
}
