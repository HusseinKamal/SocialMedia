package com.hussein.socialmedia.presentation.profile.state

data class EditProfileState(
    val username: String = "",
    val fullName: String = "",
    val bio: String? = "",
    val avatarUrl: String? = "https://www.shareicon.net/data/512x512/2016/09/15/829459_man_512x512.png",
    val isLoading: Boolean = false
)
