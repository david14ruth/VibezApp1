package com.example.vibezapp10

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vibezapp10.Adapters.SongAdapter
import com.example.vibezapp10.Models.Playlist
import com.example.vibezapp10.Models.PlaylistSong
import com.example.vibezapp10.Models.Song
import com.example.vibezapp10.api.PlaylistsService
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class CreatePlaylistActivity : AppCompatActivity() {

    private lateinit var playlistNameInput: EditText
    private lateinit var songsRecycler: RecyclerView
    private lateinit var btnCreate: Button

    private val songsAdapter by lazy {
        SongAdapter(
            songs = mutableListOf(),
            onSongClick = { song ->
                Toast.makeText(this, "Selected: ${song.title}", Toast.LENGTH_SHORT).show()
            },
            onFavoriteClick = { song ->
                Toast.makeText(this, "${song.title} favorited!", Toast.LENGTH_SHORT).show()
            }
        ).apply { setSelectionMode(true) }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_playlist)

        playlistNameInput = findViewById(R.id.playlist_name_input)
        songsRecycler = findViewById(R.id.songs_recycler)
        btnCreate = findViewById(R.id.btn_create_playlist)

        setupRecyclerView()
        loadSongs()

        btnCreate.setOnClickListener { createPlaylist() }

        findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
            .setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        songsRecycler.layoutManager = LinearLayoutManager(this)
        songsRecycler.adapter = songsAdapter
        // If your SongAdapter has selection logic, enable it here
        // songsAdapter.setSelectionMode(true) // optional
    }

    private fun loadSongs() {
        lifecycleScope.launch {
            try {
                // Fetch all songs from Supabase
                val songs: List<Song> = com.example.vibezapp10.api.SongsService().getAllSongs()

                if (songs.isEmpty()) {
                    Toast.makeText(this@CreatePlaylistActivity, "No songs available", Toast.LENGTH_SHORT).show()
                } else {
                    songsAdapter.updateSongs(songs)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@CreatePlaylistActivity, "Failed to load songs", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun createPlaylist() {
        val name = playlistNameInput.text.toString().trim()
        val selectedSongs = songsAdapter.getSelectedSongs()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        if (name.isEmpty()) {
            Toast.makeText(this, "Enter playlist name", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedSongs.isEmpty()) {
            Toast.makeText(this, "Select at least one song", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                // Create playlist object
                val playlist = Playlist(user_id = userId, name = name)
                val createdPlaylist = PlaylistsService().createPlaylist(playlist)

                if (createdPlaylist != null) {
                    // Add selected songs to playlist
                    selectedSongs.forEach { song ->
                        val playlistSong = PlaylistSong(
                            playlist_id = createdPlaylist.id.toString(),
                            song_id = song.id.toString(),
                        )
                        PlaylistsService().addSongToPlaylist(playlistSong)
                    }

                    Toast.makeText(
                        this@CreatePlaylistActivity,
                        "Playlist '$name' created!",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {
                    Toast.makeText(
                        this@CreatePlaylistActivity,
                        "Failed to create playlist",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@CreatePlaylistActivity, "Error creating playlist", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
