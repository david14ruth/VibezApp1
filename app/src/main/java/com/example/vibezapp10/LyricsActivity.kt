package com.example.vibezapp10

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.vibezapp10.Adapters.LyricsAdapter
import com.example.vibezapp10.Models.LyricLine
import com.example.vibezapp10.api.RetrofitClient
import kotlinx.coroutines.launch

class LyricsActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var songTitle: TextView
    private lateinit var artistName: TextView
    private lateinit var albumArt: ImageView
    private lateinit var btnShare: ImageButton
    private lateinit var recyclerLyrics: RecyclerView

    private var songTitleText: String = ""
    private var artistText: String = ""
    private var coverResId: Int = R.drawable.placeholder_album

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lyrics)

        toolbar = findViewById(R.id.toolbar)
        songTitle = findViewById(R.id.song_title)
        artistName = findViewById(R.id.artist_name)
        albumArt = findViewById(R.id.song_album_art)
        btnShare = findViewById(R.id.btn_share_lyrics)
        recyclerLyrics = findViewById(R.id.recycler_lyrics)
        recyclerLyrics.layoutManager = LinearLayoutManager(this)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        songTitleText = intent.getStringExtra("song_title") ?: "Unknown"
        artistText = intent.getStringExtra("artist") ?: "Unknown"
        coverResId = intent.getIntExtra("cover_res_id", R.drawable.placeholder_album)

        songTitle.text = songTitleText
        artistName.text = artistText
        Glide.with(this).load(coverResId).into(albumArt)

        btnShare.setOnClickListener { shareLyrics() }

        fetchLyricsByArtistAndTitle(artistText, songTitleText)
    }

    private fun fetchLyricsByArtistAndTitle(artist: String, title: String) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.lyricsOvhApi.getLyrics(artist, title)
                if (response.isSuccessful) {
                    val lyricsBody = response.body()?.lyrics
                    if (!lyricsBody.isNullOrBlank()) {
                        showLyrics(lyricsBody)
                    } else {
                        showEmptyMessage()
                    }
                } else {
                    showEmptyMessage()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                showEmptyMessage()
            }
        }
    }


    private fun showLyrics(lyrics: String) {
        val lines = lyrics.lines().map { LyricLine(text=it) }
        recyclerLyrics.adapter = LyricsAdapter(lines)
        recyclerLyrics.visibility = View.VISIBLE
    }

    private fun showEmptyMessage() {
        recyclerLyrics.adapter = LyricsAdapter(listOf(LyricLine(text="Lyrics not found.")))
        recyclerLyrics.visibility = View.VISIBLE
    }

    private fun shareLyrics() {
        val lyricsText = (recyclerLyrics.adapter as? LyricsAdapter)?.let { adapter ->
            (0 until adapter.itemCount).joinToString("\n") { adapter.lyrics[it].text }
        } ?: ""
        if (lyricsText.isNotEmpty()) {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, lyricsText)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(shareIntent, "Share lyrics via"))
        }
    }

    companion object {
        fun newIntent(
            context: Context,
            songTitle: String,
            artist: String,
            coverResId: Int
        ): Intent {
            return Intent(context, LyricsActivity::class.java).apply {
                putExtra("song_title", songTitle)
                putExtra("artist", artist)
                putExtra("cover_res_id", coverResId)
            }
        }
    }

}
