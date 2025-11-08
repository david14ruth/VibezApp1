package com.example.vibezapp10.api

import com.example.vibezapp10.Models.Album
import com.example.vibezapp10.Models.Playlist
import com.example.vibezapp10.Models.Song
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.Response

interface ApiService {
    @GET("songs")
    suspend fun getSongs(): List<Song>

    @GET("playlists/{id}")
    suspend fun getPlaylist(@Path("id") id: String): Playlist

    @GET("albums/{id}")
    suspend fun getAlbum(@Path("id") id: String): Album

    @GET("lyrics")
    suspend fun getLyrics(
        @Query("artist") artist: String,
        @Query("title") title: String
    ): Response<String>
}