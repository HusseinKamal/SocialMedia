package com.hussein.socialmedia.data.feed.local
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Post operations.
 * Provides methods for local database operations with Room.
 */
@Dao
interface PostDao {

    /**
     * Get all posts ordered by creation time (newest first)
     * Returns a PagingSource for Paging 3 library
     */
    @Query("SELECT * FROM posts ORDER BY created_at DESC")
    fun getPostsPagingSource(): PagingSource<Int, PostEntity>

    /**
     * Get all posts as Flow for observing changes
     */
    @Query("SELECT * FROM posts ORDER BY created_at DESC")
    fun getPostsFlow(): Flow<List<PostEntity>>

    /**
     * Get a single post by ID
     */
    @Query("SELECT * FROM posts WHERE id = :postId")
    suspend fun getPostById(postId: String): PostEntity?

    /**
     * Get posts by user ID
     */
    @Query("SELECT * FROM posts WHERE user_id = :userId ORDER BY created_at DESC")
    fun getPostsByUserId(userId: String): Flow<List<PostEntity>>

    /**
     * Insert a list of posts
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<PostEntity>)

    /**
     * Insert a single post
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity)

    /**
     * Update a post
     */
    @Update
    suspend fun updatePost(post: PostEntity)

    /**
     * Update like status
     */
    @Query("UPDATE posts SET is_liked = :isLiked, likes_count = :likesCount WHERE id = :postId")
    suspend fun updateLikeStatus(postId: String, isLiked: Boolean, likesCount: Int)

    /**
     * Update save status
     */
    @Query("UPDATE posts SET is_saved = :isSaved WHERE id = :postId")
    suspend fun updateSaveStatus(postId: String, isSaved: Boolean)

    /**
     * Delete all posts (for refresh)
     */
    @Query("DELETE FROM posts")
    suspend fun deleteAllPosts()

    /**
     * Delete posts older than specified time
     */
    @Query("DELETE FROM posts WHERE cached_at < :timestamp")
    suspend fun deleteOldPosts(timestamp: Long)

    /**
     * Delete a single post
     */
    @Query("DELETE FROM posts WHERE id = :postId")
    suspend fun deletePost(postId: String)
}