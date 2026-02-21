package com.hussein.socialmedia.domain.profile.repository

import com.hussein.socialmedia.domain.profile.model.Friend
import com.hussein.socialmedia.domain.util.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing friends/users
 */
interface FriendsRepository {

    /**
     * Get list of friends with pagination
     */
    suspend fun getFriends(limit: Int = 30, skip: Int = 0): Resource<List<Friend>>

    /**
     * Search friends by name
     */
    suspend fun searchFriends(query: String): Resource<List<Friend>>

    /**
     * Get friends as Flow for reactive updates
     */
    fun getFriendsFlow(): Flow<List<Friend>>
}