package com.hussein.socialmedia.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val icon : ImageVector,
    val title : String,
)
val TOP_LEVEL_DESTINATION = mapOf(
    Route.Feed to BottomNavItem(
        icon = Icons.Outlined.Home,
        title = "Feed"
    ),
    Route.Chat to BottomNavItem(
        icon = Icons.Outlined.Chat,
        title = "Chat"
    ),
    Route.Profile to BottomNavItem(
        icon = Icons.Outlined.AccountCircle,
        title = "Profile"
    ),
    Route.Settings to BottomNavItem(
        icon = Icons.Outlined.Settings,
        title = "Settings"
    )

)