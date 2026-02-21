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
        // 1. Trim inputs immediately
        val fName = firstName.trim()
        val lName = lastName.trim()
        val uName = username.trim()
        val eMail = email.trim()

        // 2. Perform validation on trimmed data
        if (fName.isBlank()) return Resource.Error("First name cannot be empty")
        if (lName.isBlank()) return Resource.Error("Last name cannot be empty")
        if (uName.isBlank()) return Resource.Error("Username cannot be empty")
        if (uName.length < 3) return Resource.Error("Username must be at least 3 characters")
        if (eMail.isBlank()) return Resource.Error("Email cannot be empty")

        // This will now PASS because eMail has no leading/trailing spaces
        if (!isValidEmail(eMail)) {
            return Resource.Error("Please enter a valid email address")
        }

        if (password.isBlank()) return Resource.Error("Password cannot be empty")
        if (password.length < 6) return Resource.Error("Password must be at least 6 characters")
        if (password != confirmPassword) return Resource.Error("Passwords do not match")

        val registerData = RegisterData(
            firstName = fName,
            lastName = lName,
            username = uName,
            email = eMail,
            password = password,
            confirmPassword = confirmPassword
        )

        return authRepository.register(registerData)
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"
        return email.matches(emailRegex.toRegex())
    }
}