package com.example.habittracker.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.habittracker.data.AuthRepository
import com.google.firebase.auth.FirebaseUser

class ProfileViewModel(private val authRepository: AuthRepository = AuthRepository()) : ViewModel() {

    private val _user = MutableLiveData<FirebaseUser?>()
    val user: LiveData<FirebaseUser?> get() = _user

    init {
        // Load current user
        _user.value = authRepository.getCurrentUser()
    }

    fun getEmail(): String? = _user.value?.email

    fun getDisplayName(): String? = _user.value?.displayName

    fun signOut() {
        authRepository.signOut()
        _user.value = null
    }

    fun resetPassword(email: String) = authRepository.resetPassword(email)
}
