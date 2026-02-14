package com.hussein.socialmedia.data.feed.remote.service

import com.hussein.socialmedia.data.feed.remote.model.response.PostDto
import com.hussein.socialmedia.data.feed.remote.model.response.PostsResponseDto
import com.hussein.socialmedia.data.feed.remote.model.request.CreatePostRequest
import com.hussein.socialmedia.data.feed.remote.model.request.UpdatePostRequest

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit API interface for Post-related endpoints.
 */
interface PostApi {

    /**
     * Get paginated feed posts
     * @param page Page number (starts at 1)
     * @param pageSize Number of posts per page
     */
    @GET("api/posts/feed")
    suspend fun getFeedPosts(
        @Query("page") page: Int,
        @Query("page_size") pageSize: Int
    ): PostsResponseDto

    /**
     * Get posts by user ID
     */
    @GET("api/posts/user/{userId}")
    suspend fun getPostsByUserId(
        @Path("userId") userId: String,
        @Query("page") page: Int,
        @Query("page_size") pageSize: Int
    ): PostsResponseDto

    /**
     * Get a single post by ID
     */
    @GET("api/posts/{postId}")
    suspend fun getPostById(
        @Path("postId") postId: String
    ): PostDto

    /**
     * Create a new post
     */
    @POST("api/posts")
    suspend fun createPost(
        @Body postRequest: CreatePostRequest
    ): PostDto

    /**
     * Update a post
     */
    @PUT("api/posts/{postId}")
    suspend fun updatePost(
        @Path("postId") postId: String,
        @Body postRequest: UpdatePostRequest
    ): PostDto

    /**
     * Delete a post
     */
    @DELETE("api/posts/{postId}")
    suspend fun deletePost(
        @Path("postId") postId: String
    )

    /**
     * Like a post
     */
    @POST("api/posts/{postId}/like")
    suspend fun likePost(
        @Path("postId") postId: String
    ): PostDto

    /**
     * Unlike a post
     */
    @DELETE("api/posts/{postId}/like")
    suspend fun unlikePost(
        @Path("postId") postId: String
    ): PostDto

    /**
     * Save a post
     */
    @POST("api/posts/{postId}/save")
    suspend fun savePost(
        @Path("postId") postId: String
    ): PostDto

    /**
     * Unsave a post
     */
    @DELETE("api/posts/{postId}/save")
    suspend fun unsavePost(
        @Path("postId") postId: String
    ): PostDto
}
