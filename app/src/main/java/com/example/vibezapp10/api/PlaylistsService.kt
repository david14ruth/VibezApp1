package com.example.vibezapp10.api

import com.example.vibezapp10.Models.Playlist
import com.example.vibezapp10.Models.PlaylistSong
import com.example.vibezapp10.Models.Song
import com.example.vibezapp10.Network.SupabaseManager
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PlaylistsService {

    private val client = SupabaseManager.client

    // CREATE PLAYLIST
    suspend fun createPlaylist(playlist: Playlist): Playlist? = withContext(Dispatchers.IO) {
        try {
            client.postgrest["playlists"]
                .insert(playlist)
                .decodeSingleOrNull<Playlist>()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // ADD SONG TO PLAYLIST
    suspend fun addSongToPlaylist(playlistSong: PlaylistSong): Boolean = withContext(Dispatchers.IO) {
        try {
            client.postgrest["playlist_songs"]
                .insert(playlistSong)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // REMOVE SONG FROM PLAYLIST
    suspend fun removeSongFromPlaylist(playlistId: String, songId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            client.postgrest["playlist_songs"]
                .delete {
                    filter {
                        eq("playlist_id", playlistId)
                        eq("song_id", songId)
                    }
                }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // GET ALL PLAYLISTS FOR USER (Firebase UID)
    suspend fun getPlaylistsForUser(userId: String): List<Playlist> = withContext(Dispatchers.IO) {
        try {
            client.postgrest["playlists"]
                .select {
                    filter { eq("user_id", userId) }
                }
                .decodeList<Playlist>()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // GET ALL SONGS IN A PLAYLIST (joins with SongsService)
    suspend fun getSongsInPlaylist(playlistId: String): List<Song> = withContext(Dispatchers.IO) {
        try {
            val playlistSongs = client.postgrest["playlist_songs"]
                .select {
                    filter { eq("playlist_id", playlistId) }
                }
                .decodeList<PlaylistSong>()

            playlistSongs.mapNotNull { ps ->
                SongsService().getSongById(ps.song_id)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // COUNT SONGS IN A PLAYLIST
    suspend fun getSongsCountForPlaylist(playlistId: String): Int = withContext(Dispatchers.IO) {
        try {
            val songs = client.postgrest["playlist_songs"]
                .select {
                    filter { eq("playlist_id", playlistId) }
                }
                .decodeList<PlaylistSong>()

            songs.size
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }
}