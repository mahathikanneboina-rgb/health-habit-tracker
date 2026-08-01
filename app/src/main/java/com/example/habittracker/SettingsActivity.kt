package com.example.habittracker

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.habittracker.databinding.ActivitySettingsBinding
import com.example.habittracker.viewmodel.SettingsViewModel
import com.example.habittracker.viewmodel.SettingsViewModelFactory
import com.example.habittracker.data.SettingsRepository
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    // Initialise ViewModel with SettingsRepository from Application's DataStore
    private val viewModel: SettingsViewModel by viewModels {
        val repo = SettingsRepository((application as com.example.habittracker.HabitApplication).dataStore)
        SettingsViewModelFactory(repo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Observe stored preferences
        viewModel.darkMode.observe(this) { enabled ->
            binding.switchDarkMode.isChecked = enabled
        }
        viewModel.notificationsEnabled.observe(this) { enabled ->
            binding.switchNotifications.isChecked = enabled
        }

        // Dark mode toggle – persist and apply theme instantly
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setDarkMode(isChecked)
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        // Notifications toggle – currently only stores the preference
        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setNotificationsEnabled(isChecked)
        }

        // Change password – send Firebase password‑reset email to the current user
        binding.btnChangePassword.setOnClickListener {
            val email = FirebaseAuth.getInstance().currentUser?.email
            if (email != null) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnSuccessListener {
                        Toast.makeText(this, getString(R.string.reset_link_sent), Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, e.localizedMessage, Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            }
        }

        // Logout – sign out from Firebase and return to Login screen
        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(this, getString(R.string.logout_success), Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
