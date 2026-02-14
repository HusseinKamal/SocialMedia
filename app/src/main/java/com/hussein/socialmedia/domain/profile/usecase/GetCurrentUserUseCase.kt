package com.hussein.socialmedia.domain.profile.usecase

import com.hussein.socialmedia.domain.profile.model.User
import com.hussein.socialmedia.domain.profile.repository.UserRepository
import com.hussein.socialmedia.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting current user profile.
 */
class GetCurrentUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<Resource<User>> {
        return userRepository.getCurrentUser()
    }
}