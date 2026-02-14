package com.hussein.socialmedia.data.chat.remote.model.response

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for Chat from API.
 */
data class ChatDto(
    @param:SerializedName("id")
    val id: String,

    @param:SerializedName("participant_id")
    val participantId: String,

    @param:SerializedName("participant_name")
    val participantName: String,

    @param:SerializedName("participant_avatar_url")
    val participantAvatarUrl: String?,

    @param:SerializedName("last_message")
    val lastMessage: String,

    @param:SerializedName("last_message_time")
    val lastMessageTime: Long,

    @param:SerializedName("unread_count")
    val unreadCount: Int,

    @param:SerializedName("is_online")
    val isOnline: Boolean
)

