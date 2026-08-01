package com.example.habittracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.habittracker.data.SettingsRepository
import kotlinx.coroutines.launch

/**
 * ViewModel exposing app settings stored in DataStore.
 */
class SettingsViewModel(private val settingsRepository: SettingsRepository) : ViewModel() {
    // Expose preferences as LiveData for UI observation
    val darkMode = settingsRepository.darkModeFlow.asLiveData()
    val notificationsEnabled = settingsRepository.notificationsEnabledFlow.asLiveData()

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setDarkMode(enabled) }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setNotificationsEnabled(enabled) }
    }
}
