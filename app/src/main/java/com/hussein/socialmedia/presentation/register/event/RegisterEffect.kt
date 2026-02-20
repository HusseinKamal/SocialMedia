package com.hussein.socialmedia.presentation.register.event

import com.hussein.socialmedia.presentation.login.model.AuthUserUiModel

/**
 * Side effects for Register screen
 */
sealed class RegisterEffect {
    data class NavigateToHome(val user: AuthUserUiModel) : RegisterEffect()
    data object NavigateToLogin : RegisterEffect()
}