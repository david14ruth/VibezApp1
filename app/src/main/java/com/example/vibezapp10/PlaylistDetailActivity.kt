package com.example.vibezapp10

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.vibezapp10.Models.Song
import com.example.vibezapp10.api.PlaylistsService
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class PlaylistDetailActivity : AppCompatActivity() {

    private lateinit var songsRecycler: RecyclerView
    private lateinit var emptyState: View
    private lateinit var playlistTitle: TextView

    private val songsAdapter = PlaylistSongsAdapter()
    private var playlistId: String = ""
    private var playlistName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist_detail)

        playlistTitle = findViewById(R.id.playlist_name)
        songsRecycler = findViewById(R.id.songs_recycler)
        emptyState = findViewById(R.id.empty_state)

        playlistId = intent.getStringExtra("playlist_id") ?: ""
        playlistName = intent.getStringExtra("playlist_name") ?: ""
        playlistTitle.text = playlistName

        setupRecyclerView()
        loadPlaylistSongs()
    }

    private fun setupRecyclerView() {
        songsRecycler.layoutManager = LinearLayoutManager(this)
        songsRecycler.adapter = songsAdapter
    }

    private fun loadPlaylistSongs() {
        lifecycleScope.launch {
            try {
                val songs: List<Song> = PlaylistsService().getSongsInPlaylist(playlistId)

                if (songs.isEmpty()) {
                    songsRecycler.visibility = View.GONE
                    emptyState.visibility = View.VISIBLE
                } else {
                    songsRecycler.visibility = View.VISIBLE
                    emptyState.visibility = View.GONE
                    songsAdapter.setData(songs)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                songsRecycler.visibility = View.GONE
                emptyState.visibility = View.VISIBLE
            }
        }
    }

    // --- Adapter ---
    inner class PlaylistSongsAdapter : RecyclerView.Adapter<PlaylistSongsAdapter.SongViewHolder>() {
        private var data: List<Song> = emptyList()

        fun setData(songs: List<Song>) {
            data = songs
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): SongViewHolder {
            val view = android.view.LayoutInflater.from(parent.context)
                .inflate(R.layout.item_song, parent, false)
            return SongViewHolder(view)
        }

        override fun getItemCount(): Int = data.size

        override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
            holder.bind(data[position])
        }

        inner class SongViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
            private val title: TextView = itemView.findViewById(R.id.song_title)
            private val artist: TextView = itemView.findViewById(R.id.song_artist)
            private val duration: TextView = itemView.findViewById(R.id.song_duration)
            private val albumImage: ImageView = itemView.findViewById(R.id.album_image)
            private val favoriteBtn: ImageButton = itemView.findViewById(R.id.btn_favorite)

            fun bind(song: Song) {
                title.text = song.title
                artist.text = song.artist


                // Load album image using Glide
                albumImage.setImageResource(R.drawable.placeholder_album)

                // Favorite toggle (placeholder)
                favoriteBtn.setOnClickListener {
                    Toast.makeText(itemView.context, "${song.title} favorited!", Toast.LENGTH_SHORT).show()
                    // TODO: implement actual favorite logic
                }

                // Play song on click
                itemView.setOnClickListener {
                    Toast.makeText(itemView.context, "Play ${song.title}", Toast.LENGTH_SHORT).show()
                    // TODO: Launch player activity or play audio
                }

                // Remove song on long click
                itemView.setOnLongClickListener {
                    lifecycleScope.launch {
                        try {
                            song.id?.let { id ->
                                PlaylistsService().removeSongFromPlaylist(playlistId, id.toString())
                            }
                            Toast.makeText(itemView.context, "${song.title} removed", Toast.LENGTH_SHORT).show()
                            loadPlaylistSongs() // Refresh list
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(itemView.context, "Failed to remove song", Toast.LENGTH_SHORT).show()
                        }
                    }
                    true
                }
            }
        }
    }
}
