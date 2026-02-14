package com.hussein.socialmedia.data.profile.remote.model.request


/**
 * Request model for updating profile
 */
data class UpdateProfileRequest(
    val displayName: String?,
    val bio: String?,
    val avatarUrl: String?
)