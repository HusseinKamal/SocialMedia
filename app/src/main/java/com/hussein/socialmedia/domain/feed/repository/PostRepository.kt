package com.hussein.socialmedia.domain.feed.repository

import androidx.paging.PagingData
import com.hussein.socialmedia.domain.feed.model.Post
import kotlinx.coroutines.flow.Flow
import com.hussein.socialmedia.domain.util.Resource

/**
 * Repository interface for Post operations.
 * Defines the contract for data operations related to posts.
 */
interface PostRepository {

    /**
     * Get paginated feed posts
     * Returns Flow of PagingData for infinite scrolling
     */
    fun getFeedPosts(): Flow<PagingData<Post>>

    /**
     * Get posts by user ID
     */
    fun getPostsByUserId(userId: String): Flow<Resource<List<Post>>>

    /**
     * Get a single post by ID
     */
    suspend fun getPostById(postId: String): Resource<Post>

    /**
     * Create a new post
     */
    suspend fun createPost(content: String, imageUrl: String?): Resource<Post>

    /**
     * Update a post
     */
    suspend fun updatePost(postId: String, content: String, imageUrl: String?): Resource<Post>

    /**
     * Delete a post
     */
    suspend fun deletePost(postId: String): Resource<Unit>

    /**
     * Like a post
     */
    suspend fun likePost(postId: String): Resource<Post>

    /**
     * Unlike a post
     */
    suspend fun unlikePost(postId: String): Resource<Post>

    /**
     * Save a post
     */
    suspend fun savePost(postId: String): Resource<Post>

    /**
     * Unsave a post
     */
    suspend fun unsavePost(postId: String): Resource<Post>
}