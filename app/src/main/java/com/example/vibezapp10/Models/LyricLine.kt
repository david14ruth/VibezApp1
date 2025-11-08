package com.example.vibezapp10.Models

// Simple data holder for lyrics
data class LyricLine(
    val timestamp: Long? = null, // optional if you want synced lyrics
    val text: String
)