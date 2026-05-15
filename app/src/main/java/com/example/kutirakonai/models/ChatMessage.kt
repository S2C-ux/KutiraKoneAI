package com.example.kutirakonai.models

data class ChatMessage(
    val id: String = "",
    val senderUid: String = "",
    val senderName: String = "",
    val receiverUid: String = "",
    val requestId: String = "",
    val message: String = "",
    val timestamp: Long = 0L
)