package com.hussein.socialmedia.presentation.profile.state

data class EditProfileState(
    val username: String = "",
    val fullName: String = "",
    val isLoading: Boolean = false
)
