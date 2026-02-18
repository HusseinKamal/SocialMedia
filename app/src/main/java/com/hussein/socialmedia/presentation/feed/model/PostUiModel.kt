package com.hussein.socialmedia.presentation.feed.model

/**
 * UI Model for Post - optimized for presentation layer.
 * Contains formatted strings and display-ready data.
 */
data class PostUiModel(
    val id: String,
    val userId: String?,
    val username: String?,
    val userAvatarUrl: String?,
    val content: String?,
    val imageUrl: String?,
    val likesCount: Int?,
    val commentsCount: Int?,
    val sharesCount: Int?,
    val isLiked: Boolean = false,
    val isSaved: Boolean = false,
    val formattedTime: String?
)