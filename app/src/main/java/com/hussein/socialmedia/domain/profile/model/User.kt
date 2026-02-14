package com.hussein.socialmedia.domain.profile.model

/**
 * Domain model representing a User.
 * This is the clean business entity used across use cases.
 */
data class User(
    val id: String,
    val username: String,
    val email: String,
    val displayName: String,
    val avatarUrl: String?,
    val bio: String?,
    val followersCount: Int,
    val followingCount: Int,
    val postsCount: Int,
    val isFollowing: Boolean,
    val createdAt: Long
)