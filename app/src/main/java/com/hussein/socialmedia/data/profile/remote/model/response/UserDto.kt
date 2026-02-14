package com.hussein.socialmedia.data.profile.remote.model.response

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for User from API.
 * Maps to JSON response from the API.
 */
data class UserDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("username")
    val username: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("display_name")
    val displayName: String,

    @SerializedName("avatar_url")
    val avatarUrl: String?,

    @SerializedName("bio")
    val bio: String?,

    @SerializedName("followers_count")
    val followersCount: Int,

    @SerializedName("following_count")
    val followingCount: Int,

    @SerializedName("posts_count")
    val postsCount: Int,

    @SerializedName("is_following")
    val isFollowing: Boolean,

    @SerializedName("created_at")
    val createdAt: Long
)