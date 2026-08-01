package com.example.habittracker.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest

class AuthRepository(private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()) {

    fun signIn(email: String, password: String) =
        firebaseAuth.signInWithEmailAndPassword(email, password)

    fun createUser(name: String, email: String, password: String) =
        firebaseAuth.createUserWithEmailAndPassword(email, password).continueWithTask { task ->
            if (task.isSuccessful) {
                val user = firebaseAuth.currentUser
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()
                user?.updateProfile(profileUpdates)
            }
            task
        }

    fun resetPassword(email: String) = firebaseAuth.sendPasswordResetEmail(email)

    fun updateDisplayName(name: String) =
        firebaseAuth.currentUser?.updateProfile(
            com.google.firebase.auth.UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
        )

    fun getCurrentUser(): FirebaseUser? = firebaseAuth.currentUser
    fun signOut() = firebaseAuth.signOut()
}
