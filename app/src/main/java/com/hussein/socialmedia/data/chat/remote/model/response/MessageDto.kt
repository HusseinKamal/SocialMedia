package com.hussein.socialmedia.data.chat.remote.model.response

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for Message from API.
 */
data class MessageDto(
    @param:SerializedName("id")
    val id: String,

    @param:SerializedName("chat_id")
    val chatId: String,

    @param:SerializedName("sender_id")
    val senderId: String,

    @param:SerializedName("sender_name")
    val senderName: String,

    @param:SerializedName("content")
    val content: String,

    @param:SerializedName("image_url")
    val imageUrl: String?,

    @param:SerializedName("timestamp")
    val timestamp: Long,

    @param:SerializedName("is_read")
    val isRead: Boolean,

    @param:SerializedName("is_sent_by_me")
    val isSentByMe: Boolean
)
