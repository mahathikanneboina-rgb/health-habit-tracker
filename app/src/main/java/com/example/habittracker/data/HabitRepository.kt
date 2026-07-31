package com.example.habittracker.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * Repository class that abstracts access to the Room database.
 * Serves as the mediator between the ViewModel and the DAO.
 * Ensures all synchronous database operations are executed on a background thread.
 */
class HabitRepository(private val habitDao: HabitDao) {

    /**
     * Exposes all habits from the database as a Flow.
     * This provides a stream of updates to observers, keeping layouts reactively fresh.
     */
    val allHabits: Flow<List<Habit>> = habitDao.getAllHabits()

    /**
     * Fetch a habit by its ID from the database.
     * Executed asynchronously on the Dispatchers.IO dispatcher.
     */
    suspend fun getHabitById(id: String): Habit? = withContext(Dispatchers.IO) {
        habitDao.getHabitById(id)
    }

    /**
     * Add a habit to the database.
     * Executed asynchronously on the Dispatchers.IO dispatcher.
     */
    suspend fun addHabit(habit: Habit): Unit = withContext(Dispatchers.IO) {
        habitDao.insertHabit(habit)
    }

    /**
     * Update an existing habit in the database.
     * Executed asynchronously on the Dispatchers.IO dispatcher.
     */
    suspend fun updateHabit(habit: Habit): Unit = withContext(Dispatchers.IO) {
        habitDao.updateHabit(habit)
    }

    /**
     * Delete a habit from the database.
     * Executed asynchronously on the Dispatchers.IO dispatcher.
     */
    suspend fun deleteHabit(habitId: String): Unit = withContext(Dispatchers.IO) {
        habitDao.deleteHabitById(habitId)
    }

    /**
     * Toggle the completion status of a habit in the database.
     * Fetches the current state, flips the completed state, and writes the change.
     * Returns the updated completion status.
     * Executed asynchronously on the Dispatchers.IO dispatcher.
     */
    suspend fun toggleHabitCompletion(habitId: String): Boolean = withContext(Dispatchers.IO) {
        val habit = habitDao.getHabitById(habitId)
        if (habit != null) {
            val updated = habit.copy(isCompleted = !habit.isCompleted)
            habitDao.updateHabit(updated)
            updated.isCompleted
        } else {
            false
        }
    }
}
