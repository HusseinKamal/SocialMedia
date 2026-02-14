package com.hussein.socialmedia.domain.chat.model

/**
 * Domain model representing a Chat conversation.
 */
data class Chat(
    val id: String,
    val participantId: String,
    val participantName: String,
    val participantAvatarUrl: String?,
    val lastMessage: String,
    val lastMessageTime: Long,
    val unreadCount: Int,
    val isOnline: Boolean
)
