package com.hussein.socialmedia.presentation.login.state

/**
 * UI State for Login screen
 */
data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val usernameError: String? = null,
    val passwordError: String? = null,
    val canSubmit: Boolean = false,
    val loginSuccess: Boolean = false
)