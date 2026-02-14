package com.hussein.socialmedia.data.chat.remote.mapper

import com.hussein.socialmedia.data.chat.local.ChatEntity
import com.hussein.socialmedia.data.chat.local.MessageEntity
import com.hussein.socialmedia.data.chat.remote.model.response.ChatDto
import com.hussein.socialmedia.data.chat.remote.model.response.MessageDto
import com.hussein.socialmedia.domain.chat.model.Chat
import com.hussein.socialmedia.domain.chat.model.Message
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Mapper functions for converting between Chat and Message model types.
 */

// Chat Mappers

fun ChatDto.toEntity(): ChatEntity {
    return ChatEntity(
        id = id,
        participantId = participantId,
        participantName = participantName,
        participantAvatarUrl = participantAvatarUrl,
        lastMessage = lastMessage,
        lastMessageTime = lastMessageTime,
        unreadCount = unreadCount,
        isOnline = isOnline
    )
}

fun ChatDto.toDomain(): Chat {
    return Chat(
        id = id,
        participantId = participantId,
        participantName = participantName,
        participantAvatarUrl = participantAvatarUrl,
        lastMessage = lastMessage,
        lastMessageTime = lastMessageTime,
        unreadCount = unreadCount,
        isOnline = isOnline
    )
}

fun ChatEntity.toDomain(): Chat {
    return Chat(
        id = id,
        participantId = participantId,
        participantName = participantName,
        participantAvatarUrl = participantAvatarUrl,
        lastMessage = lastMessage,
        lastMessageTime = lastMessageTime,
        unreadCount = unreadCount,
        isOnline = isOnline
    )
}

// Message Mappers

fun MessageDto.toEntity(): MessageEntity {
    return MessageEntity(
        id = id,
        chatId = chatId,
        senderId = senderId,
        senderName = senderName,
        content = content,
        imageUrl = imageUrl,
        timestamp = timestamp,
        isRead = isRead,
        isSentByMe = isSentByMe
    )
}

fun MessageDto.toDomain(): Message {
    return Message(
        id = id,
        chatId = chatId,
        senderId = senderId,
        senderName = senderName,
        content = content,
        imageUrl = imageUrl,
        timestamp = timestamp,
        isRead = isRead,
        isSentByMe = isSentByMe
    )
}

fun MessageEntity.toDomain(): Message {
    return Message(
        id = id,
        chatId = chatId,
        senderId = senderId,
        senderName = senderName,
        content = content,
        imageUrl = imageUrl,
        timestamp = timestamp,
        isRead = isRead,
        isSentByMe = isSentByMe
    )
}

// Time formatting helpers

fun formatChatTime(timestamp: Long): String {
    val currentTime = System.currentTimeMillis()
    val diff = currentTime - timestamp

    return when {
        diff < 60_000 -> "Just now"
        diff < 3_600_000 -> "${diff / 60_000}m"
        diff < 86_400_000 -> "${diff / 3_600_000}h"
        else -> {
            val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
            dateFormat.format(Date(timestamp))
        }
    }
}

fun formatMessageTime(timestamp: Long): String {
    val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return dateFormat.format(Date(timestamp))
}