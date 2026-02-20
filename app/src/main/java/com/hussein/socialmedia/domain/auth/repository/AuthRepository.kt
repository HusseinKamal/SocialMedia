package com.hussein.socialmedia.domain.auth.repository

import com.hussein.socialmedia.domain.auth.model.AuthResult
import com.hussein.socialmedia.domain.auth.model.AuthUser
import com.hussein.socialmedia.domain.auth.model.LoginCredentials
import com.hussein.socialmedia.domain.auth.model.RegisterData
import com.hussein.socialmedia.domain.util.Resource

/**
 * Repository interface for authentication operations
 */
interface AuthRepository {

    /**
     * Login with username and password
     */
    suspend fun login(credentials: LoginCredentials): Resource<AuthResult>

    /**
     * Register new user
     */
    suspend fun register(registerData: RegisterData): Resource<AuthResult>

    /**
     * Get current authenticated user
     */
    suspend fun getCurrentUser(): Resource<AuthUser>

    /**
     * Logout current user
     */
    suspend fun logout(): Resource<Unit>

    /**
     * Check if user is logged in
     */
    fun isLoggedIn(): Boolean

    /**
     * Refresh authentication token
     */
    suspend fun refreshToken(): Resource<AuthResult>
}