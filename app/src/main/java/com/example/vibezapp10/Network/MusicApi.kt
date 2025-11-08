package com.example.vibezapp10.Network

import com.example.vibezapp10.Models.Song
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface LyricsApi {
    @GET("songs/search")
    suspend fun searchSongs(@Query("query") query: String): List<Song>

    @GET("songs/{id}/lyrics")
    suspend fun getLyrics(@Path("id") id: String): String
}
