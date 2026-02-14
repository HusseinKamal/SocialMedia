package com.hussein.socialmedia.data.profile.local


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for User operations.
 */
@Dao
interface UserDao {

    /**
     * Get user by ID as Flow
     */
    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserByIdFlow(userId: String): Flow<UserEntity?>

    /**
     * Get user by ID (suspend)
     */
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: String): UserEntity?

    /**
     * Get user by username
     */
    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUserByUsername(username: String): UserEntity?

    /**
     * Insert a user
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    /**
     * Insert multiple users
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    /**
     * Update a user
     */
    @Update
    suspend fun updateUser(user: UserEntity)

    /**
     * Update follow status
     */
    @Query("UPDATE users SET is_following = :isFollowing, followers_count = :followersCount WHERE id = :userId")
    suspend fun updateFollowStatus(userId: String, isFollowing: Boolean, followersCount: Int)

    /**
     * Delete user by ID
     */
    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUser(userId: String)

    /**
     * Delete all users
     */
    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
}