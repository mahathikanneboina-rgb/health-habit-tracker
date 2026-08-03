package com.example.habittracker

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.habittracker.data.HabitDatabase
import com.example.habittracker.data.HabitRepository
import com.example.habittracker.util.NotificationHelper
import com.google.firebase.auth.FirebaseAuth

/**
 * Application class for Health Habit Tracker.
 * Provides singletons for the Room database, repository, and DataStore.
 */
class HabitApplication : Application() {

    // Lazy initialization of the Room database.
    val database: HabitDatabase by lazy { HabitDatabase.getDatabase(this) }

    // Repository that abstracts data access.
    val repository: HabitRepository by lazy { HabitRepository(database.habitDao()) }

    // DataStore for simple key‑value settings.
    val dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    override fun onCreate() {
        super.onCreate()
        // Create notification channel for reminder notifications.
        NotificationHelper.createNotificationChannel(this)

        // Disable app verification for testing to bypass reCAPTCHA Enterprise requirements on debug builds
        try {
            FirebaseAuth.getInstance().firebaseAuthSettings.setAppVerificationDisabledForTesting(true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
