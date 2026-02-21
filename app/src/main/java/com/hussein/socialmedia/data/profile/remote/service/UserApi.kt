package com.hussein.socialmedia.data.profile.remote.service

import com.hussein.socialmedia.data.profile.remote.model.request.UpdateProfileRequest
import com.hussein.socialmedia.data.profile.remote.model.response.UserDto
import com.hussein.socialmedia.data.profile.remote.model.response.UsersResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit API interface for User-related endpoints.
 */
interface UserApi {

    /**
     * Get current user profile
     */
    @GET("api/users/me")
    suspend fun getCurrentUser(): UserDto

    /**
     * Get user by ID
     */
    @GET("api/users/{userId}")
    suspend fun getUserById(
        @Path("userId") userId: String
    ): UserDto

    /**
     * Get user by username
     */
    @GET("api/users/username/{username}")
    suspend fun getUserByUsername(
        @Path("username") username: String
    ): UserDto

    /**
     * Update user profile
     */
    @PUT("api/users/me")
    suspend fun updateProfile(
        @Body updateRequest: UpdateProfileRequest
    ): UserDto

    /**
     * Follow a user
     */
    @POST("api/users/{userId}/follow")
    suspend fun followUser(
        @Path("userId") userId: String
    ): UserDto

    /**
     * Unfollow a user
     */
    @DELETE("api/users/{userId}/follow")
    suspend fun unfollowUser(
        @Path("userId") userId: String
    ): UserDto

    /**
     * Get followers list
     */
    @GET("api/users/{userId}/followers")
    suspend fun getFollowers(
        @Path("userId") userId: String
    ): List<UserDto>

    /**
     * Get following list
     */
    @GET("api/users/{userId}/following")
    suspend fun getFollowing(
        @Path("userId") userId: String
    ): List<UserDto>



    /**
     * Get all users with pagination
     * GET https://dummyjson.com/users
     */
    @GET("users")
    suspend fun getUsers(
        @Query("limit") limit: Int = 30,
        @Query("skip") skip: Int = 0
    ): UsersResponse

    /**
     * Search users by name
     * GET https://dummyjson.com/users/search?q=John
     */
    @GET("users/search")
    suspend fun searchUsers(
        @Query("q") query: String,
        @Query("limit") limit: Int = 30
    ): UsersResponse
}