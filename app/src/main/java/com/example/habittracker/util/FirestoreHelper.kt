package com.example.habittracker.util

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object FirestoreHelper {
    /**
     * Provides a singleton instance of FirebaseFirestore.
     */
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    /**
     * Returns the currently signed‑in user's UID, or null if not signed in.
     */
    fun currentUserId(): String? = FirebaseAuth.getInstance().currentUser?.uid
}
