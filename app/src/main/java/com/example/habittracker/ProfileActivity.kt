package com.example.habittracker

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.habittracker.databinding.ActivityProfileBinding
import com.example.habittracker.viewmodel.ProfileViewModel

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Observe user data
        viewModel.user.observe(this) { user ->
            if (user != null) {
                binding.tvProfileName.text = user.displayName ?: "User"
                binding.tvProfileEmail.text = user.email ?: ""
            } else {
                // No user signed in, redirect to login
                navigateToLogin()
            }
        }

        // Set button listeners
        binding.btnLogout.setOnClickListener {
            viewModel.signOut()
            Toast.makeText(this, getString(R.string.logout_success), Toast.LENGTH_SHORT).show()
            navigateToLogin()
        }

        binding.btnChangePassword.setOnClickListener {
            val email = viewModel.getEmail()
            if (email != null) {
                viewModel.resetPassword(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, getString(R.string.reset_link_sent), Toast.LENGTH_LONG).show()
                        } else {
                            val error = task.exception?.localizedMessage ?: "Failed to send reset email"
                            Toast.makeText(this, error, Toast.LENGTH_LONG).show()
                        }
                    }
            } else {
                Toast.makeText(this, "No email available", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnEditProfile.setOnClickListener {
            // Placeholder for edit profile functionality
            Toast.makeText(this, "Edit Profile not implemented yet", Toast.LENGTH_SHORT).show()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
