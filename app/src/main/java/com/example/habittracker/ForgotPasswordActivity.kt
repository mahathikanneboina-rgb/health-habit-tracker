package com.example.habittracker

import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.example.habittracker.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                Toast.makeText(this, getString(R.string.reset_link_sent), Toast.LENGTH_LONG).show()
                finish()
            }
        }

        // Navigate back to Login
        binding.tvBackToLogin.setOnClickListener {
            finish()
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
