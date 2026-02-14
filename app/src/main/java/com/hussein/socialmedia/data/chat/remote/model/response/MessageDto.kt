package com.hussein.socialmedia.data.chat.remote.model.response

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for Message from API.
 */
data class MessageDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("chat_id")
    val chatId: String,

    @SerializedName("sender_id")
    val senderId: String,

    @SerializedName("sender_name")
    val senderName: String,

    @SerializedName("content")
    val content: String,

    @SerializedName("image_url")
    val imageUrl: String?,

    @SerializedName("timestamp")
    val timestamp: Long,

    @SerializedName("is_read")
    val isRead: Boolean,

    @SerializedName("is_sent_by_me")
    val isSentByMe: Boolean
)
