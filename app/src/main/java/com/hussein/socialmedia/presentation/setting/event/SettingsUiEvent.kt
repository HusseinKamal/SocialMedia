package com.hussein.socialmedia.presentation.setting.event

import com.hussein.socialmedia.domain.setting.model.FeedLayout
import com.hussein.socialmedia.domain.setting.model.ThemeMode
import com.hussein.socialmedia.domain.setting.model.UserSession

/**
 * UI Events for Settings screen
 */
sealed class SettingsUiEvent {
    data class UpdateThemeMode(val mode: ThemeMode) : SettingsUiEvent()
    data class UpdateNotifications(val enabled: Boolean) : SettingsUiEvent()
    data class UpdateAutoPlayVideos(val enabled: Boolean) : SettingsUiEvent()
    data class UpdateShowOnlineStatus(val show: Boolean) : SettingsUiEvent()
    data class UpdateReadReceipts(val enabled: Boolean) : SettingsUiEvent()
    data class UpdateFeedLayout(val layout: FeedLayout) : SettingsUiEvent()
    data class UpdateLanguage(val language: String) : SettingsUiEvent()
    data class SaveUserSession(val session: UserSession) : SettingsUiEvent()
    data object Logout : SettingsUiEvent()
    data object ClearError : SettingsUiEvent()
}