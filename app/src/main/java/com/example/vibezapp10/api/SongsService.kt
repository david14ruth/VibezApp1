package com.example.vibezapp10.api

import com.example.vibezapp10.Models.Song
import com.example.vibezapp10.Network.SupabaseManager
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SongsService {

    private val client = SupabaseManager.client

    // FETCH SONG BY ID
    suspend fun getSongById(id: String): Song? = withContext(Dispatchers.IO) {
        try {
            client.postgrest["song"]
                .select {
                    filter {
                        eq("id", id)   // bigint -> Long
                    }
                }
                .decodeSingleOrNull<Song>()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // ADD A NEW SONG
    suspend fun addSong(song: Song): Song? = withContext(Dispatchers.IO) {
        try {
            client.postgrest["song"]   // ✅ correct table
                .insert(song)
                .decodeSingleOrNull<Song>()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getAllSongs(): List<Song> = withContext(Dispatchers.IO) {
        try {
            client.postgrest["song"]
                .select()
                .decodeList<Song>()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // SEARCH FOR SONGS BY ARTIST & TITLE
    suspend fun searchSongs(artist: String?, title: String?): List<Song> = withContext(Dispatchers.IO) {
        try {
            client.postgrest["song"]   // ✅ correct table
                .select {
                    if (!artist.isNullOrBlank()) {
                        filter {
                            ilike("artist", "%$artist%")
                        }
                    }
                    if (!title.isNullOrBlank()) {
                        filter {
                            ilike("title", "%$title%")
                        }
                    }
                }
                .decodeList<Song>()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
