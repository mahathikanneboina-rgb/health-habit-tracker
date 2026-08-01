package com.example.habittracker.data

import androidx.lifecycle.LiveData
import com.example.habittracker.data.HabitDao
import com.example.habittracker.data.Habit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * Repository class that abstracts access to the Room database.
 * Serves as the mediator between the ViewModel and the DAO.
 * Ensures all synchronous database operations are executed on a background thread.
 */
class HabitRepository(private val habitDao: HabitDao) {

    // New method to sync local DB from Firestore
    suspend fun syncFromFirestore(uid: String) = withContext(Dispatchers.IO) {
        // Clear existing habits
        habitDao.deleteAllHabits()
        // Fetch from Firestore
        val firestoreHabits = com.example.habittracker.repository.FirestoreRepository().fetchHabits(uid)
        // Insert each habit into local DB
        firestoreHabits.forEach { habitDao.insertHabit(it.toRoomHabit()) }
    }

    // Update addHabit to also upload to Firestore
    suspend fun addHabit(habit: Habit) = withContext(Dispatchers.IO) {
        habitDao.insertHabit(habit)
        // Firestore upload (if user is logged in)
        val uid = com.example.habittracker.util.FirestoreHelper.currentUserId()
        uid?.let { com.example.habittracker.repository.FirestoreRepository().addHabit(it, habit) }
    }

    // Update updateHabit to also update Firestore
    suspend fun updateHabit(habit: Habit) = withContext(Dispatchers.IO) {
        habitDao.updateHabit(habit)
        val uid = com.example.habittracker.util.FirestoreHelper.currentUserId()
        uid?.let { com.example.habittracker.repository.FirestoreRepository().updateHabit(it, habit) }
    }

    // Update deleteHabit to also delete from Firestore
    suspend fun deleteHabit(habitId: String) = withContext(Dispatchers.IO) {
        habitDao.deleteHabitById(habitId)
        val uid = com.example.habittracker.util.FirestoreHelper.currentUserId()
        uid?.let { com.example.habittracker.repository.FirestoreRepository().deleteHabit(it, habitId) }
    }

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

    // Deprecated simple CRUD methods removed to avoid duplication. Sync-aware methods above now handle both local and Firestore operations.


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
