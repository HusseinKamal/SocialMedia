package com.hussein.socialmedia.data.profile.remote.repository

import com.hussein.socialmedia.data.database.SocialMediaDatabase
import com.hussein.socialmedia.data.profile.remote.mapper.toDomain
import com.hussein.socialmedia.data.profile.remote.mapper.toEntity
import com.hussein.socialmedia.data.profile.remote.model.request.UpdateProfileRequest
import com.hussein.socialmedia.data.profile.remote.service.UserApi
import com.hussein.socialmedia.domain.profile.model.User
import com.hussein.socialmedia.domain.profile.repository.UserRepository
import com.hussein.socialmedia.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi,
    private val database: SocialMediaDatabase
) : UserRepository {

    private val userDao = database.userDao()

    override fun getCurrentUser(): Flow<Resource<User>> = flow {
        emit(Resource.Loading)
        try {
            val userDto = userApi.getCurrentUser()
            val user = userDto.toDomain()
            userDao.insertUser(userDto.toEntity())
            emit(Resource.Success(user))
        } catch (e: Exception) {
            emit(Resource.Error("Failed to load user: ${e.message}", e))
        }
    }

    override suspend fun getUserById(userId: String): Resource<User> {
        return try {
            val userDto = userApi.getUserById(userId)
            val user = userDto.toDomain()
            userDao.insertUser(userDto.toEntity())
            Resource.Success(user)
        } catch (e: Exception) {
            val cachedUser = userDao.getUserById(userId)
            if (cachedUser != null) {
                Resource.Success(cachedUser.toDomain())
            } else {
                Resource.Error("Failed to load user: ${e.message}", e)
            }
        }
    }

    override suspend fun updateProfile(
        displayName: String?,
        bio: String?,
        avatarUrl: String?
    ): Resource<User> {
        return try {
            val request = UpdateProfileRequest(displayName, bio, avatarUrl)
            val userDto = userApi.updateProfile(request)
            val user = userDto.toDomain()
            userDao.insertUser(userDto.toEntity())
            Resource.Success(user)
        } catch (e: Exception) {
            Resource.Error("Failed to update profile: ${e.message}", e)
        }
    }

    override suspend fun followUser(userId: String): Resource<User> {
        return try {
            val userDto = userApi.followUser(userId)
            val user = userDto.toDomain()
            userDao.updateFollowStatus(userId, true, userDto.followersCount)
            Resource.Success(user)
        } catch (e: Exception) {
            Resource.Error("Failed to follow user: ${e.message}", e)
        }
    }

    override suspend fun unfollowUser(userId: String): Resource<User> {
        return try {
            val userDto = userApi.unfollowUser(userId)
            val user = userDto.toDomain()
            userDao.updateFollowStatus(userId, false, userDto.followersCount)
            Resource.Success(user)
        } catch (e: Exception) {
            Resource.Error("Failed to unfollow user: ${e.message}", e)
        }
    }
}