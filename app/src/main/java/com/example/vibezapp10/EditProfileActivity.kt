package com.example.vibezapp10

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.vibezapp10.Models.Profile
import com.example.vibezapp10.Network.SupabaseManager
import com.google.firebase.auth.FirebaseAuth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream

class EditProfileActivity : AppCompatActivity() {

    private lateinit var profileImageView: ImageView
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var btnSave: Button
    private lateinit var firebaseAuth: FirebaseAuth

    private val profilesTable = SupabaseManager.client.from("profiles")
    private var avatarUri: Uri? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            avatarUri = it
            profileImageView.setImageURI(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        profileImageView = findViewById(R.id.profile_image)
        nameEditText = findViewById(R.id.edit_name)
        emailEditText = findViewById(R.id.edit_email)
        btnSave = findViewById(R.id.btn_save_profile)
        firebaseAuth = FirebaseAuth.getInstance()

        loadUserProfile()

        profileImageView.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        btnSave.setOnClickListener {
            saveProfile()
        }
    }

    private fun loadUserProfile() {
        val userId = firebaseAuth.currentUser?.uid ?: return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = profilesTable.select { filter { eq("id", userId) } }
                val profileList = result.decodeList<Profile>() // <-- decode as data class
                val profile = profileList.firstOrNull()

                withContext(Dispatchers.Main) {
                    nameEditText.setText(profile?.full_name ?: "")
                    emailEditText.setText(profile?.email ?: "")
                    val avatarUrl = profile?.avatar_url
                    if (!avatarUrl.isNullOrEmpty()) {
                        Glide.with(this@EditProfileActivity)
                            .load(avatarUrl)
                            .placeholder(R.drawable.ic_person)
                            .into(profileImageView)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditProfileActivity, "Failed to load profile", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveProfile() {
        val userId = firebaseAuth.currentUser?.uid ?: return
        val updatedName = nameEditText.text.toString().trim()
        val updatedEmail = emailEditText.text.toString().trim()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Load current profile first to preserve other fields
                val currentResult = profilesTable.select { filter { eq("id", userId) } }
                val currentProfile = currentResult.decodeList<Profile>().firstOrNull()

                // Upload avatar if selected
                var avatarUrl = currentProfile?.avatar_url ?: ""
                avatarUri?.let { uri ->
                    val inputStream = contentResolver.openInputStream(uri)
                    inputStream?.let { stream ->
                        val bytes = stream.readBytes()
                        val fileName = "$userId.jpg"

                        val uploadResponse = SupabaseManager.client.storage
                            .from("avatars")
                            .upload(fileName, bytes)

                        if (uploadResponse.isNotEmpty()) {
                            avatarUrl = SupabaseManager.client.storage
                                .from("avatars")
                                .publicUrl(fileName)

                        } else {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@EditProfileActivity,
                                    "Failed to upload avatar",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }

                // Create updated profile object
                val updatedProfile = Profile(
                    id = userId,
                    full_name = updatedName,
                    email = updatedEmail,
                    avatar_url = avatarUrl,
                    theme = currentProfile?.theme ?: "default",
                    notifications_enabled = currentProfile?.notifications_enabled ?: true,
                    language = currentProfile?.language ?: "en"
                )

                // Upsert profile in Supabase
                profilesTable.upsert(updatedProfile)

                // Update Firebase email if changed
                if (firebaseAuth.currentUser?.email != updatedEmail) {
                    firebaseAuth.currentUser?.updateEmail(updatedEmail)?.addOnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Toast.makeText(
                                this@EditProfileActivity,
                                "Failed to update Firebase email: ${task.exception?.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditProfileActivity, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditProfileActivity, "Failed to update profile: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }


}
