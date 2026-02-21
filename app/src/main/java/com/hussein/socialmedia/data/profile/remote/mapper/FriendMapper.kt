package com.hussein.socialmedia.data.profile.remote.mapper

import com.hussein.socialmedia.data.profile.remote.model.response.FriendDto
import com.hussein.socialmedia.data.profile.remote.model.response.UserDto
import com.hussein.socialmedia.domain.profile.model.Friend
import kotlin.random.Random

/**
 * Maps UserDto to Friend domain model
 */
fun FriendDto.toFriend(): Friend {
    return Friend(
        id = id.toString(),
        firstName = firstName,
        lastName = lastName,
        fullName = "$firstName $lastName",
        username = username,
        email = email,
        phone = phone,
        avatarUrl = image,
        age = age,
        gender = gender,
        isOnline = Random.nextBoolean(), // Simulate online status
        lastSeen = if (Random.nextBoolean()) generateLastSeen() else null,
        company = company?.name,
        jobTitle = company?.title
    )
}

/**
 * Maps list of UserDto to list of Friend
 */
fun List<FriendDto>.toFriendsList(): List<Friend> {
    return this.map { it.toFriend() }
}

/**
 * Generates random last seen time
 */
private fun generateLastSeen(): String {
    val minutesAgo = Random.nextInt(1, 120)
    return when {
        minutesAgo < 60 -> "${minutesAgo}m ago"
        else -> "${minutesAgo / 60}h ago"
    }
}