package com.hussein.socialmedia.presentation.register.model

import com.hussein.socialmedia.presentation.login.model.AuthUserUiModel


/**
 * UI Model for registration result
 */
data class RegisterResultUiModel(
    val user: AuthUserUiModel,
    val isSuccess: Boolean,
    val successMessage: String
)

/**
 * UI Model for form field state
 */
data class FormFieldUiModel(
    val value: String = "",
    val error: String? = null,
    val isValid: Boolean = true,
    val isTouched: Boolean = false
)

/**
 * UI Model for password field state
 */
data class PasswordFieldUiModel(
    val value: String = "",
    val isVisible: Boolean = false,
    val error: String? = null,
    val isValid: Boolean = true,
    val isTouched: Boolean = false,
    val strengthLevel: PasswordStrength = PasswordStrength.NONE,
    val strengthMessage: String = ""
)
/**
 * UI Model for register form
 */
data class RegisterFormUiModel(
    val firstName: FormFieldUiModel = FormFieldUiModel(),
    val lastName: FormFieldUiModel = FormFieldUiModel(),
    val username: FormFieldUiModel = FormFieldUiModel(),
    val email: FormFieldUiModel = FormFieldUiModel(),
    val password: PasswordFieldUiModel = PasswordFieldUiModel(),
    val confirmPassword: PasswordFieldUiModel = PasswordFieldUiModel(),
    val isFormValid: Boolean = false,
    val canSubmit: Boolean = false
)
/**
 * Password strength levels
 */
enum class PasswordStrength {
    NONE,
    WEAK,
    MEDIUM,
    STRONG
}