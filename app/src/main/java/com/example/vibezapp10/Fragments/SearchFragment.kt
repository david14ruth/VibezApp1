package com.example.vibezapp10.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vibezapp10.Adapters.RecentSearchAdapter
import com.example.vibezapp10.Adapters.SongAdapter
import com.example.vibezapp10.MusicPlayerActivity
import com.example.vibezapp10.Models.Song
import com.example.vibezapp10.R
import com.example.vibezapp10.api.SongsService
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private lateinit var searchView: SearchView
    private lateinit var rvSearchResults: RecyclerView
    private lateinit var rvRecentSearches: RecyclerView
    private lateinit var btnClearRecent: Button
    private lateinit var sectionRecent: LinearLayout
    private lateinit var sectionResults: LinearLayout
    private lateinit var sectionNoResults: LinearLayout
    private lateinit var sectionLoading: LinearLayout

    private val recentSearches = mutableListOf<String>()
    private val songsService = SongsService()
    private lateinit var songAdapter: SongAdapter
    private lateinit var recentAdapter: RecentSearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_search, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchView = view.findViewById(R.id.sv_search)
        rvSearchResults = view.findViewById(R.id.rv_search_results)
        rvRecentSearches = view.findViewById(R.id.rv_recent_searches)
        btnClearRecent = view.findViewById(R.id.btn_clear_recent)
        sectionRecent = view.findViewById(R.id.section_recent_searches)
        sectionResults = view.findViewById(R.id.section_search_results)
        sectionNoResults = view.findViewById(R.id.section_no_results)
        sectionLoading = view.findViewById(R.id.section_loading)

        setupAdapters()
        setupListeners()

        val sv_search = view.findViewById<SearchView>(R.id.sv_search)
        val search = view.findViewById<TextView>(R.id.tv_search_header)
        val recent = view.findViewById<TextView>(R.id.tv_recent_searches)
        val clear = view.findViewById<TextView>(R.id.btn_clear_recent)

        val xhosa = view.findViewById<TextView>(R.id.Xhosa)
        val english = view.findViewById<TextView>(R.id.English)
        val afrikkans = view.findViewById<TextView>(R.id.Afrikkans)


        xhosa.setOnClickListener(){
            search.setText("Khangela")
            recent.setText("Uphendlo lwakutsha nje")
            clear.setText("uluhlu lokudlala")
            sv_search.setQueryHint("Isihloko seengoma nganye...")
        }
        english.setOnClickListener(){
            search.setText("Search")
            recent.setText("Recent searches")
            clear.setText("Clear All")
            sv_search.setQueryHint("Search songs title...")
        }
        afrikkans.setOnClickListener(){
            search.setText("Soek")
            recent.setText("Vee alles uit")
            clear.setText("Onlangse soektogte")
            sv_search.setQueryHint("Soek liedjies se titel...")
        }
    }

    private fun setupAdapters() {
        // Song Adapter
        songAdapter = SongAdapter(mutableListOf(),
            onSongClick = { song ->
                // Open MusicPlayerActivity with song ID
                Log.d("SearchFragment", "Opening MusicPlayerActivity for song: ${song.title} (ID: ${song.id})")
                val intent = MusicPlayerActivity.newIntent(requireContext(), song.id.toString())
                startActivity(intent)
            },
            onFavoriteClick = { song ->
                Toast.makeText(requireContext(), "${song.title} favorited!", Toast.LENGTH_SHORT).show()
            }
        )
        rvSearchResults.layoutManager = LinearLayoutManager(requireContext())
        rvSearchResults.adapter = songAdapter

        // Recent Searches Adapter
        recentAdapter = RecentSearchAdapter(recentSearches) { query ->
            searchView.setQuery(query, true)
        }
        rvRecentSearches.layoutManager = LinearLayoutManager(requireContext())
        rvRecentSearches.adapter = recentAdapter
    }

    private fun setupListeners() {
        btnClearRecent.setOnClickListener {
            Log.d("SearchFragment", "Clearing recent searches")
            recentSearches.clear()
            recentAdapter.notifyDataSetChanged()
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { performSearch(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean = true
        })
    }

    private fun performSearch(query: String) {
        // Show loading, hide other sections
        sectionLoading.visibility = View.VISIBLE
        sectionNoResults.visibility = View.GONE
        sectionResults.visibility = View.GONE
        sectionRecent.visibility = View.GONE

        // Add to recent searches
        if (!recentSearches.contains(query)) {
            Log.d("SearchFragment", "Adding to recent searches: $query")
            recentSearches.add(0, query)
            recentAdapter.notifyDataSetChanged()
        }

        lifecycleScope.launch {
            try {
                Log.d("SearchFragment", "Searching for: $query")
                val songs: List<Song> = songsService.searchSongs(artist = null, title = query)
                Log.d("SearchFragment", "Found ${songs.size} songs")
                sectionLoading.visibility = View.GONE

                if (songs.isEmpty()) {
                    Log.d("SearchFragment", "No results found for: $query")
                    sectionNoResults.visibility = View.VISIBLE
                } else {
                    sectionResults.visibility = View.VISIBLE
                    songAdapter.updateSongs(songs)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("SearchFragment", "Error performing search: ${e.message}")
                sectionLoading.visibility = View.GONE
                sectionNoResults.visibility = View.VISIBLE
            }
        }
    }
}
