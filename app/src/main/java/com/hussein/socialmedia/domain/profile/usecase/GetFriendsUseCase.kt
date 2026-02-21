package com.hussein.socialmedia.domain.profile.usecase

import com.hussein.socialmedia.domain.profile.model.Friend
import com.hussein.socialmedia.domain.profile.repository.FriendsRepository
import com.hussein.socialmedia.domain.util.Resource
import javax.inject.Inject

/**
 * Use case for getting friends list
 */
class GetFriendsUseCase @Inject constructor(
    private val friendsRepository: FriendsRepository
) {
    suspend operator fun invoke(limit: Int = 30, skip: Int = 0): Resource<List<Friend>> {
        return friendsRepository.getFriends(limit, skip)
    }
}