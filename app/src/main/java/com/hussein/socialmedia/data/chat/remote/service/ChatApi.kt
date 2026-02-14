package com.hussein.socialmedia.data.chat.remote.service

import com.hussein.socialmedia.data.chat.remote.model.request.SendMessageRequest
import com.hussein.socialmedia.data.chat.remote.model.response.ChatDto
import com.hussein.socialmedia.data.chat.remote.model.response.MessageDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit API interface for Chat-related endpoints.
 */
interface ChatApi {

    /**
     * Get all chats for current user
     */
    @GET("api/chats")
    suspend fun getChats(): List<ChatDto>

    /**
     * Get chat by ID
     */
    @GET("api/chats/{chatId}")
    suspend fun getChatById(
        @Path("chatId") chatId: String
    ): ChatDto

    /**
     * Get messages for a chat
     */
    @GET("api/chats/{chatId}/messages")
    suspend fun getMessages(
        @Path("chatId") chatId: String,
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 50
    ): List<MessageDto>

    /**
     * Send a message
     */
    @POST("api/chats/{chatId}/messages")
    suspend fun sendMessage(
        @Path("chatId") chatId: String,
        @Body messageRequest: SendMessageRequest
    ): MessageDto

    /**
     * Mark messages as read
     */
    @PUT("api/chats/{chatId}/read")
    suspend fun markAsRead(
        @Path("chatId") chatId: String
    )

    /**
     * Create or get chat with user
     */
    @POST("api/chats/user/{userId}")
    suspend fun createOrGetChat(
        @Path("userId") userId: String
    ): ChatDto
}