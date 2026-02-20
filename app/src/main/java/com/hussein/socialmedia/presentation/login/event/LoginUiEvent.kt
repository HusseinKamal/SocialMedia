package com.hussein.socialmedia.presentation.login.event

/**
 * UI Events for Login screen
 */
sealed class LoginUiEvent {
    data class UsernameChanged(val username: String) : LoginUiEvent()
    data class PasswordChanged(val password: String) : LoginUiEvent()
    data object TogglePasswordVisibility : LoginUiEvent()
    data object Login : LoginUiEvent()
    data object NavigateToRegister : LoginUiEvent()
    data object ClearError : LoginUiEvent()
    data object ValidateFields : LoginUiEvent()
}