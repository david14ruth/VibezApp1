package com.example.vibezapp10.Fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.vibezapp10.EditProfileActivity
import com.example.vibezapp10.Models.Profile
import com.example.vibezapp10.R
import com.example.vibezapp10.SignInActivity
import com.example.vibezapp10.Network.SupabaseManager
import com.google.firebase.auth.FirebaseAuth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class ProfileFragment : Fragment() {

    private lateinit var profileImage: ImageView
    private lateinit var userNameText: TextView
    private lateinit var userEmailText: TextView
    private lateinit var btnEditProfile: Button
    private lateinit var btnSignIn: Button
    private lateinit var logoutCard: View
    private lateinit var btnSettings: ImageButton
    private lateinit var firebaseAuth: FirebaseAuth

    private val profilesTable = SupabaseManager.client.from("profiles")

    private val editProfileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            loadProfile()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rootLayout = view.findViewById<View>(R.id.profile_root)
        updateBackgroundForTheme(rootLayout)


        profileImage = view.findViewById(R.id.profile_image)
        userNameText = view.findViewById(R.id.user_name)
        userEmailText = view.findViewById(R.id.user_email)
        btnEditProfile = view.findViewById(R.id.btn_edit_profile)
        btnSignIn = view.findViewById(R.id.btn_sign_in)
        logoutCard = view.findViewById(R.id.logout_card)
        btnSettings = view.findViewById(R.id.btn_settings)
        firebaseAuth = FirebaseAuth.getInstance()

        loadProfile()

        btnEditProfile.setOnClickListener {
            val intent = Intent(requireContext(), EditProfileActivity::class.java)
            editProfileLauncher.launch(intent)
        }

        btnSettings.setOnClickListener {
            openSettingsDialog()
        }

        btnSignIn.setOnClickListener {
            startActivity(Intent(requireContext(), SignInActivity::class.java))
        }

        logoutCard.setOnClickListener {
            startActivity(Intent(requireContext(), SignInActivity::class.java))
            requireActivity().finish()
        }

        val profle = view.findViewById<TextView>(R.id.Profile)
        val edit_profile = view.findViewById<TextView>(R.id.btn_edit_profile)
        val logout = view.findViewById<TextView>(R.id.Logout)


        val xhosa = view.findViewById<TextView>(R.id.Xhosa)
        val english = view.findViewById<TextView>(R.id.English)
        val afrikkans = view.findViewById<TextView>(R.id.Afrikkans)

        xhosa.setOnClickListener(){
            profle.setText("Iprofayili")
            edit_profile.setText("Hlengahlengisa iprofayile yakho")
            logout.setText("uluhlu lokudlala")
        }
        english.setOnClickListener(){
            profle.setText("Profile")
            edit_profile.setText("Edit Profile")
            logout.setText("Logout")
        }
        afrikkans.setOnClickListener(){
            profle.setText("Profiel")
            edit_profile.setText("Wysig-profiel")
            logout.setText("Teken uit")
        }
    }
    private fun updateBackgroundForTheme(rootView: View) {
        val isDarkMode = when (AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.MODE_NIGHT_YES -> true
            AppCompatDelegate.MODE_NIGHT_NO -> false
            else -> (resources.configuration.uiMode and
                    android.content.res.Configuration.UI_MODE_NIGHT_MASK) ==
                    android.content.res.Configuration.UI_MODE_NIGHT_YES
        }

        val backgroundColor = if (isDarkMode) {
            ContextCompat.getColor(requireContext(), R.color.background_primary)
        } else {
            ContextCompat.getColor(requireContext(), R.color.background_primary)
        }

        rootView.setBackgroundColor(backgroundColor)
        rootView.animate().alpha(0f).setDuration(150).withEndAction {
            rootView.setBackgroundColor(backgroundColor)
            rootView.animate().alpha(1f).setDuration(150).start()
        }.start()

    }


    private fun loadProfile() {
        val userId = firebaseAuth.currentUser?.uid ?: run {
            btnSignIn.visibility = View.VISIBLE
            profileImage.visibility = View.GONE
            userNameText.visibility = View.GONE
            userEmailText.visibility = View.GONE
            btnEditProfile.visibility = View.GONE
            logoutCard.visibility = View.GONE
            btnSettings.visibility = View.GONE
            return
        }

        btnSignIn.visibility = View.GONE
        profileImage.visibility = View.VISIBLE
        userNameText.visibility = View.VISIBLE
        userEmailText.visibility = View.VISIBLE
        btnEditProfile.visibility = View.VISIBLE
        logoutCard.visibility = View.VISIBLE
        btnSettings.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = profilesTable.select(columns = Columns.ALL) { filter { eq("id", userId) } }
                val profileList = result.decodeList<Profile>()
                val profile = profileList.firstOrNull()

                withContext(Dispatchers.Main) {
                    profile?.let {
                        userNameText.text = it.full_name
                        userEmailText.text = it.email
                        if (!it.avatar_url.isNullOrEmpty()) {
                            Glide.with(this@ProfileFragment)
                                .load(it.avatar_url)
                                .placeholder(R.drawable.ic_person)
                                .into(profileImage)
                        } else {
                            profileImage.setImageResource(R.drawable.ic_person)
                        }
                        // Apply theme immediately
                        applyTheme(it.theme)

                        // Apply language immediately
                        applyLanguage(it.language)
                    } ?: run {
                        userNameText.text = "User"
                        userEmailText.text = "No Email"
                        profileImage.setImageResource(R.drawable.ic_person)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun openSettingsDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_settings, null)
        val themeSpinner = dialogView.findViewById<Spinner>(R.id.spinner_theme)
        val languageSpinner = dialogView.findViewById<Spinner>(R.id.spinner_language)

        val userId = firebaseAuth.currentUser?.uid ?: return

        // Load current settings
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = profilesTable.select { filter { eq("id", userId) } }
                val profileList = result.decodeList<Profile>()
                val profile = profileList.firstOrNull()

                withContext(Dispatchers.Main) {
                    profile?.let {
                        themeSpinner.setSelection(getThemeIndex(it.theme))
                        languageSpinner.setSelection(getLanguageIndex(it.language))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Settings")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val selectedTheme = themeSpinner.selectedItem.toString()
                val selectedLanguage = languageSpinner.selectedItem.toString()

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val currentResult = profilesTable.select { filter { eq("id", userId) } }
                        val currentProfile = currentResult.decodeList<Profile>().firstOrNull() ?: return@launch

                        val updatedProfile = Profile(
                            id = userId,
                            full_name = currentProfile.full_name,
                            email = currentProfile.email,
                            avatar_url = currentProfile.avatar_url,
                            theme = selectedTheme,
                            notifications_enabled = currentProfile.notifications_enabled,
                            language = selectedLanguage
                        )

                        profilesTable.upsert(updatedProfile)

                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), "Settings updated", Toast.LENGTH_SHORT).show()
                            // Apply settings live
                            applyTheme(selectedTheme)
                            applyLanguage(selectedLanguage)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), "Failed to update settings: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    // Apply theme immediately
    private fun applyTheme(theme: String) {
        val mode = when (theme.lowercase(Locale.ROOT)) {
            "dark" -> AppCompatDelegate.MODE_NIGHT_YES
            "light" -> AppCompatDelegate.MODE_NIGHT_NO
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }

        if (AppCompatDelegate.getDefaultNightMode() != mode) {
            AppCompatDelegate.setDefaultNightMode(mode)
            view?.let { updateBackgroundForTheme(it) }
        }
    }


    // Apply language immediately
    private fun applyLanguage(language: String) {
        val locale = when (language.lowercase()) {
            "afrikaans" -> Locale("af")
            "zulu" -> Locale("zu")
            else -> Locale("en")
        }

        val context = requireContext()
        val config = context.resources.configuration
        config.setLocale(locale)
        val localizedContext = context.createConfigurationContext(config)
        // You can now use localizedContext to get strings
        // e.g., localizedContext.getString(R.string.dashboard)


    }

    private fun getThemeIndex(theme: String) = when (theme.lowercase()) {
        "light" -> 0
        "dark" -> 1
        else -> 0
    }

    private fun getLanguageIndex(language: String) = when (language.lowercase()) {
        "english" -> 0
        "afrikaans" -> 1
        "zulu" -> 2
        else -> 0
    }
}
