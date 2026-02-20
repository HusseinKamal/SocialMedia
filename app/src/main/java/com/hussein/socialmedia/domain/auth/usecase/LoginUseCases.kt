package com.hussein.socialmedia.domain.auth.usecase

import com.hussein.socialmedia.domain.auth.model.AuthResult
import com.hussein.socialmedia.domain.auth.model.LoginCredentials
import com.hussein.socialmedia.domain.auth.repository.AuthRepository
import com.hussein.socialmedia.domain.util.Resource
import javax.inject.Inject

/**
 * Use case for user login
 */
class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(username: String, password: String): Resource<AuthResult> {
        // Validate inputs
        if (username.isBlank()) {
            return Resource.Error("Username cannot be empty")
        }

        if (password.isBlank()) {
            return Resource.Error("Password cannot be empty")
        }

        val credentials = LoginCredentials(
            username = username.trim(),
            password = password
        )

        return authRepository.login(credentials)
    }
}

