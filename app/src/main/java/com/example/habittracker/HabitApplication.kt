package com.example.habittracker

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import java.io.File

import android.app.Application
import com.example.habittracker.data.HabitDatabase
import com.example.habittracker.data.HabitRepository

/**
 * HabitApplication class which acts as the entry point of the app. Provides lazy loading singletons for the database and repository.
 */
class HabitApplication : Application() {

    // Lazily instantiate the database.
    val database by lazy { HabitDatabase.getDatabase(this) }

    // DataStore for app settings
    val dataStore: DataStore<Preferences> by lazy {
        PreferenceDataStoreFactory.create {
            File(applicationContext.filesDir, "settings.preferences_pb")
        }
    }
    val repository by lazy { HabitRepository(database.habitDao()) }
}
