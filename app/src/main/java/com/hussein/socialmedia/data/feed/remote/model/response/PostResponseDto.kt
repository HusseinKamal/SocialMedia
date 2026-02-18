package com.hussein.socialmedia.data.feed.remote.model.response
import com.google.gson.annotations.SerializedName


/**
 * Response wrapper for paginated posts
 */
data class PostsResponseDto(
    @SerializedName("posts")
    val posts: List<PostDto> = emptyList(),

    @SerializedName("total")
    val total: Int?,

    @SerializedName("skip")
    val skip: Int?,

    @SerializedName("limit")
    val limit: Int?,
)