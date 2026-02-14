package com.hussein.socialmedia.data.feed.remote.model.response
import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for Post from API.
 */
data class PostDto(
    @param:SerializedName("id")
    val id: String,

    @param:SerializedName("user_id")
    val userId: String,

    @param:SerializedName("username")
    val username: String,

    @param:SerializedName("user_avatar_url")
    val userAvatarUrl: String?,

    @param:SerializedName("content")
    val content: String,

    @param:SerializedName("image_url")
    val imageUrl: String?,

    @param:SerializedName("likes_count")
    val likesCount: Int,

    @param:SerializedName("comments_count")
    val commentsCount: Int,

    @param:SerializedName("shares_count")
    val sharesCount: Int,

    @param:SerializedName("is_liked")
    val isLiked: Boolean,

    @param:SerializedName("is_saved")
    val isSaved: Boolean,

    @param:SerializedName("created_at")
    val createdAt: Long,

    @param:SerializedName("updated_at")
    val updatedAt: Long?
)
