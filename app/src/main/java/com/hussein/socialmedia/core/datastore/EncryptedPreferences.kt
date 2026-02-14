package com.hussein.socialmedia.core.datastore


import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

/**
 * Encrypted SharedPreferences wrapper using AndroidX Security library.
 * Provides secure storage for sensitive data like tokens, user credentials, etc.
 */
@Singleton
class EncryptedPreferences @Inject constructor(
    @param:ApplicationContext private val context: Context
) {

    private val masterKey: MasterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    private val encryptedSharedPreferences: SharedPreferences by lazy {
        EncryptedSharedPreferences.create(
            context,
            ENCRYPTED_PREFS_FILE_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    companion object {
        private const val ENCRYPTED_PREFS_FILE_NAME = "secure_app_prefs"

        // Keys for encrypted data
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
        private const val KEY_SESSION_ID = "session_id"
        private const val KEY_DEVICE_ID = "device_id"
    }

    // Auth Token
    fun saveAuthToken(token: String) {
        encryptedSharedPreferences.edit { putString(KEY_AUTH_TOKEN, token) }
    }

    fun getAuthToken(): String? {
        return encryptedSharedPreferences.getString(KEY_AUTH_TOKEN, null)
    }

    fun clearAuthToken() {
        encryptedSharedPreferences.edit { remove(KEY_AUTH_TOKEN) }
    }

    // Refresh Token
    fun saveRefreshToken(token: String) {
        encryptedSharedPreferences.edit { putString(KEY_REFRESH_TOKEN, token) }
    }

    fun getRefreshToken(): String? {
        return encryptedSharedPreferences.getString(KEY_REFRESH_TOKEN, null)
    }

    // User ID
    fun saveUserId(userId: String) {
        encryptedSharedPreferences.edit { putString(KEY_USER_ID, userId) }
    }

    fun getUserId(): String? {
        return encryptedSharedPreferences.getString(KEY_USER_ID, null)
    }

    // User Email
    fun saveUserEmail(email: String) {
        encryptedSharedPreferences.edit { putString(KEY_USER_EMAIL, email) }
    }

    fun getUserEmail(): String? {
        return encryptedSharedPreferences.getString(KEY_USER_EMAIL, null)
    }

    // Login State
    fun setLoggedIn(isLoggedIn: Boolean) {
        encryptedSharedPreferences.edit { putBoolean(KEY_IS_LOGGED_IN, isLoggedIn) }
    }

    fun isLoggedIn(): Boolean {
        return encryptedSharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    // Biometric
    fun setBiometricEnabled(enabled: Boolean) {
        encryptedSharedPreferences.edit { putBoolean(KEY_BIOMETRIC_ENABLED, enabled) }
    }

    fun isBiometricEnabled(): Boolean {
        return encryptedSharedPreferences.getBoolean(KEY_BIOMETRIC_ENABLED, false)
    }

    // Session ID
    fun saveSessionId(sessionId: String) {
        encryptedSharedPreferences.edit { putString(KEY_SESSION_ID, sessionId) }
    }

    fun getSessionId(): String? {
        return encryptedSharedPreferences.getString(KEY_SESSION_ID, null)
    }

    // Device ID
    fun saveDeviceId(deviceId: String) {
        encryptedSharedPreferences.edit { putString(KEY_DEVICE_ID, deviceId) }
    }

    fun getDeviceId(): String? {
        return encryptedSharedPreferences.getString(KEY_DEVICE_ID, null)
    }

    // Save user session data (atomic operation)
    fun saveUserSession(
        authToken: String,
        refreshToken: String,
        userId: String,
        email: String
    ) {
        encryptedSharedPreferences.edit().apply {
            putString(KEY_AUTH_TOKEN, authToken)
            putString(KEY_REFRESH_TOKEN, refreshToken)
            putString(KEY_USER_ID, userId)
            putString(KEY_USER_EMAIL, email)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    // Clear all session data (logout)
    fun clearUserSession() {
        encryptedSharedPreferences.edit().apply {
            remove(KEY_AUTH_TOKEN)
            remove(KEY_REFRESH_TOKEN)
            remove(KEY_USER_ID)
            remove(KEY_USER_EMAIL)
            remove(KEY_SESSION_ID)
            putBoolean(KEY_IS_LOGGED_IN, false)
            apply()
        }
    }

    // Clear all encrypted data
    fun clearAll() {
        encryptedSharedPreferences.edit().clear().apply()
    }

    // Generic methods for custom data
    fun saveString(key: String, value: String) {
        encryptedSharedPreferences.edit().putString(key, value).apply()
    }

    fun getString(key: String, defaultValue: String? = null): String? {
        return encryptedSharedPreferences.getString(key, defaultValue)
    }

    fun saveInt(key: String, value: Int) {
        encryptedSharedPreferences.edit { putInt(key, value) }
    }

    fun getInt(key: String, defaultValue: Int = 0): Int {
        return encryptedSharedPreferences.getInt(key, defaultValue)
    }

    fun saveLong(key: String, value: Long) {
        encryptedSharedPreferences.edit { putLong(key, value) }
    }

    fun getLong(key: String, defaultValue: Long = 0L): Long {
        return encryptedSharedPreferences.getLong(key, defaultValue)
    }

    fun saveBoolean(key: String, value: Boolean) {
        encryptedSharedPreferences.edit { putBoolean(key, value) }
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return encryptedSharedPreferences.getBoolean(key, defaultValue)
    }

    fun saveFloat(key: String, value: Float) {
        encryptedSharedPreferences.edit { putFloat(key, value) }
    }

    fun getFloat(key: String, defaultValue: Float = 0f): Float {
        return encryptedSharedPreferences.getFloat(key, defaultValue)
    }

    fun remove(key: String) {
        encryptedSharedPreferences.edit { remove(key) }
    }

    fun contains(key: String): Boolean {
        return encryptedSharedPreferences.contains(key)
    }
}