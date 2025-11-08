package com.example.vibezapp10

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.vibezapp10.Models.Profile
import com.example.vibezapp10.Network.SupabaseManager
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider


class SignInActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        firebaseAuth = FirebaseAuth.getInstance()

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // from google-services.json
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

// Google Sign-In button
        val googleSignInButton = findViewById<Button>(R.id.btn_google_sign_in)
        googleSignInButton.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }








        val emailInput = findViewById<TextInputEditText>(R.id.email_input)
        val passwordInput = findViewById<TextInputEditText>(R.id.password_input)
        val loginButton = findViewById<Button>(R.id.btn_login)
        val signUpButton = findViewById<Button>(R.id.btn_sign_up)

        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firebaseUser = firebaseAuth.currentUser
                        firebaseUser?.uid?.let { uid ->
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    val profilesTable = SupabaseManager.client.from("profiles")

                                    val result = profilesTable.select(columns = Columns.ALL) {
                                        filter {eq("id", uid)
                                        }
                                    }

                                    val profiles = result.decodeList<Profile>()
                                    val profile = profiles.firstOrNull()

                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(
                                            this@SignInActivity,
                                            "Welcome back, ${profile?.full_name ?: "User"}!",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        startActivity(Intent(this@SignInActivity, MainActivity::class.java))
                                        finish()
                                    }
                                } catch (e: Exception) {
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(
                                            this@SignInActivity,
                                            "Failed to fetch profile: ${e.message}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        startActivity(Intent(this@SignInActivity, MainActivity::class.java))
                                        finish()
                                    }
                                }
                            }
                        }
                    } else {
                        Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }

        signUpButton.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    firebaseAuthWithGoogle(account.idToken!!)
                }
            } catch (e: ApiException) {
                Log.d("SignInActivity", "Google Sign-In failed: ${e.message}")
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    user?.let {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val profilesTable = SupabaseManager.client.from("profiles")
                                val profile = Profile(
                                    id = it.uid,
                                    full_name = it.displayName ?: "User",
                                    email = it.email ?: "",
                                    avatar_url = it.photoUrl?.toString() ?: "",
                                    theme = "light",
                                    notifications_enabled = true,
                                    language = "en"
                                )
                                profilesTable.upsert(profile)

                                withContext(Dispatchers.Main) {
                                    Toast.makeText(this@SignInActivity, "Welcome, ${it.displayName ?: "User"}!", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this@SignInActivity, MainActivity::class.java))
                                    finish()
                                }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(this@SignInActivity, "Profile sync failed: ${e.message}", Toast.LENGTH_LONG).show()
                                    Log.d("SignInActivity", "Profile sync failed: ${e.message}")
                                    startActivity(Intent(this@SignInActivity, MainActivity::class.java))
                                    finish()
                                }
                            }
                        }
                    }
                } else {
                    Toast.makeText(this, "Authentication Failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    Log.d("SignInActivity", "Authentication Failed: ${task.exception?.message}")
                }
            }
    }


}
