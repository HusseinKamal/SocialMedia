package com.hussein.socialmedia.data.profile.remote.repository

import com.hussein.socialmedia.data.profile.remote.mapper.toFriendsList
import com.hussein.socialmedia.data.profile.remote.service.UserApi
import com.hussein.socialmedia.domain.profile.model.Friend
import com.hussein.socialmedia.domain.profile.repository.FriendsRepository
import com.hussein.socialmedia.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of FriendsRepository
 */
@Singleton
class FriendsRepositoryImpl @Inject constructor(
    private val friendsApi: UserApi
) : FriendsRepository {

    private var cachedFriends: List<Friend>? = null

    override suspend fun getFriends(limit: Int, skip: Int): Resource<List<Friend>> {
        return try {
            val response = friendsApi.getUsers(limit, skip)
            val friends = response.users.toFriendsList()

            // Cache the friends
            cachedFriends = friends

            Resource.Success(friends)
        } catch (e: HttpException) {
            val errorMessage = when (e.code()) {
                404 -> "Users not found"
                500 -> "Server error. Please try again later."
                else -> "Failed to load friends: ${e.message()}"
            }
            Resource.Error(errorMessage, e)
        } catch (e: IOException) {
            Resource.Error("Network error. Please check your connection.", e)
        } catch (e: Exception) {
            Resource.Error("An unexpected error occurred: ${e.message}", e)
        }
    }

    override suspend fun searchFriends(query: String): Resource<List<Friend>> {
        return try {
            if (query.isBlank()) {
                return getFriends()
            }

            val response = friendsApi.searchUsers(query)
            val friends = response.users.toFriendsList()

            Resource.Success(friends)
        } catch (e: HttpException) {
            val errorMessage = when (e.code()) {
                404 -> "No users found matching \"$query\""
                else -> "Search failed: ${e.message()}"
            }
            Resource.Error(errorMessage, e)
        } catch (e: IOException) {
            Resource.Error("Network error. Please check your connection.", e)
        } catch (e: Exception) {
            Resource.Error("Search failed: ${e.message}", e)
        }
    }

    override fun getFriendsFlow(): Flow<List<Friend>> = flow {
        // First emit cached data if available
        cachedFriends?.let { emit(it) }

        // Then fetch fresh data
        when (val result = getFriends()) {
            is Resource.Success -> emit(result.data)
            else -> {
                // If fetch fails and no cache, emit empty list
                if (cachedFriends == null) {
                    emit(emptyList())
                }
            }
        }
    }
}