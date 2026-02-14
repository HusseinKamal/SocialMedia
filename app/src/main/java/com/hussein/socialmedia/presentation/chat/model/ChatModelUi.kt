package com.hussein.socialmedia.presentation.chat.model


/**
 * UI Model for Chat.
 */
data class ChatUiModel(
    val id: String,
    val participantId: String,
    val participantName: String,
    val participantAvatarUrl: String?,
    val lastMessage: String,
    val formattedTime: String,
    val unreadCount: Int,
    val isOnline: Boolean
)

/**
 * UI Model for Message.
 */
data class MessageUiModel(
    val id: String,
    val chatId: String,
    val senderId: String,
    val senderName: String,
    val content: String,
    val imageUrl: String?,
    val formattedTime: String,
    val isRead: Boolean,
    val isSentByMe: Boolean
)