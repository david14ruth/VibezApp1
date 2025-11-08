package com.example.vibezapp10.Models

import kotlinx.serialization.Serializable

@Serializable
data class Playlist(
    val id: Long? = null,
    val user_id: String,
    val name: String,
    val description: String? = null,
    val created_at: String? = null
)
