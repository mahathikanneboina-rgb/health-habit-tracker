package com.example.habittracker

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.example.habittracker.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.UserProfileChangeRequest

/**
 * RegisterActivity handles new user registration using Firebase Authentication.
 */
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        setupListeners()
    }

    private fun setupListeners() {
        // Clear errors as user types
        binding.etName.doOnTextChanged { _, _, _, _ ->
            binding.tilName.error = null
        }

        binding.etEmail.doOnTextChanged { _, _, _, _ ->
            binding.tilEmail.error = null
        }

        binding.etPassword.doOnTextChanged { _, _, _, _ ->
            binding.tilPassword.error = null
        }

        binding.etConfirmPassword.doOnTextChanged { _, _, _, _ ->
            binding.tilConfirmPassword.error = null
        }

        // Register button click
        binding.btnRegister.setOnClickListener {
            if (validateInputs()) {
                val name = binding.etName.text.toString().trim()
                val email = binding.etEmail.text.toString().trim()
                val password = binding.etPassword.text.toString().trim()

                registerWithFirebase(name, email, password)
            }
        }

        // Navigate back to Login
        binding.tvLogin.setOnClickListener {
            finish()
        }
    }

    /**
     * Create account using Firebase Authentication.
     */
    private fun registerWithFirebase(name: String, email: String, password: String) {
        binding.btnRegister.isEnabled = false

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    // Update user display name in Firebase Auth profile
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()
                    user?.updateProfile(profileUpdates)

                    Toast.makeText(this, getString(R.string.registration_success_login), Toast.LENGTH_LONG).show()

                    // Redirect to Login screen
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    binding.btnRegister.isEnabled = true
                    val exception = task.exception
                    val errorMessage = when (exception) {
                        is FirebaseAuthUserCollisionException -> "An account with this email address already exists."
                        else -> exception?.localizedMessage ?: "Registration failed. Please try again."
                    }
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
    }

    /**
     * Input validation logic for Name, Email, Password and Confirm Password fields.
     */
    private fun validateInputs(): Boolean {
        var isValid = true

        val name = binding.etName.text?.toString()?.trim().orEmpty()
        val email = binding.etEmail.text?.toString()?.trim().orEmpty()
        val password = binding.etPassword.text?.toString()?.trim().orEmpty()
        val confirmPassword = binding.etConfirmPassword.text?.toString()?.trim().orEmpty()

        // Name Validation
        if (name.isEmpty()) {
            binding.tilName.error = getString(R.string.error_empty_name)
            isValid = false
        } else {
            binding.tilName.error = null
        }

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
        } else if (password.length < 6) {
            binding.tilPassword.error = getString(R.string.error_password_too_short)
            isValid = false
        } else {
            binding.tilPassword.error = null
        }

        // Confirm Password Validation
        if (confirmPassword.isEmpty()) {
            binding.tilConfirmPassword.error = getString(R.string.error_empty_confirm_password)
            isValid = false
        } else if (confirmPassword != password) {
            binding.tilConfirmPassword.error = getString(R.string.error_password_mismatch)
            isValid = false
        } else {
            binding.tilConfirmPassword.error = null
        }

        return isValid
    }
}
