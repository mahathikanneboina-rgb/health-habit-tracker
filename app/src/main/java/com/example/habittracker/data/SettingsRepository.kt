package com.example.habittracker.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repository for managing app settings using Jetpack DataStore (Preferences).
 * Handles dark mode and reminder notification preferences.
 */
class SettingsRepository(private val dataStore: DataStore<Preferences>) {

    private companion object {
        val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
        val NOTIFICATIONS_KEY = booleanPreferencesKey("notifications_enabled")
    }

    // Expose current preferences as Flows
    val darkModeFlow: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[DARK_MODE_KEY] ?: false // default: light mode
    }

    val notificationsEnabledFlow: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[NOTIFICATIONS_KEY] ?: true // default: notifications enabled
    }

    suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[DARK_MODE_KEY] = enabled
        }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[NOTIFICATIONS_KEY] = enabled
        }
    }
}
