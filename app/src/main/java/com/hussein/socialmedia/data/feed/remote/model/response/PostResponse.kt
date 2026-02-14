package com.hussein.socialmedia.data.feed.remote.model.response
import com.google.gson.annotations.SerializedName


/**
 * Response wrapper for paginated posts
 */
data class PostsResponseDto(
    @SerializedName("posts")
    val posts: List<PostDto>,

    @SerializedName("next_page")
    val nextPage: Int?,

    @SerializedName("has_more")
    val hasMore: Boolean
)