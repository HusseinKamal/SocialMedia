package com.hussein.socialmedia.data.feed.remote.model.response
import com.google.gson.annotations.SerializedName


/**
 * Response wrapper for paginated posts
 */
data class PostsResponseDto(
    @param:SerializedName("posts")
    val posts: List<PostDto>,

    @param:SerializedName("next_page")
    val nextPage: Int?,

    @param:SerializedName("has_more")
    val hasMore: Boolean
)