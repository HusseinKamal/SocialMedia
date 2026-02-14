package com.hussein.socialmedia.data.profile.remote.model.response

/**
 * Data Transfer Object for User from API.
 * Maps to JSON response from the API.
 */
data class UserDto(
    @param:SerializedName("id")
    val id: String,

    @param:SerializedName("username")
    val username: String,

    @param:SerializedName("email")
    val email: String,

    @param:SerializedName("display_name")
    val displayName: String,

    @param:SerializedName("avatar_url")
    val avatarUrl: String?,

    @param:SerializedName("bio")
    val bio: String?,

    @param:SerializedName("followers_count")
    val followersCount: Int,

    @param:SerializedName("following_count")
    val followingCount: Int,

    @param:SerializedName("posts_count")
    val postsCount: Int,

    @param:SerializedName("is_following")
    val isFollowing: Boolean,

    @param:SerializedName("created_at")
    val createdAt: Long
)