package com.example.kutirakonai.models

data class SwapRequest(
    val id: String = "",
    val scrapId: String = "",
    val scrapMaterial: String = "",
    val scrapColor: String = "",
    val scrapSize: Double = 0.0,
    val scrapOwnerId: String = "",
    val scrapOwnerName: String = "",
    val requesterId: String = "",
    val requesterName: String = "",
    val type: String = "",
    val status: String = "pending",
    val timestamp: Long = 0L,
    val hasUnreadMessages: Boolean = false
)