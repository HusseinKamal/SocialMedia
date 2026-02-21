package com.hussein.socialmedia.presentation.profile.mapper

import com.hussein.socialmedia.domain.auth.model.AuthUser
import com.hussein.socialmedia.domain.profile.model.User

fun User.toAuthUser(): AuthUser {
    return AuthUser(
        id = this.id,
        username = this.username,
        email = this.email,
        firstName = "", //  User domain model doesn't have firstName
        lastName = "", //  User domain model doesn't have lastName
        fullName = this.displayName,
        image = this.avatarUrl,
        gender = "" // User domain model doesn't have gender
    )
}
