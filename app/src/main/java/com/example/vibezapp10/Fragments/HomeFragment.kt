package com.example.vibezapp10.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vibezapp10.Adapters.SongAdapter
import com.example.vibezapp10.Models.Profile
import com.example.vibezapp10.MusicPlayerActivity
import com.example.vibezapp10.Models.Song
import com.example.vibezapp10.Network.SupabaseManager
import com.example.vibezapp10.R
import com.example.vibezapp10.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val songsList = mutableListOf<Song>()
    private lateinit var songAdapter: SongAdapter
    private lateinit var firebaseAuth: FirebaseAuth



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        fetchProfileName()
        setupSongsRecycler()
        fetchSongs()

        val dashboard = view.findViewById<TextView>(R.id.dashboard_title)
        val welcome = view.findViewById<TextView>(R.id.welcome_text1)
        val song = view.findViewById<TextView>(R.id.songs_title)
        val music = view.findViewById<TextView>(R.id.Music_Streaming)
        val favorites = view.findViewById<TextView>(R.id.Favorites)

        val playlist =  view.findViewById<Button>(R.id.btn_playlists)
        val share =  view.findViewById<Button>(R.id.btn_share)

        val xhosa = view.findViewById<TextView>(R.id.Xhosa)
        val english = view.findViewById<TextView>(R.id.English)
        val afrikkans = view.findViewById<TextView>(R.id.Afrikkans)

        xhosa.setOnClickListener(){
            dashboard.setText("kwideshibhodi")
            welcome.setText("Wamkelekile")
            playlist.setText("uluhlu lokudlala")
            share.setText("ukwabelana")
            song.setText("Iingoma ezikhoyo")
            music.setText("Umculo_Ukusasaza")
            favorites.setText("Ezizithandayo")
        }
        english.setOnClickListener(){
            dashboard.setText("Dashboard")
            welcome.setText("Welcome")
            playlist.setText("Playlist")
            share.setText("Share")
            song.setText("Featured Songs")
            music.setText("Music Streaming")
            favorites.setText("Favorites")
        }
        afrikkans.setOnClickListener(){
            dashboard.setText("Dashboard")
            welcome.setText("Welkom")
            playlist.setText("Snitlys")
            share.setText("Deel")
            song.setText("Uitstalliedjies")
            music.setText("Musiek_Stroom")
            favorites.setText("GUnstelinge")
        }

    }

    private fun fetchProfileName() {
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                Log.d("ProfileFetch", "Fetching profile for user: ${firebaseUser.uid}")

                // ✅ Fetch all profiles and decode using your Profile data class
                val profiles = SupabaseManager.client
                    .from("profiles")
                    .select()
                    .decodeList<Profile>()


                val profile = profiles.firstOrNull { it.id == firebaseUser.uid }

                withContext(Dispatchers.Main) {
                    if (profile != null) {
                        Log.d("ProfileFetch", "Profile found: ${profile.full_name}")
                        binding.welcomeText.text = " ${profile.full_name} 👋"
                    } else {
                        Log.w("ProfileFetch", "Profile not found for UID: ${firebaseUser.uid}")
                        Toast.makeText(requireContext(), "Profile not found", Toast.LENGTH_SHORT).show()
                    }
                }

            } catch (e: Exception) {
                Log.e("ProfileFetch", "Error fetching profile", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Failed to load profile", Toast.LENGTH_SHORT).show()
                }
            }
        }
        }

    private fun setupSongsRecycler() {
        songAdapter = SongAdapter(
            songs = songsList,
            onSongClick = { song ->
                // Open MusicPlayerActivity
                val intent = MusicPlayerActivity.newIntent(requireContext(), song.id.toString())
                startActivity(intent)
            },
            onFavoriteClick = { song ->
                // Optional: implement favorite logic
                Toast.makeText(requireContext(), "${song.title} favorited!", Toast.LENGTH_SHORT).show()
            }
        )

        binding.songsRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = songAdapter
        }
    }

    private fun fetchSongs() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Fetch and decode songs directly
                val songs: List<Song> = SupabaseManager.client
                    .from("song")
                    .select()       // "*" is optional
                    .decodeList()   // <-- decode into List<Song>


                withContext(Dispatchers.Main) {
                    songsList.clear()
                    songsList.addAll(songs)
                    songAdapter.notifyDataSetChanged()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Failed to load songs", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
