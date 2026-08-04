package com.example.habittracker.repository

import com.example.habittracker.data.Habit
import com.example.habittracker.model.FirestoreHabit
import com.example.habittracker.util.FirestoreHelper
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Low‑level repository that talks directly to Firebase Firestore.
 * All operations are performed on the IO dispatcher and use Tasks.await
 * to bridge the Task‑based Firebase API with Kotlin coroutines.
 */
class FirestoreRepository {

    private val db = FirestoreHelper.db
    private fun habitsCollection(uid: String): CollectionReference =
        db.collection("users").document(uid).collection("habits")

    /** Fetch all habits for the given user UID. */
    suspend fun fetchHabits(uid: String): List<FirestoreHabit> = withContext(Dispatchers.IO) {
        try {
            val snapshot = Tasks.await(habitsCollection(uid).get())
            snapshot.documents.mapNotNull { doc ->
                // Convert Firestore document to our model and set the document ID
                doc.toObject(FirestoreHabit::class.java)?.apply { habitId = doc.id }
            }
        } catch (e: Exception) {
            // On error, return empty list – UI can handle sync failure separately
            emptyList()
        }
    }

    /** Add a new habit to Firestore for the given user UID. */
    suspend fun addHabit(uid: String, habit: Habit) = withContext(Dispatchers.IO) {
        try {
            val firestoreHabit = FirestoreHabit.fromRoomHabit(habit)
            // Use local habit.id as Firestore document ID to keep them in sync
            Tasks.await(habitsCollection(uid).document(habit.id).set(firestoreHabit))
        } catch (e: Exception) {
            // Swallow or log; syncing errors are shown by the ViewModel
        }
    }

    /** Update an existing habit document in Firestore. */
    suspend fun updateHabit(uid: String, habit: Habit) = withContext(Dispatchers.IO) {
        try {
            val firestoreHabit = FirestoreHabit.fromRoomHabit(habit)
            Tasks.await(habitsCollection(uid).document(habit.id).set(firestoreHabit))
        } catch (e: Exception) {
            // Handle errors as needed
        }
    }

    /** Delete a habit document from Firestore. */
    suspend fun deleteHabit(uid: String, habitId: String) = withContext(Dispatchers.IO) {
        try {
            Tasks.await(habitsCollection(uid).document(habitId).delete())
        } catch (e: Exception) {
            // Handle errors as needed
        }
    }
}
