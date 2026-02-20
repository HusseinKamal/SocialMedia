package com.hussein.socialmedia.data.auth.model.request

/**
 * Request model for refresh token
 */
data class RefreshTokenRequest(
    val refreshToken: String,
    val expiresInMins: Int = 30
)