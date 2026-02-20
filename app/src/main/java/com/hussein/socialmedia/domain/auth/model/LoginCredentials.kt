package com.hussein.socialmedia.domain.auth.model

/**
 * Domain model for login credentials
 */
data class LoginCredentials(
    val username: String,
    val password: String
)