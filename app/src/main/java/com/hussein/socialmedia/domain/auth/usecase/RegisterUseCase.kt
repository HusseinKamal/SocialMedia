package com.hussein.socialmedia.domain.auth.usecase

import com.hussein.socialmedia.domain.auth.model.AuthResult
import com.hussein.socialmedia.domain.auth.model.RegisterData
import com.hussein.socialmedia.domain.auth.repository.AuthRepository
import com.hussein.socialmedia.domain.util.Resource
import javax.inject.Inject


/**
 * Use case for user registration
 */
class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        firstName: String,
        lastName: String,
        username: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Resource<AuthResult> {
        // Validate inputs
        if (firstName.isBlank()) {
            return Resource.Error("First name cannot be empty")
        }

        if (lastName.isBlank()) {
            return Resource.Error("Last name cannot be empty")
        }

        if (username.isBlank()) {
            return Resource.Error("Username cannot be empty")
        }

        if (username.length < 3) {
            return Resource.Error("Username must be at least 3 characters")
        }

        if (email.isBlank()) {
            return Resource.Error("Email cannot be empty")
        }

        if (!isValidEmail(email)) {
            return Resource.Error("Please enter a valid email address")
        }

        if (password.isBlank()) {
            return Resource.Error("Password cannot be empty")
        }

        if (password.length < 6) {
            return Resource.Error("Password must be at least 6 characters")
        }

        if (password != confirmPassword) {
            return Resource.Error("Passwords do not match")
        }

        val registerData = RegisterData(
            firstName = firstName.trim(),
            lastName = lastName.trim(),
            username = username.trim(),
            email = email.trim(),
            password = password,
            confirmPassword = confirmPassword
        )

        return authRepository.register(registerData)
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}