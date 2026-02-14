package com.hussein.socialmedia.data.setting.repository

import com.hussein.socialmedia.core.datastore.SecureDataStore
import com.hussein.socialmedia.core.datastore.EncryptedPreferences
import com.hussein.socialmedia.domain.setting.model.AppSettings
import com.hussein.socialmedia.domain.setting.model.FeedLayout
import com.hussein.socialmedia.domain.setting.repository.PreferencesRepository
import com.hussein.socialmedia.domain.setting.model.ThemeMode
import com.hussein.socialmedia.domain.setting.model.UserSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of PreferencesRepository.
 * Combines EncryptedPreferences for sensitive data and SecureDataStore for settings.
 */
@Singleton
class PreferencesRepositoryImpl @Inject constructor(
    private val encryptedPreferences: EncryptedPreferences,
    private val secureDataStore: SecureDataStore
) : PreferencesRepository {

    override fun getAppSettings(): Flow<AppSettings> {
        return secureDataStore.getUserPreferences().map { prefs ->
            AppSettings(
                themeMode = when (prefs.themeMode) {
                    "light" -> ThemeMode.LIGHT
                    "dark" -> ThemeMode.DARK
                    else -> ThemeMode.SYSTEM
                },
                language = prefs.language,
                notificationsEnabled = prefs.notificationsEnabled,
                autoPlayVideos = prefs.autoPlayVideos,
                showOnlineStatus = prefs.showOnlineStatus,
                readReceiptsEnabled = prefs.readReceiptsEnabled,
                feedLayout = when (prefs.feedLayout) {
                    "list" -> FeedLayout.LIST
                    else -> FeedLayout.GRID
                },
                autoDownloadMedia = secureDataStore.isAutoDownloadMedia().first()
            )
        }
    }

    override suspend fun updateAppSettings(settings: AppSettings) {
        secureDataStore.saveUserPreferences(
            themeMode = when (settings.themeMode) {
                ThemeMode.LIGHT -> "light"
                ThemeMode.DARK -> "dark"
                ThemeMode.SYSTEM -> "system"
            },
            notificationsEnabled = settings.notificationsEnabled,
            autoPlayVideos = settings.autoPlayVideos,
            showOnlineStatus = settings.showOnlineStatus
        )

        secureDataStore.saveFeedLayout(
            when (settings.feedLayout) {
                FeedLayout.GRID -> "grid"
                FeedLayout.LIST -> "list"
            }
        )

        secureDataStore.setReadReceiptsEnabled(settings.readReceiptsEnabled)
        secureDataStore.setAutoDownloadMedia(settings.autoDownloadMedia)
        secureDataStore.saveLanguage(settings.language)
    }

    override suspend fun updateThemeMode(mode: String) {
        secureDataStore.saveThemeMode(mode)
    }

    override suspend fun updateNotifications(enabled: Boolean) {
        secureDataStore.setNotificationsEnabled(enabled)
    }

    override suspend fun saveUserSession(session: UserSession) {
        encryptedPreferences.saveUserSession(
            authToken = session.authToken,
            refreshToken = session.refreshToken,
            userId = session.userId,
            email = session.email
        )
    }

    override suspend fun getUserSession(): UserSession? {
        val authToken = encryptedPreferences.getAuthToken()
        val refreshToken = encryptedPreferences.getRefreshToken()
        val userId = encryptedPreferences.getUserId()
        val email = encryptedPreferences.getUserEmail()

        return if (authToken != null && userId != null && email != null) {
            UserSession(
                authToken = authToken,
                refreshToken = refreshToken ?: "",
                userId = userId,
                email = email,
                isLoggedIn = encryptedPreferences.isLoggedIn()
            )
        } else {
            null
        }
    }

    override suspend fun clearUserSession() {
        encryptedPreferences.clearUserSession()
    }

    override fun isLoggedIn(): Boolean {
        return encryptedPreferences.isLoggedIn()
    }

    override fun getAuthToken(): String? {
        return encryptedPreferences.getAuthToken()
    }

    override suspend fun saveAuthToken(token: String) {
        encryptedPreferences.saveAuthToken(token)
    }

    override suspend fun clearAuthToken() {
        encryptedPreferences.clearAuthToken()
    }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        secureDataStore.setOnboardingCompleted(completed)
    }

    override fun isOnboardingCompleted(): Flow<Boolean> {
        return secureDataStore.isOnboardingCompleted()
    }

    override suspend fun incrementAppLaunchCount() {
        secureDataStore.incrementAppLaunchCount()
    }

    override fun getAppLaunchCount(): Flow<Int> {
        return secureDataStore.getAppLaunchCount()
    }
}