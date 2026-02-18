package com.hussein.socialmedia.presentation.feed.mapper

import com.hussein.socialmedia.domain.feed.model.Post
import com.hussein.socialmedia.presentation.feed.model.PostUiModel
import java.util.concurrent.TimeUnit

/**
 * Maps Post domain model to PostUiModel (for presentation layer)
 */
fun Post.toUiModel(): PostUiModel {
    return PostUiModel(
        id = id,
        userId = "",
        username = username ?: "Unknown user",
        userAvatarUrl = userAvatarUrl,
        content = content.orEmpty(),
        imageUrl = imageUrl,
        likesCount = likesCount ?: 0,
        commentsCount = commentsCount ?: 0,
        sharesCount = sharesCount ?: 0,
        isLiked = isLiked ?: false,
        isSaved = isSaved ?: false,
        formattedTime = createdAt?.toRelativeTime() ?: "Just now"
    )
}
private fun Long.toRelativeTime(): String {
    val diff = System.currentTimeMillis() - this

    val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
    val hours = TimeUnit.MILLISECONDS.toHours(diff)
    val days = TimeUnit.MILLISECONDS.toDays(diff)

    return when {
        minutes < 1 -> "Just now"
        minutes < 60 -> "$minutes min ago"
        hours < 24 -> "$hours h ago"
        days < 7 -> "$days d ago"
        else -> "${days / 7} w ago"
    }
}