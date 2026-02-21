package com.hussein.socialmedia.domain.auth.model

/**
 * Domain model for authenticated user
 */
data class AuthUser(
    val id: String = "1",
    val username: String = "HusseinAmin",
    val email: String = "test@example.com",
    val firstName: String = "Hussein",
    val lastName: String= "Kamal",
    val fullName: String = "Hussein Kamal",
    val image: String? = "https://www.shareicon.net/data/512x512/2016/09/15/829459_man_512x512.png",
    val gender: String? = "male"
)
