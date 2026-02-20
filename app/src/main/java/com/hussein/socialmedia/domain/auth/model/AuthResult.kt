package com.hussein.socialmedia.domain.auth.model

/**
 * Domain model for authentication result
 */
data class AuthResult(
    val user: AuthUser,
    val token: String,
    val refreshToken: String
)