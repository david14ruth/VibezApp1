package com.example.vibezapp10.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://api.lyrics.ovh/"

    val lyricsOvhApi: LyricsOvhApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LyricsOvhApi::class.java)
    }
}

