package com.hussein.socialmedia.data.auth.service

import com.hussein.socialmedia.data.auth.model.request.LoginRequest
import com.hussein.socialmedia.data.auth.model.request.RefreshTokenRequest
import com.hussein.socialmedia.data.auth.model.response.LoginResponse
import com.hussein.socialmedia.data.auth.model.request.RegisterRequest
import com.hussein.socialmedia.data.auth.model.response.UserResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * API interface for authentication endpoints using DummyJSON API.
 * Base URL: https://dummyjson.com
 */
interface AuthApi {

    /**
     * Login with username and password
     * POST https://dummyjson.com/auth/login
     */
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse

    /**
     * Get current authenticated user
     * GET https://dummyjson.com/auth/me
     * Requires: Authorization: Bearer <token>
     */
    @GET("auth/me")
    suspend fun getCurrentUser(): UserResponse

    /**
     * Refresh auth token
     * POST https://dummyjson.com/auth/refresh
     */
    @POST("auth/refresh")
    suspend fun refreshToken(
        @Body refreshToken: RefreshTokenRequest
    ): LoginResponse

    /**
     * Register new user (DummyJSON doesn't have real registration,
     * so we'll simulate it with a POST to /users/add)
     */
    @POST("users/add")
    suspend fun register(
        @Body request: RegisterRequest
    ): UserResponse
}