package com.hussein.socialmedia.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val icon : ImageVector,
    val title : String,
)
val TOP_LEVEL_DESTINATION = mapOf(
    Route.TodoList to BottomNavItem(
        icon = Icons.Outlined.Checklist,
        title = "Home"
    ),
    Route.TodoFavorites to BottomNavItem(
        icon = Icons.Outlined.Favorite,
        title = "Settings"
    ),
    Route.Settings to BottomNavItem(
        icon = Icons.Outlined.Settings,
        title = "Settings"
    )

)