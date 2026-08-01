package com.example.habittracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for the habits table.
 * Provides SQLite database operations for the habit tracker app.
 */
@Dao
interface HabitDao {

    /**
     * Retrieve all habits from the database, ordered by ID descending.
     * Returns a Flow to enable reactive updates in the UI when database content changes.
     */
    @Query("SELECT * FROM habits ORDER BY id DESC")
    fun getAllHabits(): Flow<List<Habit>>

    /**
     * Retrieve a specific habit by its unique ID.
     */
    @Query("SELECT * FROM habits WHERE id = :id LIMIT 1")
    fun getHabitById(id: String): Habit?

    /**
     * Insert a new habit or replace an existing one on conflict.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHabit(habit: Habit): Long

    /**
     * Update an existing habit.
     */
    @Update
    fun updateHabit(habit: Habit): Int

    /**
     * Delete a habit by its ID.
     */
    @Query("DELETE FROM habits WHERE id = :id")
    fun deleteHabitById(id: String): Int
    // New method to delete all habits – used when syncing from Firestore
    @Query("DELETE FROM habits")
    suspend fun deleteAllHabits()

    }
