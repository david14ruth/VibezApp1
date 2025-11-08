package com.example.vibezapp10.Models

import kotlinx.serialization.Serializable

@Serializable
data class PlaylistSong(
    val playlist_id: String,
    val song_id: String
)