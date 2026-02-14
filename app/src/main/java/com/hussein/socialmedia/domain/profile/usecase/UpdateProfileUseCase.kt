package com.hussein.socialmedia.domain.profile.usecase

import com.hussein.socialmedia.domain.profile.model.User
import com.hussein.socialmedia.domain.profile.repository.UserRepository
import com.hussein.socialmedia.domain.util.Resource
import javax.inject.Inject

/**
 * Use case for updating user profile.
 */
class UpdateProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        displayName: String?,
        bio: String?,
        avatarUrl: String?
    ): Resource<User> {
        return userRepository.updateProfile(displayName, bio, avatarUrl)
    }
}