package com.hussein.socialmedia.presentation.profile.model

/**
 * UI Model for Friend optimized for presentation
 */
data class FriendUiModel(
    val id: String,
    val fullName: String,
    val username: String,
    val avatarUrl: String,
    val statusText: String,           // "Online" or "Last seen 30m ago"
    val statusColor: androidx.compose.ui.graphics.Color,
    val isOnline: Boolean,
    val subtitle: String,              // Job title or email
    val initials: String               // For avatar placeholder
)