package com.hussein.socialmedia.presentation.setting.state

import com.hussein.socialmedia.domain.setting.model.AppSettings
import com.hussein.socialmedia.domain.setting.model.FeedLayout
import com.hussein.socialmedia.domain.setting.model.ThemeMode
import com.hussein.socialmedia.domain.setting.model.UserSession

/**
 * UI State for Settings screen
 */
data class SettingsUiState(
    val settings: AppSettings = AppSettings(
        themeMode = ThemeMode.SYSTEM,
        language = "en",
        notificationsEnabled = true,
        autoPlayVideos = false,
        showOnlineStatus = true,
        readReceiptsEnabled = true,
        feedLayout = FeedLayout.GRID,
        autoDownloadMedia = true
    ),
    val userSession: UserSession? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val saveSuccess: Boolean = false,
    val logoutSuccess: Boolean = false
)