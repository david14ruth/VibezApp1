package com.example.vibezapp10


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vibezapp10.Models.Playlist
import com.example.vibezapp10.api.PlaylistsService
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class PlaylistsActivity : AppCompatActivity() {

    private lateinit var playlistsRecycler: RecyclerView
    private lateinit var emptyState: View
    private lateinit var totalPlaylists: TextView
    private lateinit var totalSongs: TextView
    private lateinit var playlistTabs: TabLayout

    private val playlistsAdapter = PlaylistsAdapter()
    private var userId: String = ""

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlists)

        playlistsRecycler = findViewById(R.id.playlists_recycler)
        emptyState = findViewById(R.id.empty_state)
        totalPlaylists = findViewById(R.id.total_playlists)
        totalSongs = findViewById(R.id.total_songs)
        playlistTabs = findViewById(R.id.playlist_tabs)
        val fab = findViewById<FloatingActionButton>(R.id.fab_create_playlist)

        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        setupRecyclerView()
        setupTabs()
        loadPlaylists()

        fab.setOnClickListener {
            startActivity(Intent(this, CreatePlaylistActivity::class.java))
        }

        // Toolbar back
        findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
            .setNavigationOnClickListener { finish() }

        val tool =findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        val music = findViewById<TextView>(R.id.playlist)
        val favorites = findViewById<TextView>(R.id.song)

        val xhosa = findViewById<TextView>(R.id.Xhosa)
        val english = findViewById<TextView>(R.id.English)
        val afrikkans = findViewById<TextView>(R.id.Afrikkans)


        xhosa.setOnClickListener(){
            tool.setTitle("Uluhlu lwam lokudlala")
            music.setText("lwam lokudlala")
            favorites.setText("Iingoma")
        }
        english.setOnClickListener(){
            tool.setTitle("My playlist")
            music.setText("playlist")
            favorites.setText("Songs")
        }
        afrikkans.setOnClickListener(){
            tool.setTitle("My speellys")
            music.setText("speellys")
            favorites.setText("Liedjies")
        }
    }

    private fun setupRecyclerView() {
        playlistsRecycler.layoutManager = LinearLayoutManager(this)
        playlistsRecycler.adapter = playlistsAdapter
    }

    private fun setupTabs() {
        val tabs = listOf("All", "Favorites", "Recently Added")
        tabs.forEach { tab -> playlistTabs.addTab(playlistTabs.newTab().setText(tab)) }

        playlistTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                filterPlaylists(tab.text.toString())
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {
                filterPlaylists(tab.text.toString())
            }
        })
    }

    private fun filterPlaylists(tabName: String) {
        lifecycleScope.launch {
            try {
                val playlists = when (tabName) {

                    else -> PlaylistsService().getPlaylistsForUser(userId)
                }

                displayPlaylists(playlists)
            } catch (e: Exception) {
                e.printStackTrace()
                displayPlaylists(emptyList())
            }
        }
    }

    private fun loadPlaylists() {
        filterPlaylists("All") // Default tab
    }

    private fun displayPlaylists(playlists: List<Playlist>) {
        if (playlists.isEmpty()) {
            playlistsRecycler.visibility = View.GONE
            emptyState.visibility = View.VISIBLE
        } else {
            playlistsRecycler.visibility = View.VISIBLE
            emptyState.visibility = View.GONE
            playlistsAdapter.setData(playlists)
            totalPlaylists.text = playlists.size.toString()
        }
    }

    // --- Adapter ---
    class PlaylistsAdapter : RecyclerView.Adapter<PlaylistsAdapter.PlaylistViewHolder>() {
        private var data: List<Playlist> = emptyList()

        fun setData(playlists: List<Playlist>) {
            data = playlists
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): PlaylistViewHolder {
            val view = android.view.LayoutInflater.from(parent.context)
                .inflate(android.R.layout.simple_list_item_2, parent, false)
            return PlaylistViewHolder(view)
        }

        override fun getItemCount(): Int = data.size

        override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
            holder.bind(data[position])
        }

        inner class PlaylistViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
            private val title: TextView = itemView.findViewById(android.R.id.text1)
            private val subtitle: TextView = itemView.findViewById(android.R.id.text2)

            fun bind(playlist: Playlist) {
                title.text = playlist.name


                itemView.setOnClickListener {
                    val intent = Intent(itemView.context, PlaylistDetailActivity::class.java).apply {
                        putExtra("playlist_id", playlist.id)
                        putExtra("playlist_name", playlist.name)
                    }
                    itemView.context.startActivity(intent)
                }
            }
        }
    }
}
