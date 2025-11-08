package com.example.vibezapp10

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.vibezapp10.Models.Song
import com.example.vibezapp10.api.SongsService
import kotlinx.coroutines.launch

class MusicPlayerActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var seekBar: SeekBar
    private lateinit var playPauseBtn: ImageButton
    private lateinit var nextBtn: ImageButton
    private lateinit var prevBtn: ImageButton
    private lateinit var shuffleBtn: ImageButton
    private lateinit var repeatBtn: ImageButton
    private lateinit var lyricsBtn: Button
    private lateinit var albumArt: ImageView
    private lateinit var songTitleText: TextView
    private lateinit var artistText: TextView
    private lateinit var currentTimeText: TextView
    private lateinit var totalTimeText: TextView

    private var isPlaying = false
    private var isShuffle = false
    private var isRepeat = false
    private var currentSongUrl: String? = null
    private var songId: Long? = null
    private var albumArtUrl: String? = null

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_player)

        initViews()
        mediaPlayer = MediaPlayer()

        // Get songId from Intent (as Long)
        songId = intent.getStringExtra("song_id")?.toLongOrNull()
        loadSong(songId ?: 1L)

        setupButtons()
        setupSeekBarUpdater()
        val back = findViewById<ImageButton>(R.id.btn_back)
        back.setOnClickListener (){
            finish()
        }
    }

    private fun initViews() {
        playPauseBtn = findViewById(R.id.btn_play_pause)
        nextBtn = findViewById(R.id.btn_next)
        prevBtn = findViewById(R.id.btn_previous)
        shuffleBtn = findViewById(R.id.btn_shuffle)
        repeatBtn = findViewById(R.id.btn_repeat)
        seekBar = findViewById(R.id.seek_bar)
        lyricsBtn = findViewById(R.id.btn_show_lyrics)
        albumArt = findViewById(R.id.album_art)
        songTitleText = findViewById(R.id.song_title)
        artistText = findViewById(R.id.artist_name)
        currentTimeText = findViewById(R.id.current_time)
        totalTimeText = findViewById(R.id.total_time)
    }

    private fun loadSong(id: Long) {
        lifecycleScope.launch {
            try {
                val song: Song? = SongsService().getSongById(id.toString())
                if (song != null) {
                    currentSongUrl = song.fileUrl
                    albumArtUrl = song.albumArtUrl

                    songTitleText.text = song.title
                    artistText.text = song.artist

                    Glide.with(this@MusicPlayerActivity)
                        .load(song.albumArtUrl ?: R.drawable.placeholder_album)
                        .into(albumArt)

                    setupMediaPlayer()
                } else {
                    Toast.makeText(this@MusicPlayerActivity, "Song not found", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@MusicPlayerActivity, "Failed to load song", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupMediaPlayer() {
        currentSongUrl?.let {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(it)
            mediaPlayer.setOnPreparedListener {
                seekBar.max = mediaPlayer.duration
                totalTimeText.text = formatTime(mediaPlayer.duration)
                mediaPlayer.start()
                isPlaying = true
                playPauseBtn.setImageResource(R.drawable.ic_pause)
            }
            mediaPlayer.setOnCompletionListener {
                if (isRepeat) {
                    mediaPlayer.seekTo(0)
                    mediaPlayer.start()
                } else if (isShuffle) {
                    // TODO: implement shuffle logic
                } else {
                    playPauseBtn.setImageResource(R.drawable.ic_play)
                    isPlaying = false
                }
            }
            mediaPlayer.prepareAsync()
        }
    }

    private fun setupButtons() {
        playPauseBtn.setOnClickListener {
            if (isPlaying) mediaPlayer.pause() else mediaPlayer.start()
            isPlaying = !isPlaying
            playPauseBtn.setImageResource(if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play)
        }

        shuffleBtn.setOnClickListener {
            isShuffle = !isShuffle
            shuffleBtn.setColorFilter(if (isShuffle) getColor(R.color.red) else getColor(R.color.white))
        }

        repeatBtn.setOnClickListener {
            isRepeat = !isRepeat
            repeatBtn.setColorFilter(if (isRepeat) getColor(R.color.red) else getColor(R.color.white))
        }

        lyricsBtn.setOnClickListener {
            startActivity(
                LyricsActivity.newIntent(
                    this,
                    songTitleText.text.toString(),
                    artistText.text.toString(),
                    R.drawable.placeholder_album
                )
            )
        }

    }

    private fun setupSeekBarUpdater() {
        handler.post(object : Runnable {
            override fun run() {
                if (mediaPlayer.isPlaying) {
                    seekBar.progress = mediaPlayer.currentPosition
                    currentTimeText.text = formatTime(mediaPlayer.currentPosition)
                }
                handler.postDelayed(this, 1000)
            }
        })
    }

    private fun formatTime(ms: Int): String {
        val seconds = ms / 1000 % 60
        val minutes = ms / 1000 / 60
        return String.format("%d:%02d", minutes, seconds)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        handler.removeCallbacksAndMessages(null)
    }

    companion object {
        fun newIntent(context: Context, songId: String): Intent {
            return Intent(context, MusicPlayerActivity::class.java).apply {
                songId?.let { putExtra("song_id", it.toString()) }
            }
        }
    }
}
