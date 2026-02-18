package com.hussein.socialmedia.domain.feed.model

/**
 * Domain model representing a Post in the feed.
 */
data class Post(
    val id: String,
    val userId: String?,
    val username: String?,
    val userAvatarUrl: String?,
    val content: String?,
    val imageUrl: String?,
    val likesCount: Int?,
    val commentsCount: Int?,
    val sharesCount: Int?,
    val isLiked: Boolean?,
    val isSaved: Boolean?,
    val createdAt: Long?,
    val updatedAt: Long?
)