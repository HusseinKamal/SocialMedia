package com.hussein.socialmedia.domain.profile.usecase

import com.hussein.socialmedia.domain.profile.model.Friend
import com.hussein.socialmedia.domain.profile.repository.FriendsRepository
import com.hussein.socialmedia.domain.util.Resource
import javax.inject.Inject

/**
 * Use case for searching friends
 */
class SearchFriendsUseCase @Inject constructor(
    private val friendsRepository: FriendsRepository
) {
    suspend operator fun invoke(query: String): Resource<List<Friend>> {
        if (query.isBlank()) {
            return Resource.Error("Search query cannot be empty")
        }

        return friendsRepository.searchFriends(query.trim())
    }
}