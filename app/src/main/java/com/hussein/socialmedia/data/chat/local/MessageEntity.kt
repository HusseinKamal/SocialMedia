package com.hussein.socialmedia.data.chat.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for caching Message data locally.
 */
@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "chat_id")
    val chatId: String,

    @ColumnInfo(name = "sender_id")
    val senderId: String,

    @ColumnInfo(name = "sender_name")
    val senderName: String,

    @ColumnInfo(name = "content")
    val content: String,

    @ColumnInfo(name = "image_url")
    val imageUrl: String?,

    @ColumnInfo(name = "timestamp")
    val timestamp: Long,

    @ColumnInfo(name = "is_read")
    val isRead: Boolean,

    @ColumnInfo(name = "is_sent_by_me")
    val isSentByMe: Boolean
)

/**
 * Room entity for caching Chat data locally.
 */
@Entity(tableName = "chats")
data class ChatEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "participant_id")
    val participantId: String,

    @ColumnInfo(name = "participant_name")
    val participantName: String,

    @ColumnInfo(name = "participant_avatar_url")
    val participantAvatarUrl: String?,

    @ColumnInfo(name = "last_message")
    val lastMessage: String,

    @ColumnInfo(name = "last_message_time")
    val lastMessageTime: Long,

    @ColumnInfo(name = "unread_count")
    val unreadCount: Int,

    @ColumnInfo(name = "is_online")
    val isOnline: Boolean
)