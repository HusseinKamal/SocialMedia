package com.hussein.socialmedia.domain.setting.repository

import com.hussein.socialmedia.domain.setting.model.AppSettings
import com.hussein.socialmedia.domain.setting.model.UserSession
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing app preferences and settings
 */
interface PreferencesRepository {

    // Settings
    fun getAppSettings(): Flow<AppSettings>
    suspend fun updateAppSettings(settings: AppSettings)
    suspend fun updateThemeMode(mode: String)
    suspend fun updateNotifications(enabled: Boolean)

    // Session
    suspend fun saveUserSession(session: UserSession)
    suspend fun getUserSession(): UserSession?
    suspend fun clearUserSession()
    fun isLoggedIn(): Boolean

    // Auth Token
    fun getAuthToken(): String?
    suspend fun saveAuthToken(token: String)
    suspend fun clearAuthToken()

    // Individual preferences
    suspend fun setOnboardingCompleted(completed: Boolean)
    fun isOnboardingCompleted(): Flow<Boolean>

    suspend fun incrementAppLaunchCount()
    fun getAppLaunchCount(): Flow<Int>
}