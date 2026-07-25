package com.example.habittracker

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.example.habittracker.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        // Clear errors as user types
        binding.etEmail.doOnTextChanged { _, _, _, _ ->
            binding.tilEmail.error = null
        }

        binding.etPassword.doOnTextChanged { _, _, _, _ ->
            binding.tilPassword.error = null
        }

        // Handle Login button click
        binding.btnLogin.setOnClickListener {
            if (validateInputs()) {
                Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        // Navigate to Register Activity
        binding.tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Navigate to Forgot Password Activity
        binding.tvForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        val email = binding.etEmail.text?.toString()?.trim().orEmpty()
        val password = binding.etPassword.text?.toString()?.trim().orEmpty()

        // Email Validation
        if (email.isEmpty()) {
            binding.tilEmail.error = getString(R.string.error_empty_email)
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = getString(R.string.error_invalid_email)
            isValid = false
        } else {
            binding.tilEmail.error = null
        }

        // Password Validation
        if (password.isEmpty()) {
            binding.tilPassword.error = getString(R.string.error_empty_password)
            isValid = false
        } else {
            binding.tilPassword.error = null
        }

        return isValid
    }
}
