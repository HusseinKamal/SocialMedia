package com.hussein.socialmedia.data.feed.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for caching Post data locally.
 */
@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "username")
    val username: String,

    @ColumnInfo(name = "user_avatar_url")
    val userAvatarUrl: String?,

    @ColumnInfo(name = "content")
    val content: String,

    @ColumnInfo(name = "image_url")
    val imageUrl: String?,

    @ColumnInfo(name = "likes_count")
    val likesCount: Int,

    @ColumnInfo(name = "comments_count")
    val commentsCount: Int,

    @ColumnInfo(name = "shares_count")
    val sharesCount: Int,

    @ColumnInfo(name = "is_liked")
    val isLiked: Boolean,

    @ColumnInfo(name = "is_saved")
    val isSaved: Boolean,

    @ColumnInfo(name = "created_at")
    val createdAt: Long,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long?,

    @ColumnInfo(name = "cached_at")
    val cachedAt: Long = System.currentTimeMillis()
)