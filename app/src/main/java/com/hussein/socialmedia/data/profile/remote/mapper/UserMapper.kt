package com.hussein.socialmedia.data.profile.remote.mapper

import com.hussein.socialmedia.data.profile.local.UserEntity
import com.hussein.socialmedia.data.profile.remote.model.response.UserDto
import com.hussein.socialmedia.domain.profile.model.User

/**
 * Mapper functions for converting between User model types.
 * Follows the data flow: DTO -> Entity -> Domain -> UI
 */

/**
 * Maps UserDto (from API) to UserEntity (for database)
 */
fun UserDto.toEntity(): UserEntity {
    return UserEntity(
        id = id,
        username = username,
        email = email,
        displayName = displayName,
        avatarUrl = avatarUrl,
        bio = bio,
        followersCount = followersCount,
        followingCount = followingCount,
        postsCount = postsCount,
        isFollowing = isFollowing,
        createdAt = createdAt
    )
}

/**
 * Maps UserDto (from API) to User domain model
 */
fun UserDto.toDomain(): User {
    return User(
        id = id,
        username = username,
        email = email,
        displayName = displayName,
        avatarUrl = avatarUrl,
        bio = bio,
        followersCount = followersCount,
        followingCount = followingCount,
        postsCount = postsCount,
        isFollowing = isFollowing,
        createdAt = createdAt
    )
}

/**
 * Maps UserEntity (from database) to User domain model
 */
fun UserEntity.toDomain(): User {
    return User(
        id = id,
        username = username,
        email = email,
        displayName = displayName,
        avatarUrl = avatarUrl,
        bio = bio,
        followersCount = followersCount,
        followingCount = followingCount,
        postsCount = postsCount,
        isFollowing = isFollowing,
        createdAt = createdAt
    )
}

/**
 * Helper function to format timestamp
 */
fun formatTimestamp(timestamp: Long): String {
    val currentTime = System.currentTimeMillis()
    val diff = currentTime - timestamp

    return when {
        diff < 60_000 -> "Just now"
        diff < 3_600_000 -> "${diff / 60_000}m ago"
        diff < 86_400_000 -> "${diff / 3_600_000}h ago"
        diff < 604_800_000 -> "${diff / 86_400_000}d ago"
        else -> "Joined ${diff / 604_800_000}w ago"
    }
}