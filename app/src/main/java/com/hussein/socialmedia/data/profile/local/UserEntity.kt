package com.hussein.socialmedia.data.profile.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for caching User data locally.
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "username")
    val username: String,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "display_name")
    val displayName: String,

    @ColumnInfo(name = "avatar_url")
    val avatarUrl: String?,

    @ColumnInfo(name = "bio")
    val bio: String?,

    @ColumnInfo(name = "followers_count")
    val followersCount: Int,

    @ColumnInfo(name = "following_count")
    val followingCount: Int,

    @ColumnInfo(name = "posts_count")
    val postsCount: Int,

    @ColumnInfo(name = "is_following")
    val isFollowing: Boolean,

    @ColumnInfo(name = "created_at")
    val createdAt: Long,

    @ColumnInfo(name = "cached_at")
    val cachedAt: Long = System.currentTimeMillis()
)
