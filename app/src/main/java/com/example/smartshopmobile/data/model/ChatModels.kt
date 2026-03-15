package com.example.smartshopmobile.data.model

data class SendChatMessageDto(
    val message: String
)

data class ChatMessageDto(
    val id: String,
    val userId: String,
    val username: String,
    val message: String,
    val sentAt: String, // Changed to String for robust SignalR serialization
    val createdAt: String
)
