package com.hussein.socialmedia.domain.chat.repository

import com.hussein.socialmedia.domain.chat.model.Chat
import com.hussein.socialmedia.domain.chat.model.Message
import com.hussein.socialmedia.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getChats(): Flow<Resource<List<Chat>>>
    fun getMessages(chatId: String): Flow<Resource<List<Message>>>
    suspend fun sendMessage(chatId: String, content: String, imageUrl: String?): Resource<Message>
    suspend fun markAsRead(chatId: String): Resource<Unit>
}