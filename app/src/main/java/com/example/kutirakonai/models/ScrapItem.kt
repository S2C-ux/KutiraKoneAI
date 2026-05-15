package com.example.kutirakonai.models

data class ScrapItem(
    val id: String = "",
    val userId: String = "",
    val ownerName: String = "",
    val material: String = "",       // typed or spoken by uploader
    val color: String = "",
    val sizeMeters: Double = 0.0,
    val imageUrl: String = "",
    val description: String = "",
    val isAvailable: Boolean = true,
    val timestamp: Long = 0L,
    val aiSuggestions: String = ""   // AI-generated design ideas
)