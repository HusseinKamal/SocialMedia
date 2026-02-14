package com.hussein.socialmedia.presentation.feed.mapper

import com.hussein.socialmedia.data.feed.remote.mapper.formatPostTime
import com.hussein.socialmedia.domain.feed.model.Post
import com.hussein.socialmedia.presentation.feed.model.PostUiModel

/**
 * Maps Post domain model to PostUiModel (for presentation layer)
 */
fun Post.toUiModel(): PostUiModel {
    return PostUiModel(
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
        formattedTime = formatPostTime(createdAt)
    )
}