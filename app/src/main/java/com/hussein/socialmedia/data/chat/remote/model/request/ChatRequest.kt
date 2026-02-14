package com.hussein.socialmedia.data.chat.remote.model.request


/**
 * Request model for sending a message
 */
data class SendMessageRequest(
    val content: String,
    val imageUrl: String? = null
)