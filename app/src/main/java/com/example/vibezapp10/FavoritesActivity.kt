package com.example.vibezapp10

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vibezapp10.Adapters.FavoritesAdapter
import com.example.vibezapp10.Models.Playlist
import com.example.vibezapp10.Models.Song
import com.example.vibezapp10.databinding.ActivityFavoritesBinding

class FavoritesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoritesBinding
    private val favoriteSongs = mutableListOf<Song>()
    private lateinit var adapter: FavoritesAdapter

    // Example source of playlists
    private val allPlaylists = mutableListOf<Playlist>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.favoritesRecycler.layoutManager = LinearLayoutManager(this)
        binding.favoritesRecycler.adapter = adapter

        loadFavorites()
    }

    private fun loadFavorites() {
        favoriteSongs.clear()
        // Collect all songs marked as favorite from all playlists

    }

    private fun toggleEmptyState() {
        val isEmpty = favoriteSongs.isEmpty()
        binding.emptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.favoritesRecycler.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    // Call this to refresh favorites whenever playlists or songs change
    fun refreshFavorites() {
        loadFavorites()
    }
}
