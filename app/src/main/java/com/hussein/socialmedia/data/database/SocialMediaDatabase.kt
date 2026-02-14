package com.hussein.socialmedia.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hussein.socialmedia.data.chat.local.ChatDao
import com.hussein.socialmedia.data.chat.local.ChatEntity
import com.hussein.socialmedia.data.chat.local.MessageDao
import com.hussein.socialmedia.data.chat.local.MessageEntity
import com.hussein.socialmedia.data.feed.local.PostDao
import com.hussein.socialmedia.data.feed.local.PostEntity
import com.hussein.socialmedia.data.profile.local.UserDao
import com.hussein.socialmedia.data.profile.local.UserEntity

/**
 * Room Database for the Social Media App.
 * Provides local caching and offline support.
 */
@Database(
    entities = [
        PostEntity::class,
        UserEntity::class,
        MessageEntity::class,
        ChatEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SocialMediaDatabase : RoomDatabase() {

    abstract fun postDao(): PostDao
    abstract fun userDao(): UserDao
    abstract fun messageDao(): MessageDao
    abstract fun chatDao(): ChatDao

    companion object {
        const val DATABASE_NAME = "social_media_db"
    }
}