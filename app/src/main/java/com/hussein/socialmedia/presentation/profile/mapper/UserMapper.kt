package com.hussein.socialmedia.presentation.profile.mapper

import com.hussein.socialmedia.data.profile.remote.mapper.formatTimestamp
import com.hussein.socialmedia.domain.profile.model.User
import com.hussein.socialmedia.presentation.profile.model.UserUiModel

/**
 * Maps User domain model to UserUiModel (for presentation layer)
 */
fun User.toUiModel(): UserUiModel {
    return UserUiModel(
        id = id,
        username = username,
        displayName = displayName,
        avatarUrl = avatarUrl,
        bio = bio ?: "",
        followersCount = followersCount,
        followingCount = followingCount,
        postsCount = postsCount,
        isFollowing = isFollowing,
        formattedJoinDate = formatTimestamp(createdAt)
    )
}