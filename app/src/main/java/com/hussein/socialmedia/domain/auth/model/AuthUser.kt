package com.hussein.socialmedia.domain.auth.model

/**
 * Domain model for authenticated user
 */
data class AuthUser(
    val id: String,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val fullName: String,
    val image: String?,
    val gender: String?
)
