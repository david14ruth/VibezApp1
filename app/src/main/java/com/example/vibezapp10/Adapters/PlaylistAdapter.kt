package com.example.vibezapp10.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vibezapp10.Models.Playlist
import com.example.vibezapp10.R
import com.example.vibezapp10.api.PlaylistsService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class PlaylistAdapter(
    private val playlists: List<Playlist>,
    private val onPlaylistClick: (Playlist) -> Unit,
    private val coroutineScope: CoroutineScope // Pass lifecycleScope from Activity/Fragment
) : RecyclerView.Adapter<PlaylistAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameText: TextView = view.findViewById(R.id.playlist_name)
        val createdAtText: TextView = view.findViewById(R.id.playlist_created_at)
        val songsCountText: TextView = view.findViewById(R.id.songs_count)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_playlist, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val playlist = playlists[position]

        // Name
        holder.nameText.text = playlist.name

        // Created at (format nicely if not null)
        holder.createdAtText.text = playlist.created_at?.let {
            try {
                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                val date = parser.parse(it)
                SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(date!!)
            } catch (e: Exception) {
                it // fallback to raw string
            }
        } ?: "Unknown date"

        // Placeholder for songs count
        holder.songsCountText.text = "Songs: ..."

        // Fetch song count asynchronously
        playlist.id?.let { id ->
            coroutineScope.launch(Dispatchers.Main) {
                try {
                    val count = PlaylistsService().getSongsCountForPlaylist(id.toString())
                    holder.songsCountText.text = "Songs: $count"
                } catch (e: Exception) {
                    holder.songsCountText.text = "Songs: ?"
                }
            }
        }

        holder.itemView.setOnClickListener {
            onPlaylistClick(playlist)
        }
    }

    override fun getItemCount(): Int = playlists.size
}
