package com.example.habittracker

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {

    private val _navigateToLogin = MutableLiveData<Boolean>()
    val navigateToLogin: LiveData<Boolean> get() = _navigateToLogin

    private val _navigateToMain = MutableLiveData<Boolean>()
    val navigateToMain: LiveData<Boolean> get() = _navigateToMain

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        viewModelScope.launch {
            delay(SPLASH_DELAY_MS)
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                _navigateToMain.value = true
            } else {
                _navigateToLogin.value = true
            }
        }
    }

    companion object {
        private const val SPLASH_DELAY_MS = 2000L
    }
}
