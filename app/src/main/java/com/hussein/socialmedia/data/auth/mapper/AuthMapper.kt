package com.hussein.socialmedia.data.auth.mapper

import com.hussein.socialmedia.data.auth.model.response.LoginResponse
import com.hussein.socialmedia.data.auth.model.response.UserResponse
import com.hussein.socialmedia.domain.auth.model.AuthResult
import com.hussein.socialmedia.domain.auth.model.AuthUser
import com.hussein.socialmedia.domain.setting.model.UserSession

/**
 * Maps LoginResponse DTO to AuthResult domain model
 */
fun LoginResponse.toAuthResult(): AuthResult {
    return AuthResult(
        user = AuthUser(
            id = id.toString(),
            username = username,
            email = email,
            firstName = firstName,
            lastName = lastName,
            fullName = "$firstName $lastName",
            image = image,
            gender = gender
        ),
        token = token,
        refreshToken = refreshToken
    )
}

/**
 * Maps LoginResponse to UserSession for encrypted storage
 */
fun LoginResponse.toUserSession(): UserSession {
    return UserSession(
        authToken = token,
        refreshToken = refreshToken,
        userId = id.toString(),
        email = email,
        isLoggedIn = true
    )
}

/**
 * Maps UserResponse DTO to AuthUser domain model
 */
fun UserResponse.toAuthUser(): AuthUser {
    return AuthUser(
        id = id.toString(),
        username = username,
        email = email,
        firstName = firstName,
        lastName = lastName,
        fullName = "$firstName $lastName",
        image = image,
        gender = gender
    )
}

/**
 * Maps AuthResult to UserSession
 */
fun AuthResult.toUserSession(): UserSession {
    return UserSession(
        authToken = token,
        refreshToken = refreshToken,
        userId = user.id,
        email = user.email,
        isLoggedIn = true
    )
}