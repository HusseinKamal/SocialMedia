package com.hussein.socialmedia.domain.chat.model

/**
 * Domain model representing a Message in a chat.
 */
data class Message(
    val id: String,
    val chatId: String,
    val senderId: String,
    val senderName: String,
    val content: String,
    val imageUrl: String?,
    val timestamp: Long,
    val isRead: Boolean,
    val isSentByMe: Boolean
)
