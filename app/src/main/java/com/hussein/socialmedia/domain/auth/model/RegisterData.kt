package com.hussein.socialmedia.domain.auth.model

/**
 * Domain model for registration data
 */
data class RegisterData(
    val firstName: String,
    val lastName: String,
    val username: String,
    val email: String,
    val password: String,
    val confirmPassword: String,
    val gender: String = "male"
)