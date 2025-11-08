package com.example.vibezapp10.Models

import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val id: String,
    val full_name: String,
    val email: String,
    val avatar_url: String,
    val theme: String,
    val notifications_enabled: Boolean,
    val language: String
)

