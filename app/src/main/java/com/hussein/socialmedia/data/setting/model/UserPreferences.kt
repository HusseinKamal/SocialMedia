package com.hussein.socialmedia.data.setting.model

/**
 * Data class representing user preferences
 */
data class UserPreferences(
    val themeMode: String = "system",
    val language: String = "en",
    val notificationsEnabled: Boolean = true,
    val autoPlayVideos: Boolean = false,
    val showOnlineStatus: Boolean = true,
    val readReceiptsEnabled: Boolean = true,
    val feedLayout: String = "grid"
)