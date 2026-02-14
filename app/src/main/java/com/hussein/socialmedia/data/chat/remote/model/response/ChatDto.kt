package com.hussein.socialmedia.data.chat.remote.model.response

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for Chat from API.
 */
data class ChatDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("participant_id")
    val participantId: String,

    @SerializedName("participant_name")
    val participantName: String,

    @SerializedName("participant_avatar_url")
    val participantAvatarUrl: String?,

    @SerializedName("last_message")
    val lastMessage: String,

    @SerializedName("last_message_time")
    val lastMessageTime: Long,

    @SerializedName("unread_count")
    val unreadCount: Int,

    @SerializedName("is_online")
    val isOnline: Boolean
)

