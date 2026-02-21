package com.hussein.socialmedia.domain.profile.model

/**
 * Domain model for a friend/user to chat with
 */
data class Friend(
    val id: String,
    val firstName: String,
    val lastName: String,
    val fullName: String,
    val username: String,
    val email: String,
    val phone: String,
    val avatarUrl: String,
    val age: Int,
    val gender: String,
    val isOnline: Boolean = false,
    val lastSeen: String? = null,
    val company: String? = null,
    val jobTitle: String? = null
)