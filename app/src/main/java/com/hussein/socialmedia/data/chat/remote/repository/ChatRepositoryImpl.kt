package com.hussein.socialmedia.data.chat.remote.repository

import com.hussein.socialmedia.data.chat.remote.mapper.toDomain
import com.hussein.socialmedia.data.chat.remote.mapper.toEntity
import com.hussein.socialmedia.data.chat.remote.model.request.SendMessageRequest
import com.hussein.socialmedia.data.chat.remote.service.ChatApi
import com.hussein.socialmedia.data.database.SocialMediaDatabase
import com.hussein.socialmedia.domain.chat.model.Chat
import com.hussein.socialmedia.domain.chat.model.Message
import com.hussein.socialmedia.domain.chat.repository.ChatRepository
import com.hussein.socialmedia.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val chatApi: ChatApi,
    private val database: SocialMediaDatabase
) : ChatRepository {

    private val chatDao = database.chatDao()
    private val messageDao = database.messageDao()

    override fun getChats(): Flow<Resource<List<Chat>>> = flow {
        emit(Resource.Loading)
        try {
            val chatsDto = chatApi.getChats()
            val chats = chatsDto.map { it.toDomain() }
            chatDao.insertChats(chatsDto.map { it.toEntity() })
            emit(Resource.Success(chats))
        } catch (e: Exception) {
            emit(Resource.Error("Failed to load chats: ${e.message}", e))
        }
    }

    override fun getMessages(chatId: String): Flow<Resource<List<Message>>> = flow {
        emit(Resource.Loading)
        try {
            val messagesDto = chatApi.getMessages(chatId)
            val messages = messagesDto.map { it.toDomain() }
            messageDao.insertMessages(messagesDto.map { it.toEntity() })
            emit(Resource.Success(messages))
        } catch (e: Exception) {
            val cachedMessages = messageDao.getMessagesByChatId(chatId)
            if (cachedMessages.isNotEmpty()) {
                emit(Resource.Success(cachedMessages.map { it.toDomain() }))
            } else {
                emit(Resource.Error("Failed to load messages: ${e.message}", e))
            }
        }
    }

    override suspend fun sendMessage(
        chatId: String,
        content: String,
        imageUrl: String?
    ): Resource<Message> {
        return try {
            val request = SendMessageRequest(content, imageUrl)
            val messageDto = chatApi.sendMessage(chatId, request)
            val message = messageDto.toDomain()
            messageDao.insertMessage(messageDto.toEntity())
            Resource.Success(message)
        } catch (e: Exception) {
            Resource.Error("Failed to send message: ${e.message}", e)
        }
    }

    override suspend fun markAsRead(chatId: String): Resource<Unit> {
        return try {
            chatApi.markAsRead(chatId)
            messageDao.markChatMessagesAsRead(chatId)
            chatDao.updateUnreadCount(chatId, 0)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Failed to mark as read: ${e.message}", e)
        }
    }
}