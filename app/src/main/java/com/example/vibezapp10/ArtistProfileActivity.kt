package com.example.vibezapp10

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.vibezapp10.Adapters.AlbumAdapter
import com.example.vibezapp10.Adapters.SongAdapterLite
import com.example.vibezapp10.Models.Album
import com.example.vibezapp10.Models.Artist
import com.example.vibezapp10.Models.Song

class ArtistProfileActivity : AppCompatActivity() {

    private lateinit var followBtn: Button
    private lateinit var shufflePlayBtn: ImageButton
    private lateinit var artistImage: ImageView
    private lateinit var artistName: TextView
    private lateinit var artistFollowers: TextView
    private lateinit var artistBio: TextView
    private lateinit var songsRecycler: RecyclerView
    private lateinit var albumsRecycler: RecyclerView

    private var artistId: String = ""
    private var following: Boolean = false
    private var songs: List<Song> = emptyList()
    private var albums: List<Album> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_artist_profile)

        // Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

    }
}
