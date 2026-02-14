package com.hussein.socialmedia.data.chat.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Chat operations.
 */
@Dao
interface ChatDao {

    /**
     * Get all chats ordered by last message time
     */
    @Query("SELECT * FROM chats ORDER BY last_message_time DESC")
    fun getAllChatsFlow(): Flow<List<ChatEntity>>

    /**
     * Get chat by ID
     */
    @Query("SELECT * FROM chats WHERE id = :chatId")
    suspend fun getChatById(chatId: String): ChatEntity?

    /**
     * Insert a chat
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChat(chat: ChatEntity)

    /**
     * Insert multiple chats
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChats(chats: List<ChatEntity>)

    /**
     * Update a chat
     */
    @Update
    suspend fun updateChat(chat: ChatEntity)

    /**
     * Update unread count
     */
    @Query("UPDATE chats SET unread_count = :count WHERE id = :chatId")
    suspend fun updateUnreadCount(chatId: String, count: Int)

    /**
     * Delete a chat
     */
    @Query("DELETE FROM chats WHERE id = :chatId")
    suspend fun deleteChat(chatId: String)

    /**
     * Delete all chats
     */
    @Query("DELETE FROM chats")
    suspend fun deleteAllChats()
}

/**
 * Data Access Object for Message operations.
 */
@Dao
interface MessageDao {

    /**
     * Get messages for a specific chat
     */
    @Query("SELECT * FROM messages WHERE chat_id = :chatId ORDER BY timestamp ASC")
    fun getMessagesByChatIdFlow(chatId: String): Flow<List<MessageEntity>>

    /**
     * Get messages for a specific chat (suspend)
     */
    @Query("SELECT * FROM messages WHERE chat_id = :chatId ORDER BY timestamp ASC")
    suspend fun getMessagesByChatId(chatId: String): List<MessageEntity>

    /**
     * Get message by ID
     */
    @Query("SELECT * FROM messages WHERE id = :messageId")
    suspend fun getMessageById(messageId: String): MessageEntity?

    /**
     * Insert a message
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    /**
     * Insert multiple messages
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<MessageEntity>)

    /**
     * Update message
     */
    @Update
    suspend fun updateMessage(message: MessageEntity)

    /**
     * Mark message as read
     */
    @Query("UPDATE messages SET is_read = 1 WHERE id = :messageId")
    suspend fun markMessageAsRead(messageId: String)

    /**
     * Mark all messages in chat as read
     */
    @Query("UPDATE messages SET is_read = 1 WHERE chat_id = :chatId AND is_sent_by_me = 0")
    suspend fun markChatMessagesAsRead(chatId: String)

    /**
     * Delete messages for a chat
     */
    @Query("DELETE FROM messages WHERE chat_id = :chatId")
    suspend fun deleteMessagesForChat(chatId: String)

    /**
     * Delete all messages
     */
    @Query("DELETE FROM messages")
    suspend fun deleteAllMessages()
}