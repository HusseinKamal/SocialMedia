package com.hussein.socialmedia.di
import android.content.Context
import androidx.room.Room
import com.hussein.socialmedia.data.chat.local.ChatDao
import com.hussein.socialmedia.data.chat.local.MessageDao
import com.hussein.socialmedia.data.database.SocialMediaDatabase
import com.hussein.socialmedia.data.feed.local.PostDao
import com.hussein.socialmedia.data.profile.local.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing database dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideSocialMediaDatabase(
        @ApplicationContext context: Context
    ): SocialMediaDatabase {
        return Room.databaseBuilder(
            context,
            SocialMediaDatabase::class.java,
            SocialMediaDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun providePostDao(database: SocialMediaDatabase): PostDao {
        return database.postDao()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: SocialMediaDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    @Singleton
    fun provideMessageDao(database: SocialMediaDatabase): MessageDao {
        return database.messageDao()
    }

    @Provides
    @Singleton
    fun provideChatDao(database: SocialMediaDatabase): ChatDao {
        return database.chatDao()
    }
}