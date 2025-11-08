package com.example.vibezapp10.Models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Song(
    val id: Int,
    val title: String? = null,
    val artist: String? = null,
    val lyrics: String? = null,

    @SerialName("file_url")
    val fileUrl: String? = null,

    @SerialName("album_art_url")
    val albumArtUrl: String? = null
)
