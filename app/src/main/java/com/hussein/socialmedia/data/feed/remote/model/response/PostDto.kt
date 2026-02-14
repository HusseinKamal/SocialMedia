package com.hussein.socialmedia.data.feed.remote.model.response
import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for Post from API.
 */
data class PostDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("user_id")
    val userId: String,

    @SerializedName("username")
    val username: String,

    @SerializedName("user_avatar_url")
    val userAvatarUrl: String?,

    @SerializedName("content")
    val content: String,

    @SerializedName("image_url")
    val imageUrl: String?,

    @SerializedName("likes_count")
    val likesCount: Int,

    @SerializedName("comments_count")
    val commentsCount: Int,

    @SerializedName("shares_count")
    val sharesCount: Int,

    @SerializedName("is_liked")
    val isLiked: Boolean,

    @SerializedName("is_saved")
    val isSaved: Boolean,

    @SerializedName("created_at")
    val createdAt: Long,

    @SerializedName("updated_at")
    val updatedAt: Long?
)
