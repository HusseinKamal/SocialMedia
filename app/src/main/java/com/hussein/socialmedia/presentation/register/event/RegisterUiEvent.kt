package com.hussein.socialmedia.presentation.register.event

/**
 * UI Events for Register screen
 */
sealed class RegisterUiEvent {
    data class FirstNameChanged(val firstName: String) : RegisterUiEvent()
    data class LastNameChanged(val lastName: String) : RegisterUiEvent()
    data class UsernameChanged(val username: String) : RegisterUiEvent()
    data class EmailChanged(val email: String) : RegisterUiEvent()
    data class PasswordChanged(val password: String) : RegisterUiEvent()
    data class ConfirmPasswordChanged(val confirmPassword: String) : RegisterUiEvent()
    data object TogglePasswordVisibility : RegisterUiEvent()
    data object ToggleConfirmPasswordVisibility : RegisterUiEvent()
    data object Register : RegisterUiEvent()
    data object NavigateToLogin : RegisterUiEvent()
    data object ClearError : RegisterUiEvent()
    data object ValidateFields : RegisterUiEvent()
}