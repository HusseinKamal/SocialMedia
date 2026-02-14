package com.hussein.socialmedia.domain.profile.repository
import com.hussein.socialmedia.domain.profile.model.User
import com.hussein.socialmedia.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getCurrentUser(): Flow<Resource<User>>
    suspend fun getUserById(userId: String): Resource<User>
    suspend fun updateProfile(displayName: String?, bio: String?, avatarUrl: String?): Resource<User>
    suspend fun followUser(userId: String): Resource<User>
    suspend fun unfollowUser(userId: String): Resource<User>
}