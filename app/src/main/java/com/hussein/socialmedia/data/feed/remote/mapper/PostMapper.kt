package com.hussein.socialmedia.data.feed.remote.mapper

import com.hussein.socialmedia.data.feed.local.PostEntity
import com.hussein.socialmedia.data.feed.remote.model.response.PostDto
import com.hussein.socialmedia.domain.feed.model.Post

/**
 * Mapper functions for converting between Post model types.
 */

/**
 * Maps PostDto (from API) to PostEntity (for database)
 */
fun PostDto.toEntity(): PostEntity {
    return PostEntity(
        id = id,
        userId = userId,
        username = username,
        userAvatarUrl = userAvatarUrl,
        content = content,
        imageUrl = imageUrl,
        likesCount = likesCount,
        commentsCount = commentsCount,
        sharesCount = sharesCount,
        isLiked = isLiked,
        isSaved = isSaved,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

/**
 * Maps PostDto (from API) to Post domain model
 */
fun PostDto.toDomain(): Post {
    return Post(
        id = id,
        userId = userId,
        username = username,
        userAvatarUrl = userAvatarUrl,
        content = content,
        imageUrl = imageUrl,
        likesCount = likesCount,
        commentsCount = commentsCount,
        sharesCount = sharesCount,
        isLiked = isLiked,
        isSaved = isSaved,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

/**
 * Maps PostEntity (from database) to Post domain model
 */
fun PostEntity.toDomain(): Post {
    return Post(
        id = id,
        userId = userId,
        username = username,
        userAvatarUrl = userAvatarUrl,
        content = content,
        imageUrl = imageUrl,
        likesCount = likesCount,
        commentsCount = commentsCount,
        sharesCount = sharesCount,
        isLiked = isLiked,
        isSaved = isSaved,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

/**
 * Helper function to format post timestamp
 */
fun formatPostTime(timestamp: Long): String {
    val currentTime = System.currentTimeMillis()
    val diff = currentTime - timestamp

    return when {
        diff < 60_000 -> "Just now"
        diff < 3_600_000 -> "${diff / 60_000}m"
        diff < 86_400_000 -> "${diff / 3_600_000}h"
        diff < 604_800_000 -> "${diff / 86_400_000}d"
        else -> "${diff / 604_800_000}w"
    }
}