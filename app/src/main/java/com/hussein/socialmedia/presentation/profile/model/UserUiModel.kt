package com.hussein.socialmedia.presentation.profile.model

/**
 * UI Model for User Profile.
 */
data class UserUiModel(
    val id: String,
    val username: String,
    val displayName: String,
    val avatarUrl: String?,
    val bio: String,
    val followersCount: Int,
    val followingCount: Int,
    val postsCount: Int,
    val isFollowing: Boolean,
    val formattedJoinDate: String
)