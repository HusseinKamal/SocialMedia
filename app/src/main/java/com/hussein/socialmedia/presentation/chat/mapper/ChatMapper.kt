package com.hussein.socialmedia.presentation.chat.mapper

import com.hussein.socialmedia.data.chat.remote.mapper.formatMessageTime
import com.hussein.socialmedia.domain.chat.model.Message
import com.hussein.socialmedia.presentation.chat.model.MessageUiModel

fun Message.toUiModel(): MessageUiModel {
    return MessageUiModel(
        id = id,
        chatId = chatId,
        senderId = senderId,
        senderName = senderName,
        content = content,
        imageUrl = imageUrl,
        formattedTime = formatMessageTime(timestamp),
        isRead = isRead,
        isSentByMe = isSentByMe
    )
}