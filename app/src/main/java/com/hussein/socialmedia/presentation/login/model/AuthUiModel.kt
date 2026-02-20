package com.hussein.socialmedia.presentation.login.model

import com.hussein.socialmedia.presentation.register.model.FormFieldUiModel
import com.hussein.socialmedia.presentation.register.model.PasswordFieldUiModel

/**
 * UI Model for authenticated user - optimized for presentation layer
 */
data class AuthUserUiModel(
    val id: String,
    val username: String,
    val email: String,
    val fullName: String,
    val firstName: String,
    val lastName: String,
    val avatarUrl: String?,
    val displayGender: String,
    val welcomeMessage: String
)

/**
 * UI Model for login result
 */
data class LoginResultUiModel(
    val user: AuthUserUiModel,
    val isSuccess: Boolean,
    val welcomeMessage: String
)


/**
 * UI Model for validation result
 */
data class ValidationResultUiModel(
    val isValid: Boolean,
    val errorMessage: String? = null
)

/**
 * UI Model for login form
 */
data class LoginFormUiModel(
    val username: FormFieldUiModel = FormFieldUiModel(),
    val password: PasswordFieldUiModel = PasswordFieldUiModel(),
    val isFormValid: Boolean = false,
    val canSubmit: Boolean = false
)

