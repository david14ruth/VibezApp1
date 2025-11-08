package com.example.vibezapp10

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.vibezapp10.Fragments.HomeFragment
import com.example.vibezapp10.Fragments.LibraryFragment
import com.example.vibezapp10.Fragments.ProfileFragment
import com.example.vibezapp10.Fragments.SearchFragment
import com.example.vibezapp10.Network.SupabaseManager
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.realtime.Realtime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val toolbar: MaterialToolbar = findViewById(R.id.topAppBar)


        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@addOnSuccessListener

            // Save token to Supabase
            CoroutineScope(Dispatchers.IO).launch {
                SupabaseManager.client.from("fcm_tokens")
                    .upsert(mapOf("user_id" to userId, "token" to token))
            }
        }








        // Set default fragment
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
            bottomNav.selectedItemId = R.id.nav_home
        }

        // Bottom navigation listener
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> loadFragment(HomeFragment())
                R.id.nav_search -> loadFragment(SearchFragment())
                R.id.nav_library -> loadFragment(LibraryFragment())
                R.id.nav_profile -> loadFragment(ProfileFragment())
                R.id.nav_player ->{ startActivity(Intent(this, MusicPlayerActivity::class.java))
                    true
                }

                else -> false
            }
        }

        // TopAppBar menu clicks
        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_search -> {
                    bottomNav.selectedItemId = R.id.nav_search
                    true
                }
                R.id.action_notifications -> {
                    // You could open a NotificationsActivity or fragment
                    true
                }
                R.id.action_settings -> {
                    // Example: open SettingsActivity
                    // startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }


    private fun loadFragment(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
        return true
    }
}
