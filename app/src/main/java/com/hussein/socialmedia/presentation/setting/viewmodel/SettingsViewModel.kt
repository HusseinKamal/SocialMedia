package com.hussein.socialmedia.presentation.setting.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hussein.socialmedia.domain.setting.model.AppSettings
import com.hussein.socialmedia.domain.setting.model.FeedLayout
import com.hussein.socialmedia.domain.setting.model.ThemeMode
import com.hussein.socialmedia.domain.setting.model.UserSession
import com.hussein.socialmedia.domain.setting.usecase.GetAppSettingsUseCase
import com.hussein.socialmedia.domain.setting.usecase.GetUserSessionUseCase
import com.hussein.socialmedia.domain.setting.usecase.LogoutUseCase
import com.hussein.socialmedia.domain.setting.usecase.SaveUserSessionUseCase
import com.hussein.socialmedia.domain.setting.usecase.UpdateAppSettingsUseCase
import com.hussein.socialmedia.presentation.setting.event.SettingsUiEvent
import com.hussein.socialmedia.presentation.setting.state.SettingsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Settings screen.
 * Manages app settings using encrypted preferences and DataStore.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getAppSettingsUseCase: GetAppSettingsUseCase,
    private val updateAppSettingsUseCase: UpdateAppSettingsUseCase,
    private val saveUserSessionUseCase: SaveUserSessionUseCase,
    private val getUserSessionUseCase: GetUserSessionUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
        loadUserSession()
    }

    /**
     * Load app settings from DataStore
     */
    private fun loadSettings() {
        viewModelScope.launch {
            getAppSettingsUseCase()
                .catch { exception ->
                    _uiState.update {
                        it.copy(error = "Failed to load settings: ${exception.message}")
                    }
                }
                .collect { settings ->
                    _uiState.update {
                        it.copy(
                            settings = settings,
                            isLoading = false
                        )
                    }
                }
        }
    }

    /**
     * Load user session from encrypted preferences
     */
    private fun loadUserSession() {
        viewModelScope.launch {
            val session = getUserSessionUseCase()
            _uiState.update {
                it.copy(userSession = session)
            }
        }
    }

    /**
     * Handle UI events
     */
    fun onEvent(event: SettingsUiEvent) {
        when (event) {
            is SettingsUiEvent.UpdateThemeMode -> updateThemeMode(event.mode)
            is SettingsUiEvent.UpdateNotifications -> updateNotifications(event.enabled)
            is SettingsUiEvent.UpdateAutoPlayVideos -> updateAutoPlayVideos(event.enabled)
            is SettingsUiEvent.UpdateShowOnlineStatus -> updateShowOnlineStatus(event.show)
            is SettingsUiEvent.UpdateReadReceipts -> updateReadReceipts(event.enabled)
            is SettingsUiEvent.UpdateFeedLayout -> updateFeedLayout(event.layout)
            is SettingsUiEvent.UpdateLanguage -> updateLanguage(event.language)
            is SettingsUiEvent.SaveUserSession -> saveUserSession(event.session)
            is SettingsUiEvent.Logout -> logout()
            is SettingsUiEvent.ClearError -> clearError()
        }
    }

    private fun updateThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            val currentSettings = _uiState.value.settings
            val updatedSettings = currentSettings.copy(themeMode = mode)
            updateAppSettingsUseCase(updatedSettings)
        }
    }

    private fun updateNotifications(enabled: Boolean) {
        viewModelScope.launch {
            val currentSettings = _uiState.value.settings
            val updatedSettings = currentSettings.copy(notificationsEnabled = enabled)
            updateAppSettingsUseCase(updatedSettings)
        }
    }

    private fun updateAutoPlayVideos(enabled: Boolean) {
        viewModelScope.launch {
            val currentSettings = _uiState.value.settings
            val updatedSettings = currentSettings.copy(autoPlayVideos = enabled)
            updateAppSettingsUseCase(updatedSettings)
        }
    }

    private fun updateShowOnlineStatus(show: Boolean) {
        viewModelScope.launch {
            val currentSettings = _uiState.value.settings
            val updatedSettings = currentSettings.copy(showOnlineStatus = show)
            updateAppSettingsUseCase(updatedSettings)
        }
    }

    private fun updateReadReceipts(enabled: Boolean) {
        viewModelScope.launch {
            val currentSettings = _uiState.value.settings
            val updatedSettings = currentSettings.copy(readReceiptsEnabled = enabled)
            updateAppSettingsUseCase(updatedSettings)
        }
    }

    private fun updateFeedLayout(layout: FeedLayout) {
        viewModelScope.launch {
            val currentSettings = _uiState.value.settings
            val updatedSettings = currentSettings.copy(feedLayout = layout)
            updateAppSettingsUseCase(updatedSettings)
        }
    }

    private fun updateLanguage(language: String) {
        viewModelScope.launch {
            val currentSettings = _uiState.value.settings
            val updatedSettings = currentSettings.copy(language = language)
            updateAppSettingsUseCase(updatedSettings)
        }
    }

    /**
     * Save user session to encrypted preferences
     */
    private fun saveUserSession(session: UserSession) {
        viewModelScope.launch {
            try {
                saveUserSessionUseCase(session)
                _uiState.update {
                    it.copy(
                        userSession = session,
                        saveSuccess = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Failed to save session: ${e.message}")
                }
            }
        }
    }

    /**
     * Logout - clear encrypted session data
     */
    private fun logout() {
        viewModelScope.launch {
            try {
                logoutUseCase()
                _uiState.update {
                    it.copy(
                        userSession = null,
                        logoutSuccess = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Failed to logout: ${e.message}")
                }
            }
        }
    }

    private fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    /**
     * Example: Save auth token separately
     */
    fun saveAuthToken(token: String) {
        viewModelScope.launch {
            val session = _uiState.value.userSession?.copy(authToken = token)
            if (session != null) {
                saveUserSession(session)
            }
        }
    }

    /**
     * Example: Update all settings at once
     */
    fun updateAllSettings(
        themeMode: ThemeMode,
        notificationsEnabled: Boolean,
        autoPlayVideos: Boolean,
        showOnlineStatus: Boolean,
        readReceiptsEnabled: Boolean,
        feedLayout: FeedLayout
    ) {
        viewModelScope.launch {
            val updatedSettings = AppSettings(
                themeMode = themeMode,
                language = _uiState.value.settings.language,
                notificationsEnabled = notificationsEnabled,
                autoPlayVideos = autoPlayVideos,
                showOnlineStatus = showOnlineStatus,
                readReceiptsEnabled = readReceiptsEnabled,
                feedLayout = feedLayout,
                autoDownloadMedia = _uiState.value.settings.autoDownloadMedia
            )
            updateAppSettingsUseCase(updatedSettings)
        }
    }
}

