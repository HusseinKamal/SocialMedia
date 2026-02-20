package com.hussein.socialmedia.domain.auth.usecase

import com.hussein.socialmedia.domain.auth.repository.AuthRepository
import javax.inject.Inject

/**
 * Use case for checking if user is logged in
 */
class IsLoggedInUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Boolean {
        return authRepository.isLoggedIn()
    }
}