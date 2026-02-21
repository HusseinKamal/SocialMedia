package com.hussein.socialmedia.presentation.profile.mapper

import androidx.compose.ui.graphics.Color
import com.hussein.socialmedia.domain.profile.model.Friend
import com.hussein.socialmedia.presentation.profile.model.FriendUiModel

/**
 * Maps Friend domain model to FriendUiModel
 */
fun Friend.toUiModel(): FriendUiModel {
    return FriendUiModel(
        id = id,
        fullName = fullName,
        username = "@$username",
        avatarUrl = avatarUrl,
        statusText = getStatusText(),
        statusColor = getStatusColor(),
        isOnline = isOnline,
        subtitle = getSubtitle(),
        initials = getInitials()
    )
}

/**
 * Maps list of Friends to list of FriendUiModels
 */
fun List<Friend>.toUiModelList(): List<FriendUiModel> {
    return this.map { it.toUiModel() }
}

/**
 * Gets status text for display
 */
private fun Friend.getStatusText(): String {
    return when {
        isOnline -> "Online"
        lastSeen != null -> "Last seen $lastSeen"
        else -> "Offline"
    }
}

/**
 * Gets status color
 */
private fun Friend.getStatusColor(): Color {
    return if (isOnline) {
        Color(0xFF4CAF50) // Green
    } else {
        Color(0xFF9E9E9E) // Gray
    }
}

/**
 * Gets subtitle for friend card
 */
private fun Friend.getSubtitle(): String {
    return when {
        jobTitle != null && company != null -> "$jobTitle at $company"
        jobTitle != null -> jobTitle
        company != null -> company
        else -> email
    }
}

/**
 * Gets initials from name
 */
private fun Friend.getInitials(): String {
    val firstInitial = firstName.firstOrNull()?.uppercaseChar() ?: ""
    val lastInitial = lastName.firstOrNull()?.uppercaseChar() ?: ""
    return "$firstInitial$lastInitial"
}

/**
 * Gets avatar color based on ID
 */
fun FriendUiModel.getAvatarColor(): Color {
    val colors = listOf(
        Color(0xFF6750A4),
        Color(0xFF00BCD4),
        Color(0xFF4CAF50),
        Color(0xFFFF9800),
        Color(0xFFE91E63),
        Color(0xFF9C27B0),
        Color(0xFF2196F3),
        Color(0xFFFF5722)
    )
    val index = id.hashCode() % colors.size
    return colors[index.coerceIn(0, colors.size - 1)]
}