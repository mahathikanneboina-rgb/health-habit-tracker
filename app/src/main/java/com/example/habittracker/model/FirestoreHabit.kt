package com.example.habittracker.model

import com.example.habittracker.data.Habit

/**
 * Data class representing a habit document stored in Firebase Firestore.
 * Field names match the Firestore structure defined in the requirements.
 */
data class FirestoreHabit(
    var habitId: String = "",
    val name: String = "",
    val category: String = "",
    val dailyGoal: String = "",
    val reminderTime: String = "",
    val notes: String = "",
    val reminderEnabled: Boolean = false,
    val isCompleted: Boolean = false
) {
    /** Convert this Firestore model to the local Room [Habit] entity. */
    fun toRoomHabit(): Habit = Habit(
        id = if (habitId.isNotEmpty()) habitId else java.util.UUID.randomUUID().toString(),
        name = name,
        category = category,
        dailyGoal = dailyGoal,
        reminderTime = reminderTime,
        notes = notes,
        reminderEnabled = reminderEnabled,
        isCompleted = isCompleted
    )

    companion object {
        /** Create a [FirestoreHabit] from a Room [Habit] entity. */
        fun fromRoomHabit(habit: Habit): FirestoreHabit = FirestoreHabit(
            habitId = habit.id,
            name = habit.name,
            category = habit.category,
            dailyGoal = habit.dailyGoal,
            reminderTime = habit.reminderTime,
            notes = habit.notes,
            reminderEnabled = habit.reminderEnabled,
            isCompleted = habit.isCompleted
        )
    }
}
