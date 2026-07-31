package com.example.habittracker

import android.app.Application
import com.example.habittracker.data.HabitDatabase
import com.example.habittracker.data.HabitRepository

/**
 * HabitApplication class which acts as the entry point of the app.
 * Provides lazy loading singletons for the database and repository.
 */
class HabitApplication : Application() {

    // Lazily instantiate the database.
    val database by lazy { HabitDatabase.getDatabase(this) }

    // Lazily instantiate the repository.
    val repository by lazy { HabitRepository(database.habitDao()) }
}

