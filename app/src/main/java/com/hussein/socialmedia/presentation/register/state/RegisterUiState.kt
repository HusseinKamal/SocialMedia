package com.hussein.socialmedia.presentation.register.state

import com.hussein.socialmedia.presentation.register.model.PasswordStrength

/**
 * UI State for Register screen
 */
data class RegisterUiState(
    val firstName: String = "",
    val lastName: String = "",
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val firstNameError: String? = null,
    val lastNameError: String? = null,
    val usernameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val passwordStrength: PasswordStrength = PasswordStrength.NONE,
    val passwordStrengthMessage: String = "",
    val canSubmit: Boolean = false,
    val registerSuccess: Boolean = false
)