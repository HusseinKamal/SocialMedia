package com.hussein.socialmedia.presentation.login.event

import com.hussein.socialmedia.presentation.login.model.AuthUserUiModel

/**
 * Side effects for Login screen
 */
sealed class LoginEffect {
    data class NavigateToHome(val user: AuthUserUiModel) : LoginEffect()
    data object NavigateToRegister : LoginEffect()
}