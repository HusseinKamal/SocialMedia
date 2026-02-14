package com.hussein.socialmedia.domain.setting.model

/**
 * Domain model representing app settings
 */
data class AppSettings(
    val themeMode: ThemeMode,
    val language: String,
    val notificationsEnabled: Boolean,
    val autoPlayVideos: Boolean,
    val showOnlineStatus: Boolean,
    val readReceiptsEnabled: Boolean,
    val feedLayout: FeedLayout,
    val autoDownloadMedia: Boolean
)

enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}

enum class FeedLayout {
    GRID,
    LIST
}

/**
 * Domain model for user session
 */
data class UserSession(
    val authToken: String,
    val refreshToken: String,
    val userId: String,
    val email: String,
    val isLoggedIn: Boolean
)