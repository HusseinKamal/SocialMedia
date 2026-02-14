package com.hussein.socialmedia.domain.profile.usecase

import com.hussein.socialmedia.domain.profile.model.User
import com.hussein.socialmedia.domain.profile.repository.UserRepository
import com.hussein.socialmedia.domain.util.Resource
import javax.inject.Inject


/**
 * Use case for following a user.
 */
class FollowUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: String): Resource<User> {
        return userRepository.followUser(userId)
    }
}