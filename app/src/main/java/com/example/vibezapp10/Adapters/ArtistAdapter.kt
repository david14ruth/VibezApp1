package com.example.vibezapp10.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.vibezapp10.Models.Artist
import com.example.vibezapp10.R

class ArtistAdapter(
    private val artists: List<Artist>,
    private val onItemClick: (Artist) -> Unit
) : RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder>() {

    inner class ArtistViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.artist_image)
        val name: TextView = view.findViewById(R.id.artist_name)

        fun bind(artist: Artist) {
            name.text = artist.name
            Glide.with(itemView.context)
                .load(artist.imageUrl ?: R.drawable.placeholder_album)
                .into(image)

            itemView.setOnClickListener { onItemClick(artist) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_artist_profile, parent, false)
        return ArtistViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {
        holder.bind(artists[position])
    }

    override fun getItemCount(): Int = artists.size
}