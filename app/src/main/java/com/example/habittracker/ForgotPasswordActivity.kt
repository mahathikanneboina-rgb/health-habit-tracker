package com.example.habittracker

import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.example.habittracker.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException

/**
 * ForgotPasswordActivity handles password reset request via Firebase Auth.
 */
class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        setupListeners()
    }

    private fun setupListeners() {
        // Clear error as user types
        binding.etEmail.doOnTextChanged { _, _, _, _ ->
            binding.tilEmail.error = null
        }

        // Handle Reset Password click
        binding.btnResetPassword.setOnClickListener {
            if (validateInput()) {
                val email = binding.etEmail.text.toString().trim()
                sendPasswordResetEmail(email)
            }
        }

        // Navigate back to Login
        binding.tvBackToLogin.setOnClickListener {
            finish()
        }
    }

    /**
     * Send password reset email via Firebase.
     */
    private fun sendPasswordResetEmail(email: String) {
        binding.btnResetPassword.isEnabled = false

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener(this) { task ->
                binding.btnResetPassword.isEnabled = true
                if (task.isSuccessful) {
                    Toast.makeText(this, getString(R.string.reset_link_sent), Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    val exception = task.exception
                    val errorMessage = when (exception) {
                        is FirebaseAuthInvalidUserException -> "No account registered with this email."
                        else -> exception?.localizedMessage ?: "Failed to send reset email. Please try again."
                    }
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun validateInput(): Boolean {
        val email = binding.etEmail.text?.toString()?.trim().orEmpty()

        if (email.isEmpty()) {
            binding.tilEmail.error = getString(R.string.error_empty_email)
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = getString(R.string.error_invalid_email)
            return false
        } else {
            binding.tilEmail.error = null
            return true
        }
    }
}
