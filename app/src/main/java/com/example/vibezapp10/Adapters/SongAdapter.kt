package com.example.vibezapp10.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.vibezapp10.Models.Song
import com.example.vibezapp10.R

class SongAdapter(
    private val songs: MutableList<Song>,
    private val onSongClick: ((Song) -> Unit)? = null,
    private val onFavoriteClick: ((Song) -> Unit)? = null
) : RecyclerView.Adapter<SongAdapter.ViewHolder>() {

    private val selectedSongs = mutableSetOf<Song>()
    private var selectionMode = false

    /** Enable or disable selection mode */
    fun setSelectionMode(enabled: Boolean) {
        selectionMode = enabled
        notifyDataSetChanged()
    }

    /** Get currently selected songs */
    fun getSelectedSongs(): List<Song> = selectedSongs.toList()

    /** Update the songs list */
    fun updateSongs(newSongs: List<Song>) {
        songs.clear()
        songs.addAll(newSongs)
        selectedSongs.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_song, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = songs[position]
        holder.bind(song)
    }

    override fun getItemCount(): Int = songs.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val titleText: TextView = view.findViewById(R.id.song_title)
        private val artistText: TextView = view.findViewById(R.id.song_artist)
        private val albumImage: ImageView = view.findViewById(R.id.album_image)
        private val favoriteBtn: ImageButton? = view.findViewById(R.id.btn_favorite)

        fun bind(song: Song) {
            titleText.text = song.title
            artistText.text = song.artist

            // Load album image placeholder using Glide
            Glide.with(itemView.context)
                .load(R.drawable.placeholder_album)
                .placeholder(R.drawable.placeholder_album)
                .into(albumImage)

            // Highlight selected songs
            itemView.alpha = if (selectedSongs.contains(song)) 0.5f else 1.0f

            // Handle item click
            itemView.setOnClickListener {
                if (selectionMode) {
                    if (selectedSongs.contains(song)) selectedSongs.remove(song)
                    else selectedSongs.add(song)

                    val pos = bindingAdapterPosition
                    if (pos != RecyclerView.NO_POSITION) notifyItemChanged(pos)
                }
                onSongClick?.invoke(song)
            }

            // Handle favorite button click
            favoriteBtn?.setOnClickListener { onFavoriteClick?.invoke(song) }
        }
    }
}
