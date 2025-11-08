package com.example.vibezapp10

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.vibezapp10.Models.Profile
import com.example.vibezapp10.Network.SupabaseManager
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignUpActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private val profilesTable = SupabaseManager.client.postgrest.from("profiles")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        FirebaseApp.initializeApp(this)
        firebaseAuth = FirebaseAuth.getInstance()

        val emailInput = findViewById<TextInputEditText>(R.id.email_input)
        val passwordInput = findViewById<TextInputEditText>(R.id.password_input)
        val confirmPasswordInput = findViewById<TextInputEditText>(R.id.confirm_password_input)
        val signUpButton = findViewById<Button>(R.id.btn_sign_up)
        val backToLoginButton = findViewById<Button>(R.id.btn_back_to_login)

        signUpButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val confirmPassword = confirmPasswordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Firebase sign-up
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = firebaseAuth.currentUser
                        user?.let {
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    val profile = Profile(
                                        id = it.uid,
                                        full_name = "User",
                                        email = email,
                                        avatar_url = "",
                                        theme = "light",
                                        notifications_enabled = true,
                                        language = "en"
                                    )

                                    profilesTable.upsert(profile)

                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(
                                            this@SignUpActivity,
                                            "Sign-up successful!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        startActivity(Intent(this@SignUpActivity, MainActivity::class.java))
                                        finish()
                                    }
                                } catch (e: Exception) {
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(
                                            this@SignUpActivity,
                                            "Failed to save profile: ${e.message}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            }
                        }
                    } else {
                        Toast.makeText(
                            this,
                            "Sign-up failed: ${task.exception?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }

        backToLoginButton.setOnClickListener {
            finish()
        }
    }
}
