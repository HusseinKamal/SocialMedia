package com.hussein.socialmedia.presentation.profile.state

import com.hussein.socialmedia.domain.auth.model.AuthUser

data class ProfileState(
    val user: AuthUser? = null,
    val isLoading: Boolean = false
)
