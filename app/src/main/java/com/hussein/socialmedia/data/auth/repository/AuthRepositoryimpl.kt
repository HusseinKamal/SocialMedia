package com.hussein.socialmedia.data.auth.repository

import com.hussein.socialmedia.data.auth.mapper.toAuthResult
import com.hussein.socialmedia.data.auth.mapper.toAuthUser
import com.hussein.socialmedia.data.auth.mapper.toUserSession
import com.hussein.socialmedia.data.auth.model.request.LoginRequest
import com.hussein.socialmedia.data.auth.model.request.RefreshTokenRequest
import com.hussein.socialmedia.data.auth.model.request.RegisterRequest
import com.hussein.socialmedia.data.auth.service.AuthApi
import com.hussein.socialmedia.domain.auth.model.AuthResult
import com.hussein.socialmedia.domain.auth.model.AuthUser
import com.hussein.socialmedia.domain.auth.model.LoginCredentials
import com.hussein.socialmedia.domain.auth.model.RegisterData
import com.hussein.socialmedia.domain.auth.repository.AuthRepository
import com.hussein.socialmedia.domain.setting.repository.PreferencesRepository
import com.hussein.socialmedia.domain.util.Resource
import com.hussein.socialmedia.domain.util.Resource.*
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of AuthRepository using DummyJSON API
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val preferencesRepository: PreferencesRepository
) : AuthRepository {

    override suspend fun login(credentials: LoginCredentials): Resource<AuthResult> {
        return try {
            val request = LoginRequest(
                username = credentials.username,
                password = credentials.password
            )

            val response = authApi.login(request)
            val authResult = response.toAuthResult()

            // Save session to encrypted preferences
            preferencesRepository.saveUserSession(response.toUserSession())

            Success(authResult)
        } catch (e: HttpException) {
            val errorMessage = when (e.code()) {
                400 -> "Invalid username or password"
                401 -> "Invalid credentials"
                404 -> "User not found"
                else -> "Login failed: ${e.message()}"
            }
            Error(errorMessage, e)
        } catch (e: IOException) {
            Error("Network error. Please check your connection.", e)
        } catch (e: Exception) {
            Error("An unexpected error occurred: ${e.message}", e)
        }
    }

    override suspend fun register(registerData: RegisterData): Resource<AuthResult> {
        return try {
            // Validate passwords match
            if (registerData.password != registerData.confirmPassword) {
                return Error("Passwords do not match")
            }

            // Validate password strength
            if (registerData.password.length < 6) {
                return Error("Password must be at least 6 characters")
            }

            val request = RegisterRequest(
                firstName = registerData.firstName,
                lastName = registerData.lastName,
                username = registerData.username,
                email = registerData.email,
                password = registerData.password,
                gender = registerData.gender
            )

            // Register user (DummyJSON simulates this)
            val userResponse = authApi.register(request)
            Resource.Success(userResponse)

            // After registration, automatically login
            val loginResult = login(
                LoginCredentials(
                    username = registerData.username,
                    password = registerData.password
                )
            )

            loginResult
        } catch (e: HttpException) {
            Error("Registration failed: ${e.message()}", e)
        } catch (e: IOException) {
            Error("Network error. Please check your connection.", e)
        } catch (e: Exception) {
            Error("Registration failed: ${e.message}", e)
        }
    }

    override suspend fun getCurrentUser(): Resource<AuthUser> {
        return try {
            val userResponse = authApi.getCurrentUser()
            Success(userResponse.toAuthUser())
        } catch (e: HttpException) {
            if (e.code() == 401) {
                // Token expired, try to refresh
                when (val refreshResult = refreshToken()) {
                    is Success -> getCurrentUser()
                    is Error -> Error("Session expired. Please login again.", e)
                    else -> Error("Authentication failed", e)
                }
            } else {
                Error("Failed to get user info: ${e.message()}", e)
            }
        } catch (e: IOException) {
            Error("Network error. Please check your connection.", e)
        } catch (e: Exception) {
            Error("Failed to get user info: ${e.message}", e)
        }
    }

    override suspend fun logout(): Resource<Unit> {
        return try {
            preferencesRepository.clearUserSession()
            Success(Unit)
        } catch (e: Exception) {
            Error("Logout failed: ${e.message}", e)
        }
    }

    override fun isLoggedIn(): Boolean {
        return preferencesRepository.isLoggedIn()
    }

    override suspend fun refreshToken(): Resource<AuthResult> {
        return try {
            val session = preferencesRepository.getUserSession()
                ?: return Error("No active session")

            val request = RefreshTokenRequest(
                refreshToken = session.refreshToken
            )

            val response = authApi.refreshToken(request)
            val authResult = response.toAuthResult()

            // Update session with new token
            preferencesRepository.saveUserSession(response.toUserSession())

            Success(authResult)
        } catch (e: Exception) {
            Resource.Error("Failed to refresh token: ${e.message}", e)
        }
    }
}