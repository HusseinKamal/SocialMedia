package com.hussein.socialmedia.data.feed.remote.model.response
import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for Post from API.
 */

data class PostDto(

    @SerializedName("id")
    val id: Int?,

    @SerializedName("title")
    val title: String?,

    @SerializedName("body")
    val body: String?,

    @SerializedName("tags")
    val tags: List<String> = emptyList(),

    @SerializedName("reactions")
    val reactions: ReactionsDto?,

    @SerializedName("views")
    val views: Int?,

    @SerializedName("userId")
    val userId: Int?
)

