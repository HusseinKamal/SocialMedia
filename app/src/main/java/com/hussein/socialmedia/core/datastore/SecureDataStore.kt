package com.hussein.socialmedia.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Secure DataStore implementation using Preferences DataStore.
 * Provides reactive data storage with Flow-based API.
 * Note: For additional encryption, combine with EncryptedPreferences or use Proto DataStore with encryption.
 */

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "secure_datastore")

@Singleton
class SecureDataStore @Inject constructor(
    @param:ApplicationContext private val context: Context
) {

    private val dataStore = context.dataStore

    companion object {
        // User Preferences Keys
        val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
        val KEY_LANGUAGE = stringPreferencesKey("language")
        val KEY_NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val KEY_AUTO_PLAY_VIDEOS = booleanPreferencesKey("auto_play_videos")

        // App State Keys
        val KEY_LAST_SYNC_TIME = longPreferencesKey("last_sync_time")
        val KEY_APP_LAUNCH_COUNT = intPreferencesKey("app_launch_count")
        val KEY_ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")

        // Feed Preferences
        val KEY_FEED_LAYOUT = stringPreferencesKey("feed_layout")
        val KEY_SHOW_SAVED_POSTS_ONLY = booleanPreferencesKey("show_saved_posts_only")

        // Privacy Settings
        val KEY_SHOW_ONLINE_STATUS = booleanPreferencesKey("show_online_status")
        val KEY_READ_RECEIPTS_ENABLED = booleanPreferencesKey("read_receipts_enabled")
        val KEY_ALLOW_MESSAGE_REQUESTS = booleanPreferencesKey("allow_message_requests")

        // Cache Settings
        val KEY_CACHE_SIZE_LIMIT = longPreferencesKey("cache_size_limit")
        val KEY_AUTO_DOWNLOAD_MEDIA = booleanPreferencesKey("auto_download_media")
    }

    // Theme Mode
    suspend fun saveThemeMode(mode: String) {
        dataStore.edit { preferences ->
            preferences[KEY_THEME_MODE] = mode
        }
    }

    fun getThemeMode(): Flow<String> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[KEY_THEME_MODE] ?: "system"
            }
    }

    // Language
    suspend fun saveLanguage(language: String) {
        dataStore.edit { preferences ->
            preferences[KEY_LANGUAGE] = language
        }
    }

    fun getLanguage(): Flow<String> {
        return dataStore.data
            .catch { handleException(it) }
            .map { preferences ->
                preferences[KEY_LANGUAGE] ?: "en"
            }
    }

    // Notifications
    suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_NOTIFICATIONS_ENABLED] = enabled
        }
    }

    fun isNotificationsEnabled(): Flow<Boolean> {
        return dataStore.data
            .catch { handleException(it) }
            .map { preferences ->
                preferences[KEY_NOTIFICATIONS_ENABLED] ?: true
            }
    }

    // Auto Play Videos
    suspend fun setAutoPlayVideos(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_AUTO_PLAY_VIDEOS] = enabled
        }
    }

    fun isAutoPlayVideosEnabled(): Flow<Boolean> {
        return dataStore.data
            .catch { handleException(it) }
            .map { preferences ->
                preferences[KEY_AUTO_PLAY_VIDEOS] ?: false
            }
    }

    // Last Sync Time
    suspend fun saveLastSyncTime(timestamp: Long) {
        dataStore.edit { preferences ->
            preferences[KEY_LAST_SYNC_TIME] = timestamp
        }
    }

    fun getLastSyncTime(): Flow<Long> {
        return dataStore.data
            .catch { handleException(it) }
            .map { preferences ->
                preferences[KEY_LAST_SYNC_TIME] ?: 0L
            }
    }

    // App Launch Count
    suspend fun incrementAppLaunchCount() {
        dataStore.edit { preferences ->
            val currentCount = preferences[KEY_APP_LAUNCH_COUNT] ?: 0
            preferences[KEY_APP_LAUNCH_COUNT] = currentCount + 1
        }
    }

    fun getAppLaunchCount(): Flow<Int> {
        return dataStore.data
            .catch { handleException(it) }
            .map { preferences ->
                preferences[KEY_APP_LAUNCH_COUNT] ?: 0
            }
    }

    // Onboarding
    suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_ONBOARDING_COMPLETED] = completed
        }
    }

    fun isOnboardingCompleted(): Flow<Boolean> {
        return dataStore.data
            .catch { handleException(it) }
            .map { preferences ->
                preferences[KEY_ONBOARDING_COMPLETED] ?: false
            }
    }

    // Feed Layout
    suspend fun saveFeedLayout(layout: String) {
        dataStore.edit { preferences ->
            preferences[KEY_FEED_LAYOUT] = layout
        }
    }

    fun getFeedLayout(): Flow<String> {
        return dataStore.data
            .catch { handleException(it) }
            .map { preferences ->
                preferences[KEY_FEED_LAYOUT] ?: "grid"
            }
    }

    // Show Saved Posts Only
    suspend fun setShowSavedPostsOnly(showSavedOnly: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_SHOW_SAVED_POSTS_ONLY] = showSavedOnly
        }
    }

    fun isShowSavedPostsOnly(): Flow<Boolean> {
        return dataStore.data
            .catch { handleException(it) }
            .map { preferences ->
                preferences[KEY_SHOW_SAVED_POSTS_ONLY] ?: false
            }
    }

    // Online Status
    suspend fun setShowOnlineStatus(show: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_SHOW_ONLINE_STATUS] = show
        }
    }

    fun isShowOnlineStatus(): Flow<Boolean> {
        return dataStore.data
            .catch { handleException(it) }
            .map { preferences ->
                preferences[KEY_SHOW_ONLINE_STATUS] ?: true
            }
    }

    // Read Receipts
    suspend fun setReadReceiptsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_READ_RECEIPTS_ENABLED] = enabled
        }
    }

    fun isReadReceiptsEnabled(): Flow<Boolean> {
        return dataStore.data
            .catch { handleException(it) }
            .map { preferences ->
                preferences[KEY_READ_RECEIPTS_ENABLED] ?: true
            }
    }

    // Message Requests
    suspend fun setAllowMessageRequests(allow: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_ALLOW_MESSAGE_REQUESTS] = allow
        }
    }

    fun isAllowMessageRequests(): Flow<Boolean> {
        return dataStore.data
            .catch { handleException(it) }
            .map { preferences ->
                preferences[KEY_ALLOW_MESSAGE_REQUESTS] ?: true
            }
    }

    // Cache Size Limit
    suspend fun saveCacheSizeLimit(sizeInBytes: Long) {
        dataStore.edit { preferences ->
            preferences[KEY_CACHE_SIZE_LIMIT] = sizeInBytes
        }
    }

    fun getCacheSizeLimit(): Flow<Long> {
        return dataStore.data
            .catch { handleException(it) }
            .map { preferences ->
                preferences[KEY_CACHE_SIZE_LIMIT] ?: 100 * 1024 * 1024L // 100 MB default
            }
    }

    // Auto Download Media
    suspend fun setAutoDownloadMedia(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_AUTO_DOWNLOAD_MEDIA] = enabled
        }
    }

    fun isAutoDownloadMedia(): Flow<Boolean> {
        return dataStore.data
            .catch { handleException(it) }
            .map { preferences ->
                preferences[KEY_AUTO_DOWNLOAD_MEDIA] ?: true
            }
    }

    // Generic save methods
    suspend fun saveString(key: Preferences.Key<String>, value: String) {
        dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    fun getString(key: Preferences.Key<String>, defaultValue: String = ""): Flow<String> {
        return dataStore.data
            .catch { handleException(it) }
            .map { preferences ->
                preferences[key] ?: defaultValue
            }
    }

    suspend fun saveInt(key: Preferences.Key<Int>, value: Int) {
        dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    fun getInt(key: Preferences.Key<Int>, defaultValue: Int = 0): Flow<Int> {
        return dataStore.data
            .catch { handleException(it) }
            .map { preferences ->
                preferences[key] ?: defaultValue
            }
    }

    suspend fun saveBoolean(key: Preferences.Key<Boolean>, value: Boolean) {
        dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    fun getBoolean(key: Preferences.Key<Boolean>, defaultValue: Boolean = false): Flow<Boolean> {
        return dataStore.data
            .catch { handleException(it) }
            .map { preferences ->
                preferences[key] ?: defaultValue
            }
    }

    suspend fun saveLong(key: Preferences.Key<Long>, value: Long) {
        dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    fun getLong(key: Preferences.Key<Long>, defaultValue: Long = 0L): Flow<Long> {
        return dataStore.data
            .catch { handleException(it) }
            .map { preferences ->
                preferences[key] ?: defaultValue
            }
    }

    // Remove specific key
    suspend fun remove(key: Preferences.Key<*>) {
        dataStore.edit { preferences ->
            preferences.remove(key)
        }
    }

    // Clear all data
    suspend fun clearAll() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    // Save user preferences as a batch
    suspend fun saveUserPreferences(
        themeMode: String,
        notificationsEnabled: Boolean,
        autoPlayVideos: Boolean,
        showOnlineStatus: Boolean
    ) {
        dataStore.edit { preferences ->
            preferences[KEY_THEME_MODE] = themeMode
            preferences[KEY_NOTIFICATIONS_ENABLED] = notificationsEnabled
            preferences[KEY_AUTO_PLAY_VIDEOS] = autoPlayVideos
            preferences[KEY_SHOW_ONLINE_STATUS] = showOnlineStatus
        }
    }

    // Get all user preferences as Flow
    fun getUserPreferences(): Flow<UserPreferences> {
        return dataStore.data
            .catch { handleException(it) }
            .map { preferences ->
                UserPreferences(
                    themeMode = preferences[KEY_THEME_MODE] ?: "system",
                    language = preferences[KEY_LANGUAGE] ?: "en",
                    notificationsEnabled = preferences[KEY_NOTIFICATIONS_ENABLED] ?: true,
                    autoPlayVideos = preferences[KEY_AUTO_PLAY_VIDEOS] ?: false,
                    showOnlineStatus = preferences[KEY_SHOW_ONLINE_STATUS] ?: true,
                    readReceiptsEnabled = preferences[KEY_READ_RECEIPTS_ENABLED] ?: true,
                    feedLayout = preferences[KEY_FEED_LAYOUT] ?: "grid"
                )
            }
    }

    private fun handleException(exception: Throwable) {
        if (exception is IOException) {
            // Log the error
        } else {
            throw exception
        }
    }
}

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